package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AuthenticationRepository
import com.example.data.BattleArenaRepository
import com.example.data.FirestoreManager
import com.example.model.AppLanguage
import com.example.model.LevelVerificationStatus
import com.example.model.Transaction
import com.example.model.TransactionStatus
import com.example.model.TransactionType
import com.example.model.User
import com.example.model.Wallet
import com.example.viewmodel.BattleArenaViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Validates the Battle Arena permanent player account and cloud data restore system.
 * Tests:
 * 1. Every player receives a unique, permanent Player ID (e.g. BA-XXXXX).
 * 2. Data stored in cloud: profile, wallet balance, match transactions, stats, language preference.
 * 3. Complete app uninstall and reinstall simulation:
 *    - All local caches, shared preferences, and in-memory states are wiped.
 *    - Player logs in with their existing account.
 *    - Original Player ID, wallet, stats, language, and match history are 100% restored.
 * 4. Idempotency: Never creates a duplicate account or overwrites existing data upon re-login.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlayerAccountCloudRestoreTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        FirestoreManager.resetCloudDatabaseForTesting()
        // Clear local preferences
        context.getSharedPreferences("battle_arena_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun `every player must have a unique and permanent Player ID upon registration`() = runBlocking {
        val authRepo = AuthenticationRepository(firebaseAuth = null)

        val reg1 = authRepo.signUpWithEmail(
            email = "player_one@gmail.com",
            pass = "secret123",
            name = "Raju Hunter",
            ffName = "⚡RAJU_OP⚡",
            ffUid = "2948112233"
        )
        assertTrue(reg1.isSuccess)
        val player1 = reg1.getOrNull()
        assertNotNull(player1)
        assertTrue("Player 1 must have a permanent Player ID starting with BA-", player1!!.id.startsWith("BA-"))
        assertEquals("player_one@gmail.com", player1.email)

        val reg2 = authRepo.signUpWithEmail(
            email = "player_two@gmail.com",
            pass = "secret456",
            name = "Anik Sniper",
            ffName = "亗ANIK_PRO亗",
            ffUid = "2948998877"
        )
        assertTrue(reg2.isSuccess)
        val player2 = reg2.getOrNull()
        assertNotNull(player2)
        assertTrue("Player 2 must have a permanent Player ID starting with BA-", player2!!.id.startsWith("BA-"))

        assertNotEquals("Each player must have a unique Player ID", player1.id, player2.id)
    }

    @Test
    fun `test complete uninstall reinstall login and data restoration process`() = runBlocking {
        // =========================================================================
        // STEP 1: INITIAL APP INSTALLATION & ACCOUNT SETUP
        // =========================================================================
        val initialRepo = BattleArenaRepository()
        val initialViewModel = BattleArenaViewModel(initialRepo)

        val authRepo = initialRepo.authRepository
        val signUpResult = authRepo.signUpWithEmail(
            email = "pro.gamer.india@gmail.com",
            pass = "password123",
            name = "Kunal Roy",
            ffName = "⚡KUNAL_FIRE⚡",
            ffUid = "2948554433"
        )
        assertTrue("Sign up must succeed", signUpResult.isSuccess)
        val initialUser = signUpResult.getOrNull()!!
        val originalPlayerId = initialUser.id
        assertTrue("Player ID must start with BA-", originalPlayerId.startsWith("BA-"))

        initialViewModel.onAuthSuccess(initialUser)

        // =========================================================================
        // STEP 2: PLAYER ENTERS MATCHES, WINS PRIZES, ADDS FUNDS, CHANGES LANGUAGE
        // =========================================================================
        // 2a. Update Free Fire Level to 24 (eligible & verified)
        initialRepo.submitLevelVerification(24)

        // 2b. Add funds to wallet (e.g. deposit 250, winning 120, bonus 20)
        val updatedWallet = Wallet(
            userId = initialUser.uid,
            depositBalance = 250,
            winningBalance = 120,
            bonusBalance = 20,
            reservedBalance = 0
        )
        initialRepo.saveCloudWallet(updatedWallet)

        // 2c. Log a match tournament victory and custom transaction
        val customTx = Transaction(
            id = "TX-MATCH-WON-7788",
            userId = initialUser.uid,
            type = TransactionType.PRIZE_MONEY,
            amount = 120,
            status = TransactionStatus.SUCCESS,
            timestamp = System.currentTimeMillis(),
            referenceId = "MATCH_CS_BOOYAH",
            titleEn = "Clash Squad Booyah Champion Prize",
            titleBn = "ক্ল্যাশ স্কোয়াড বুইয়া চ্যাম্পিয়ন প্রাইজ"
        )
        initialRepo.saveCloudTransaction(customTx)

        // 2d. Update game statistics (kills, wins, earnings, tournaments)
        val updatedProfile = initialRepo.currentUser.value!!.copy(
            totalKills = 34,
            totalWins = 8,
            totalEarnings = 560,
            totalTournaments = 14,
            phone = "+91 91234 56789",
            avatarId = 4
        )
        initialRepo.saveCloudUser(updatedProfile)

        // 2e. Set preferred language to BENGALI
        initialRepo.setLanguage(AppLanguage.BENGALI, context)
        assertEquals(AppLanguage.BENGALI, initialRepo.currentLanguage.value)

        // =========================================================================
        // STEP 3: SIMULATE COMPLETE APP UNINSTALL & REINSTALL
        // (Wipe local device storage, preferences, and discard old instances from memory)
        // =========================================================================
        // Wipe local SharedPreferences (deleted upon uninstall)
        context.getSharedPreferences("battle_arena_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

        // =========================================================================
        // STEP 4: FRESH APP REINSTALL & PLAYER LOGS IN WITH SAME ACCOUNT
        // =========================================================================
        val freshRepo = BattleArenaRepository()
        val freshViewModel = BattleArenaViewModel(freshRepo)

        // Login using previously registered account credentials
        val loginResult = freshRepo.authRepository.signInWithEmail(
            email = "pro.gamer.india@gmail.com",
            pass = "password123"
        )
        assertTrue("Re-login after reinstall must succeed", loginResult.isSuccess)
        val restoredUser = loginResult.getOrNull()!!

        freshViewModel.onAuthSuccess(restoredUser)

        // =========================================================================
        // STEP 5: VERIFY FULL RESTORATION WITHOUT DATA LOSS OR OVERWRITES
        // =========================================================================
        // 5a. Permanent Player ID must match the original ID exactly!
        assertEquals(
            "Original permanent Player ID must be restored identically",
            originalPlayerId,
            freshRepo.currentUser.value?.id
        )
        assertEquals(originalPlayerId, restoredUser.id)

        // 5b. Profile information restored
        assertEquals("Kunal Roy", freshRepo.currentUser.value?.name)
        assertEquals("⚡KUNAL_FIRE⚡", freshRepo.currentUser.value?.freeFireName)
        assertEquals("2948554433", freshRepo.currentUser.value?.freeFireUid)
        assertEquals(24, freshRepo.currentUser.value?.freeFireLevel)
        assertEquals(LevelVerificationStatus.VERIFIED, freshRepo.currentUser.value?.levelVerificationStatus)
        assertEquals("+91 91234 56789", freshRepo.currentUser.value?.phone)
        assertEquals(4, freshRepo.currentUser.value?.avatarId)

        // 5c. Lifetime game statistics restored
        assertEquals(34, freshRepo.currentUser.value?.totalKills)
        assertEquals(8, freshRepo.currentUser.value?.totalWins)
        assertEquals(560, freshRepo.currentUser.value?.totalEarnings)
        assertEquals(14, freshRepo.currentUser.value?.totalTournaments)

        // 5d. Wallet balance and funds segregation restored
        val restoredWallet = freshRepo.currentWallet.value
        assertEquals("Deposit balance must be restored", 250, restoredWallet.depositBalance)
        assertEquals("Winning balance must be restored", 120, restoredWallet.winningBalance)
        assertEquals("Bonus balance must be restored", 20, restoredWallet.bonusBalance)
        assertEquals("Available balance must equal 390", 390, restoredWallet.availableBalance)

        // 5e. Language preference restored
        assertEquals(
            "Language preference must be restored from cloud",
            AppLanguage.BENGALI,
            freshRepo.currentLanguage.value
        )

        // 5f. Match history / transactions restored
        val restoredTxs = freshRepo.transactions.value
        assertTrue("Match transactions must be restored", restoredTxs.isNotEmpty())
        val foundTx = restoredTxs.find { it.id == "TX-MATCH-WON-7788" }
        assertNotNull("Specific match victory transaction must be restored", foundTx)
        assertEquals(120, foundTx?.amount)
        assertEquals(TransactionType.PRIZE_MONEY, foundTx?.type)
    }

    @Test
    fun `never create duplicate accounts when existing player logs in`() = runBlocking {
        val authRepo = AuthenticationRepository(firebaseAuth = null)

        // Register first time
        val reg = authRepo.signUpWithEmail(
            email = "duplicate.check@arena.com",
            pass = "mypassword",
            name = "Aman Verma",
            ffName = "⚡AMAN_99⚡",
            ffUid = "2948191919"
        )
        val initialPlayer = reg.getOrNull()!!
        val originalPlayerId = initialPlayer.id

        // Log in second time
        val login1 = authRepo.signInWithEmail("duplicate.check@arena.com", "mypassword")
        val user1 = login1.getOrNull()!!
        assertEquals("Player ID must not change on 2nd login", originalPlayerId, user1.id)

        // Log in third time
        val login2 = authRepo.signInWithEmail("duplicate.check@arena.com", "mypassword")
        val user2 = login2.getOrNull()!!
        assertEquals("Player ID must not change on 3rd login", originalPlayerId, user2.id)

        assertEquals("No duplicate accounts: same UID", initialPlayer.uid, user2.uid)
    }

    @Test
    fun `player data persists without depending on local shared preferences`() = runBlocking {
        val firestoreManager = FirestoreManager()

        // 1. Save data directly to cloud
        val cloudUser = User(
            uid = "cloud_player_99",
            displayName = "Cloud Warrior",
            email = "cloud.warrior@gmail.com",
            freeFireUID = "2948777777",
            freeFireName = "⚡CLOUD_OP⚡",
            freeFireLevel = 18,
            playerId = "BA-88899",
            languageCode = "hi",
            totalKills = 50,
            totalWins = 10,
            totalEarnings = 800,
            totalTournaments = 20
        )
        firestoreManager.saveUser(cloudUser)

        val cloudWallet = Wallet(
            userId = "cloud_player_99",
            depositBalance = 300,
            winningBalance = 200,
            bonusBalance = 50
        )
        firestoreManager.saveWallet(cloudWallet)

        // 2. Clear all local SharedPreferences
        context.getSharedPreferences("battle_arena_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

        // 3. Restore purely from cloud
        val (restoredUser, restoredWallet, _) = firestoreManager.restoreOrRegisterUser(
            authUid = "cloud_player_99",
            email = "cloud.warrior@gmail.com",
            name = "Default Name"
        )

        assertEquals("BA-88899", restoredUser.id)
        assertEquals("Cloud Warrior", restoredUser.displayName)
        assertEquals("hi", restoredUser.languageCode)
        assertEquals(50, restoredUser.totalKills)
        assertEquals(10, restoredUser.totalWins)
        assertEquals(300, restoredWallet.depositBalance)
        assertEquals(200, restoredWallet.winningBalance)
    }

    @Test
    fun `test secure multi-user data isolation`() = runBlocking {
        val authRepo = AuthenticationRepository(firebaseAuth = null)

        // User A
        val regA = authRepo.signUpWithEmail(
            email = "user.a@arena.com",
            pass = "passA",
            name = "Player A",
            ffName = "⚡PLAYER_A⚡",
            ffUid = "2948000001"
        )
        val userA = regA.getOrNull()!!

        // User B
        val regB = authRepo.signUpWithEmail(
            email = "user.b@arena.com",
            pass = "passB",
            name = "Player B",
            ffName = "⚡PLAYER_B⚡",
            ffUid = "2948000002"
        )
        val userB = regB.getOrNull()!!

        assertNotEquals("Users must have different Player IDs", userA.id, userB.id)

        // Fund User A wallet only
        authRepo.firestoreManager.saveWallet(
            Wallet(userId = userA.uid, depositBalance = 500, winningBalance = 300)
        )

        // Fund User B wallet with different amounts
        authRepo.firestoreManager.saveWallet(
            Wallet(userId = userB.uid, depositBalance = 100, winningBalance = 50)
        )

        // Re-login as User A
        val (restoredUserA, walletA, _) = authRepo.firestoreManager.restoreOrRegisterUser(
            authUid = userA.uid,
            email = "user.a@arena.com",
            name = "Player A"
        )
        assertEquals(userA.id, restoredUserA.id)
        assertEquals(500, walletA.depositBalance)
        assertEquals(300, walletA.winningBalance)

        // Re-login as User B
        val (restoredUserB, walletB, _) = authRepo.firestoreManager.restoreOrRegisterUser(
            authUid = userB.uid,
            email = "user.b@arena.com",
            name = "Player B"
        )
        assertEquals(userB.id, restoredUserB.id)
        assertEquals(100, walletB.depositBalance)
        assertEquals(50, walletB.winningBalance)
    }
}
