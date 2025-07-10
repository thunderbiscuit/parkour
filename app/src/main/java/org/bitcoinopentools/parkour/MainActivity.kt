package org.bitcoinopentools.parkour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bitcoinopentools.parkour.domain.Wallet
import org.bitcoinopentools.parkour.presentation.navigation.NavigationRoot
import org.bitcoinopentools.parkour.presentation.ui.theme.ParkourTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val filesDirectoryPath: String = applicationContext.filesDir.absolutePath
        Wallet.setDbPath(filesDirectoryPath)
        Wallet.initialize()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ParkourTheme {
                NavigationRoot(onboardingDone = true)
            }
        }
    }
}
