package org.bitcoinopentools.parkour.domain

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import org.experimental.ark.ArkWallet
import java.net.URL
import javax.net.ssl.HttpsURLConnection

private const val TAG: String = "ParkourApp/Wallet"
private const val RECOVERY_PHRASE = "era film bless deposit agent either list galaxy guilt layer start squirrel"


object Wallet {
    private lateinit var wallet: ArkWallet
    private lateinit var dbPath: String
    private val scope = CoroutineScope(Dispatchers.IO)

    fun setDbPath(path: String) {
        dbPath = path
    }

    fun initialize() {
        val db = File(dbPath, "parkour_db.sqlite3")
        if (db.exists()) {
            Log.i(TAG, "Database already exists at $dbPath")
            scope.launch(Dispatchers.IO) {
                Log.i(TAG, "Loading existing Ark wallet...")
                wallet = ArkWallet.load(dbPath, RECOVERY_PHRASE)
            }
        } else {
            Log.i(TAG, "Database doesn't exist at $dbPath")
            scope.launch(Dispatchers.IO) {
                Log.i(TAG, "Creating existing Ark wallet...")
                wallet = ArkWallet(dbPath, RECOVERY_PHRASE)
            }
        }
    }

    // fun testHandshake() {
    //     scope.launch(Dispatchers.IO) {
    //         Log.d("ArkTest", "Testing Ark handshake...")
    //         try {
    //             val handshakeTester = HandshakeTester()
    //             handshakeTester.testHandshake("https://ark.signet.2nd.dev")
    //             Log.d("ArkTest", "Handshake successful")
    //         } catch (e: Exception) {
    //             Log.e("ArkTest", "Handshake failed", e)
    //         }
    //     }
    // }

    fun onchainBalance(): ULong {
        val balance = wallet.onchainBalance()
        Log.d(TAG, "Onchain balance: $balance")
        return balance
    }

    fun arkBalance(): ULong {
        val balance = wallet.offchainBalance()
        Log.d(TAG, "Ark balance: $balance")
        return balance
    }

    fun syncArk(): Unit {
        Log.d(TAG, "Syncing wallet...")
        scope.launch(Dispatchers.IO) {
            wallet.syncArk()
        }
    }

    fun vtxoPubkey(): String {
        val pubkey = wallet.vtxoPubkey()
        Log.d(TAG, "VTXO Pubkey: $pubkey")
        return pubkey
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

