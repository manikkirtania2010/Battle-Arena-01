package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.example.model.AccountStatus
import com.example.model.User
import com.example.model.Wallet
import com.example.model.Transaction
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Thrown when an account has an active suspension or permanent ban.
 * Preserves the full user profile, permanent Player ID, wallet, and match history.
 */
class AccountRestrictedException(
    val user: User
) : Exception(
    "ACCOUNT_RESTRICTED: Account [${user.playerId}] is ${user.effectiveAccountStatus.displayName}. " +
    "Reason: ${user.banReason?.ifBlank { "Violation of Fair Play rules" } ?: "Violation of Fair Play rules"}. " +
    if (user.effectiveAccountStatus == AccountStatus.TEMPORARILY_SUSPENDED)
        "Expires in: ${user.suspensionRemainingFormatted}. All funds and game history remain securely saved."
    else
        "This ban is permanent. All funds and game history remain securely saved."
)

/**
 * AuthenticationRepository encapsulates all Firebase Authentication operations,
 * Google Sign-In with Android Jetpack Credential Manager, and permanent cloud session management.
 *
 * Guarantees:
 * - Every player retains their permanent Player ID.
 * - Restores wallet, match history, and profile data from cloud upon reinstall / re-login.
 * - Never creates duplicate accounts or overwrites existing user data.
 * - Strictly enforces Admin bans and temporary suspensions on login.
 */
class AuthenticationRepository(
    private val firebaseAuth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Log.w("AuthRepository", "Firebase Auth not available or running in sandbox/offline mode", e)
        null
    },
    val firestoreManager: FirestoreManager = FirestoreManager()
) {

    private val authScope = CoroutineScope(Dispatchers.IO)

    private val _authenticatedUser = MutableStateFlow<User?>(null)
    val authenticatedUser: StateFlow<User?> = _authenticatedUser.asStateFlow()

    private val _lastRestoredWallet = MutableStateFlow<Wallet?>(null)
    val lastRestoredWallet: StateFlow<Wallet?> = _lastRestoredWallet.asStateFlow()

    private val _lastRestoredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val lastRestoredTransactions: StateFlow<List<Transaction>> = _lastRestoredTransactions.asStateFlow()

    private val _lastRestrictedUser = MutableStateFlow<User?>(null)
    val lastRestrictedUser: StateFlow<User?> = _lastRestrictedUser.asStateFlow()

    fun clearLastRestrictedUser() {
        _lastRestrictedUser.value = null
    }

    val currentFirebaseUser: FirebaseUser?
        get() = firebaseAuth?.currentUser

    val isUserAuthenticated: Boolean
        get() = currentFirebaseUser != null || _authenticatedUser.value != null

    init {
        // Listen to Firebase Auth state changes and restore cloud profile idempotently
        firebaseAuth?.addAuthStateListener { auth ->
            val fbUser = auth.currentUser
            if (fbUser != null && _authenticatedUser.value == null) {
                authScope.launch {
                    try {
                        val (user, wallet, txs) = firestoreManager.restoreOrRegisterUser(
                            authUid = fbUser.uid,
                            email = fbUser.email ?: "",
                            name = fbUser.displayName ?: fbUser.email?.substringBefore("@") ?: "Player",
                            ffName = "⚡${(fbUser.displayName ?: fbUser.email?.substringBefore("@") ?: "PLAYER").replace(" ", "_").uppercase()}_FF⚡"
                        )
                        handleLoginResult(user, wallet, txs)
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Failed to restore cloud user on auth state change", e)
                    }
                }
            } else if (fbUser == null && _authenticatedUser.value?.id?.startsWith("BA-G") == false && _authenticatedUser.value?.id?.startsWith("BA-70912") == false) {
                _authenticatedUser.value = null
                _lastRestoredWallet.value = null
                _lastRestoredTransactions.value = emptyList()
            }
        }
    }

    /**
     * Enforces Account Ban and Temporary Suspension rules upon login.
     * Preserves player ID, wallet records, and match history safely in the cloud.
     */
    fun handleLoginResult(user: User, wallet: Wallet, txs: List<Transaction>): Result<User> {
        if (user.isSuspensionActive) {
            _lastRestrictedUser.value = user
            _authenticatedUser.value = null
            try {
                firebaseAuth?.signOut()
            } catch (e: Exception) {
                Log.w("AuthRepository", "Error signing out restricted user", e)
            }
            return Result.failure(AccountRestrictedException(user))
        }

        _lastRestrictedUser.value = null
        _authenticatedUser.value = user
        _lastRestoredWallet.value = wallet
        _lastRestoredTransactions.value = txs
        return Result.success(user)
    }

    /**
     * Signs in using Google via Android Jetpack Credential Manager and links to Firebase.
     * Restores existing player profile and permanent Player ID from Cloud Firestore.
     */
    suspend fun signInWithGoogle(context: Context, serverClientId: String = ""): Result<User> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val resolvedClientId = serverClientId.ifBlank {
                try {
                    context.getString(R.string.default_web_client_id)
                } catch (e: Exception) {
                    "854863485052-d8prs81bsr7dt8qvq0s3s6k9hgpskskt.apps.googleusercontent.com"
                }
            }

            val googleIdOption = GetSignInWithGoogleOption.Builder(resolvedClientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val email = googleIdTokenCredential.id
                val displayName = googleIdTokenCredential.displayName ?: email.substringBefore("@")

                var authUid = "google_${email.hashCode()}"
                if (firebaseAuth != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                    authResult.user?.uid?.let { authUid = it }
                }

                // Restore original Player ID and cloud data without overwriting
                val (user, wallet, txs) = firestoreManager.restoreOrRegisterUser(
                    authUid = authUid,
                    email = email,
                    name = displayName,
                    ffName = "⚡${displayName.replace(" ", "_").uppercase()}_FF⚡"
                )
                handleLoginResult(user, wallet, txs)
            } else {
                Result.failure(Exception("Unsupported credential type returned from Credential Manager"))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Google Sign-In was cancelled by user"))
        } catch (e: GetCredentialException) {
            Log.e("AuthRepository", "Credential Manager returned error: ${e.message}", e)
            Result.failure(Exception("Google Sign-In failed: ${e.message ?: "Authentication error"}. Please check Google Play Services or use Email sign-in."))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Google Sign-In failed", e)
            Result.failure(e)
        }
    }

    /**
     * Signs in with email and password using Firebase Auth.
     * Restores existing player profile and permanent Player ID from Cloud Firestore.
     */
    suspend fun signInWithEmail(email: String, pass: String): Result<User> {
        return try {
            var authUid = "email_${email.hashCode()}"
            var displayName = email.substringBefore("@")

            if (firebaseAuth != null) {
                val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
                val fbUser = result.user
                fbUser?.uid?.let { authUid = it }
                fbUser?.displayName?.let { if (it.isNotBlank()) displayName = it }
            }

            val (user, wallet, txs) = firestoreManager.restoreOrRegisterUser(
                authUid = authUid,
                email = email,
                name = displayName
            )
            handleLoginResult(user, wallet, txs)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Email sign-in failed", e)
            Result.failure(e)
        }
    }

    /**
     * Registers a new user with email, password, and Free Fire details using Firebase Auth.
     */
    suspend fun signUpWithEmail(
        email: String,
        pass: String,
        name: String,
        ffName: String,
        ffUid: String
    ): Result<User> {
        return try {
            var authUid = "email_${email.hashCode()}"
            if (firebaseAuth != null) {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
                result.user?.uid?.let { authUid = it }
            }

            val (user, wallet, txs) = firestoreManager.restoreOrRegisterUser(
                authUid = authUid,
                email = email,
                name = name.ifBlank { email.substringBefore("@") },
                ffName = ffName.ifBlank { "⚡PLAYER_FF⚡" },
                ffUid = ffUid.ifBlank { "2948109382" }
            )
            handleLoginResult(user, wallet, txs)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Email sign-up failed", e)
            Result.failure(e)
        }
    }

    /**
     * Signs out from Firebase Authentication and clears the local authenticated user state.
     */
    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error signing out from Firebase", e)
        }
        _authenticatedUser.value = null
        _lastRestoredWallet.value = null
        _lastRestoredTransactions.value = emptyList()
    }
}
