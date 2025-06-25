package org.bitcoinopentools.parkour.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.WideNavigationRailColors
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.composables.icons.lucide.ArrowRightLeft
import com.composables.icons.lucide.Circle
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Menu
import com.composables.icons.lucide.Settings
import kotlinx.coroutines.Dispatchers
import org.bitcoinopentools.parkour.domain.Wallet
import org.bitcoinopentools.parkour.domain.testArkReachability
import org.bitcoinopentools.parkour.presentation.navigation.ScreenDestinations
import org.bitcoinopentools.parkour.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigation: (ScreenDestinations) -> Unit
) {
    val items = listOf("History", "About", "Settings", "Vtxos")
    val icons = listOf(Lucide.ArrowRightLeft, Lucide.Info, Lucide.Settings, Lucide.Circle)
    val state = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parkour") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { state.expand() } }) {
                        Icon(Lucide.Menu, contentDescription = "Open Navigation Rail")
                    }
                },
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
                    items.forEachIndexed { index, item ->
                        WideNavigationRailItem(
                            railExpanded = true,
                            icon = {
                                Icon(
                                    icons[index],
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            },
                            label = { Text(item) },
                            selected = false,
                            onClick = {
                                scope.launch { state.collapse() }
                                when (item) {
                                    "History"  -> onNavigation(ScreenDestinations.History)
                                    "About"    -> onNavigation(ScreenDestinations.About)
                                    "Settings" -> onNavigation(ScreenDestinations.Settings)
                                    "Vtxos"    -> onNavigation(ScreenDestinations.Vtxos)
                                }
                            },
                        )
                    }
                }

                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { scope.launch { state.expand() } },
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("Open Modal Rail")
                    }
                }
            }
        }
    )

}
