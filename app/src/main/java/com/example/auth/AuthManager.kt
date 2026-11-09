package com.example.auth

import android.content.Context
import com.example.data.AuthenticationRepository
import com.example.model.User
import com.google.firebase.auth.FirebaseUser

/**
 * AuthManager acts as a coordinator delegating to AuthenticationRepository
 * for clean separation of concerns and backward compatibility across UI components.
 */
class AuthManager(
    val repository: AuthenticationRepository = AuthenticationRepository()
) {
    val currentFirebaseUser: FirebaseUser?
        get() = repository.currentFirebaseUser

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return repository.signInWithEmail(email, password)
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        name: String,
        ffName: String,
        ffUid: String
    ): Result<User> {
        return repository.signUpWithEmail(email, password, name, ffName, ffUid)
    }

    suspend fun signInWithGoogle(context: Context, serverClientId: String = ""): Result<User> {
        return repository.signInWithGoogle(context, serverClientId)
    }

    val lastRestrictedUser = repository.lastRestrictedUser

    fun clearLastRestrictedUser() {
        repository.clearLastRestrictedUser()
    }

    fun signOut() {
        repository.signOut()
    }
}
