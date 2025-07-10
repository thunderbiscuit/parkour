package org.bitcoinopentools.parkour.presentation.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CurrencyBitcoin
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.ClipboardCopy
import com.composables.icons.lucide.Link
import com.composables.icons.lucide.Lucide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bitcoinopentools.parkour.R
import org.bitcoinopentools.parkour.domain.Wallet
import org.bitcoinopentools.parkour.presentation.ui.components.ParkourButton
import org.bitcoinopentools.parkour.presentation.ui.theme.LightGray
import org.bitcoinopentools.parkour.presentation.ui.theme.ParkourGray
import org.bitcoinopentools.parkour.presentation.ui.theme.ParkourTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnchainScreen(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var address: String? by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Onchain Wallet",
                            fontSize = 22.sp,
                            fontFamily = FontFamily(Font(R.font.orbitron_semibold))
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Lucide.ArrowLeft, contentDescription = "Navigate back")
                    }
                },
                // Completely white top app bar
                colors = TopAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.White,
                    navigationIconContentColor = Color.Black,
                    titleContentColor =  Color.Black,
                    actionIconContentColor = Color.Black,
                    subtitleContentColor = Color.Black,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var onchainBalance: Long by remember { mutableLongStateOf(0L) }
                Spacer(Modifier.height(70.dp))
                HorizontalDivider(thickness = 1.dp, color = ParkourGray, modifier = Modifier.fillMaxWidth(0.6f))
                Spacer(Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        imageVector =  Lucide.Link,
                        tint = ParkourGray,
                        contentDescription = "Bitcoin",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(34.dp)
                    )
                    Icon(
                        imageVector =  Icons.Rounded.CurrencyBitcoin,
                        tint = ParkourGray,
                        contentDescription = "Bitcoin",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 16.dp)
                            .size(42.dp)
                    )
                    Text(
                        text = "$onchainBalance",
                        fontSize = 32.sp,
                        fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
                    )
                }
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(thickness = 1.dp, color = ParkourGray, modifier = Modifier.fillMaxWidth(0.6f))
                Spacer(Modifier.height(54.dp))

                if (address == null) {
                    ParkourButton(
                        text = "New Address",
                        onClick = { address = Wallet.onchainAddress() },
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    ) {
                        SelectionContainer {
                            Text(
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 100.dp)
                                    .background(
                                        color = LightGray,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        copyToClipboard(
                                            address ?: "",
                                            context,
                                            scope,
                                            snackbarHostState,
                                            null,
                                        )
                                    }
                                    .padding(12.dp),
                                text = address!!.chunked(4).joinToString(" "),
                                fontFamily = FontFamily(Font(R.font.ibmplexmono_medium)),
                                fontSize = 14.sp
                            )
                        }
                        Icon(
                            Lucide.ClipboardCopy,
                            tint = Color.Black,
                            contentDescription = "Copy to clipboard",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
                ParkourButton(
                    text = "Sync Onchain Wallet",
                    onClick = {
                        Wallet.syncOnchain()
                        onchainBalance = Wallet.onchainBalance().toLong()
                    },
                )
            }
        }
    )
}

fun copyToClipboard(content: String, context: Context, scope: CoroutineScope, snackbarHostState: SnackbarHostState, setCopyClicked: ((Boolean) -> Unit)?) {
    val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("", content)
    clipboard.setPrimaryClip(clip)
    scope.launch {
        snackbarHostState.showSnackbar("Copied address to clipboard!")
        delay(1000)
        if (setCopyClicked != null) {
            setCopyClicked(false)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OnchainScreenPreview() {
    ParkourTheme {
        OnchainScreen(
            onBack = { }
        )
    }
}
