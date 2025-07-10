package org.bitcoinopentools.parkour.presentation.ui.screens

import android.util.Log
import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CurrencyBitcoin
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalWideNavigationRail
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.WideNavigationRailColors
import androidx.compose.material3.ripple
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowRightLeft
import com.composables.icons.lucide.Bitcoin
import com.composables.icons.lucide.Circle
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Menu
import com.composables.icons.lucide.Settings
import org.bitcoinopentools.parkour.domain.Wallet
import org.bitcoinopentools.parkour.presentation.navigation.ScreenDestinations
import org.bitcoinopentools.parkour.R
import org.bitcoinopentools.parkour.presentation.ui.components.ParkourButton
import org.bitcoinopentools.parkour.presentation.ui.theme.testPink

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigation: (ScreenDestinations) -> Unit
) {
    val items = listOf("History", "VTXOs", "Settings", "About")
    val icons = listOf(Lucide.ArrowRightLeft, Lucide.Circle, Lucide.Settings, Lucide.Info)
    val state = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Parkour",
                        fontSize = 22.sp,
                        fontFamily = FontFamily(Font(R.font.orbitron_semibold))
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { state.expand() } }) {
                        Icon(Lucide.Menu, contentDescription = "Open Navigation Rail")
                    }
                },
                // Completely white top app bar
                // colors = TopAppBarColors(
                //     containerColor = Color.White,
                //     scrolledContainerColor = Color.White,
                //     navigationIconContentColor = Color.Black,
                //     titleContentColor =  Color.Black,
                //     actionIconContentColor = Color.Black,
                //     subtitleContentColor = Color.Black,
                // )
                // Top app bar is black with white text and icons
                // colors = TopAppBarColors(
                //     containerColor = Color.Black,
                //     scrolledContainerColor = Color.Black,
                //     navigationIconContentColor = Color.White,
                //     titleContentColor =  Color.White,
                //     actionIconContentColor = Color.White,
                //     subtitleContentColor = Color.White,
                // )
            )
        },
        content = { paddingValues ->
            Row(Modifier.fillMaxSize()) {
                ModalWideNavigationRail(
                    state = state,
                    hideOnCollapse = true,
                    colors = WideNavigationRailColors(
                        modalContainerColor = Color.White,
                        modalScrimColor = Color(0x50000000),
                        containerColor = Color.Transparent, // Not used here because we are using a modal navigation rail
                        contentColor = Color.Transparent, // Not used here because we are using a modal navigation rail
                    )
                ) {
                    Row(
                        modifier = Modifier.width(250.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.parkour),
                            contentDescription = "Hero Image",
                            modifier = Modifier
                                .padding(16.dp)
                                .size(64.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    // HorizontalDivider()
                    items.forEachIndexed { index, item ->
                        WideNavigationRailItem(
                            railExpanded = true,
                            icon = {
                                Icon(
                                    icons[index],
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item,
                                    fontFamily = FontFamily(Font(R.font.ibmplexmono_medium))
                                )
                            },
                            selected = false,
                            onClick = {
                                scope.launch { state.collapse() }
                                when (item) {
                                    "History"  -> onNavigation(ScreenDestinations.History)
                                    "Vtxos"    -> onNavigation(ScreenDestinations.Vtxos)
                                    "Settings" -> onNavigation(ScreenDestinations.Settings)
                                    "About"    -> onNavigation(ScreenDestinations.About)
                                }
                            },
                            modifier = Modifier.offset(x = (-10).dp)
                            // modifier = Modifier.padding(start = 30.dp).background(testPink)
                        )
                    }
                }

                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    var arkBalance by remember { mutableLongStateOf(100L) }
                    Spacer(Modifier.height(70.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color.Black, modifier = Modifier.fillMaxWidth(0.6f))
                    Spacer(Modifier.height(24.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Icon(
                            imageVector =  Icons.Rounded.CurrencyBitcoin,
                            tint = Color.Black,
                            contentDescription = "Bitcoin",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(50.dp)
                        )
                        Text(
                            text = "$arkBalance",
                            fontSize = 32.sp,
                            fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color.Black, modifier = Modifier.fillMaxWidth(0.6f))
                    // Row(
                    //     Modifier
                    //         .fillMaxWidth(0.9f)
                    //         .padding(horizontal = 8.dp)
                    //         .background(
                    //             // color = Color(0xffd2d2d2),
                    //             color = Color(0xffe4e4e4),
                    //             shape = RoundedCornerShape(16.dp)
                    //         )
                    //         .height(100.dp),
                    //     verticalAlignment = Alignment.CenterVertically,
                    //     horizontalArrangement = Arrangement.SpaceEvenly
                    // ) {
                    //     Icon(
                    //         imageVector =  Icons.Rounded.CurrencyBitcoin,
                    //         tint = Color.Black,
                    //         contentDescription = "Bitcoin",
                    //         modifier = Modifier
                    //             .align(Alignment.CenterVertically)
                    //             .size(50.dp)
                    //     )
                    //     Text(
                    //         text = "$arkBalance",
                    //         fontSize = 32.sp,
                    //         fontFamily = FontFamily(Font(R.font.ibmplexmono_regular)),
                    //     )
                    // }
                    // Spacer(Modifier.height(100.dp))
                    // ParkourButton(
                    //     text = "Create Wallet",
                    //     onClick = { Wallet.initialize() }
                    // )
                    // ParkourButton(
                    //     text = "Update balance",
                    //     onClick = {
                    //         arkBalance = Wallet.arkBalance().toLong()
                    //     },
                    // )
                    // ParkourButton(
                    //     text = "Sync Wallet",
                    //     onClick = {
                    //         Wallet.syncArk()
                    //     },
                    // )
                    // ParkourButton(
                    //     text = "VTXO Pubkey",
                    //     onClick = {
                    //         val pubkey: String = Wallet.vtxoPubkey()
                    //         Log.i("ParkourApp", "VTXO Pubkey: $pubkey")
                    //     },
                    // )
                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider(thickness = 1.dp, color = Color.Black, modifier = Modifier.fillMaxWidth(0.9f))
                    BottomButtonRow()
                }
            }
        }
    )
}

@Composable
fun BottomButtonRow() {
    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp)
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            interactionSource = remember { MutableInteractionSource() },
            shape = RectangleShape
        ) {
            Text(
                text = "Receive",
                fontSize = 16.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.ibmplexmono_medium)),
            )
        }
        VerticalDivider(thickness = 1.dp, color = Color.Black, modifier = Modifier.fillMaxHeight(0.9f))
        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RectangleShape
        ) {
            Text(
                text = "Send",
                fontSize = 16.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.ibmplexmono_medium)),
            )
        }
    }
}
