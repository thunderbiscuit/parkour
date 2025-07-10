package org.bitcoinopentools.parkour.domain

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import org.experimental.ark.ArkWallet
import java.net.URL
import javax.net.ssl.HttpsURLConnection

private const val TAG: String = "ParkourApp/Wallet"
// private const val RECOVERY_PHRASE = "era film bless deposit agent either list galaxy guilt layer start squirrel"
private const val RECOVERY_PHRASE = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"

object Wallet {
    private lateinit var wallet: ArkWallet
    private lateinit var dbPath: String
    private val scope = CoroutineScope(Dispatchers.IO)

    fun setDbPath(path: String) {
        dbPath = path
    }

    fun initialize() {
        val db = File(dbPath, "parkour_db.sqlite3")
        scope.launch(Dispatchers.IO) {
            Log.i(TAG, "Creating existing Ark wallet...")
            wallet = ArkWallet(dbPath, RECOVERY_PHRASE)
        }
        // if (db.exists()) {
        //     // Log.i(TAG, "Database already exists at $dbPath")
        //     // scope.launch(Dispatchers.IO) {
        //     //     Log.i(TAG, "Loading existing Ark wallet...")
        //     //     wallet = ArkWallet.load(dbPath, RECOVERY_PHRASE)
        //     // }
        // } else {
        //     Log.i(TAG, "Database doesn't exist at $dbPath")
        //     scope.launch(Dispatchers.IO) {
        //         Log.i(TAG, "Creating existing Ark wallet...")
        //         wallet = ArkWallet(dbPath, RECOVERY_PHRASE)
        //     }
        // }
    }

    // fun onchainBalance(): ULong {
    //     val balance = wallet.onchainBalance()
    //     Log.d(TAG, "Onchain balance: $balance")
    //     return balance
    // }

    fun onchainAddress(): String {
        return wallet.onchainAddress()
    }

    fun syncOnchain(): Unit {
        scope.launch(Dispatchers.IO) {
            wallet.syncOnchain()
            Log.i(TAG, "Onchain wallet synced successfully")
        }
    }

    fun onchainBalance(): ULong {
        return runBlocking(Dispatchers.IO) {
            val balance = wallet.onchainBalance()
            Log.d(TAG, "Onchain balance: $balance")
            balance
        }
    }

    fun arkBalance(): ULong {
        return runBlocking(Dispatchers.IO) {
            val balance = wallet.offchainBalance()
            Log.d(TAG, "Ark balance: $balance")
            balance
        }
    }

    fun syncArk(): Unit {
        Log.d(TAG, "Syncing wallet...")
        scope.launch(Dispatchers.IO) {
            wallet.maintenance()
            Log.i(TAG, "Wallet synced successfully")
        }
    }

    fun vtxoPubkey(): String {
        return runBlocking {
            val pubkey = wallet.vtxoPubkey()
            Log.d(TAG, "VTXO Pubkey: $pubkey")
            pubkey
        }
    }
}

fun testArkReachability(url: String, timeoutMillis: Int = 3000): Boolean {
    return try {
        val connection = URL(url).openConnection() as HttpsURLConnection
        connection.connectTimeout = timeoutMillis
        connection.readTimeout = timeoutMillis
        connection.requestMethod = "HEAD" // lightweight
        connection.connect()
        connection.responseCode in 200..499 // Accept anything but timeouts
    } catch (e: Exception) {
        Log.e("ArkTest", "Error connecting to $url", e)
        false
    }
}

