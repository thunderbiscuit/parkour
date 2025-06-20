package org.bitcoinopentools.parkour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.bitcoinopentools.parkour.ui.theme.ParkourTheme
import org.experimental.ark.Calendar

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ParkourTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Parkour") }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val calendar: Calendar = Calendar(21, "Nakamoto")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ParkourTheme {}
}
