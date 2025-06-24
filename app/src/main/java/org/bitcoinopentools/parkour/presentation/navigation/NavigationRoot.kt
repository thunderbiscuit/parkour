package org.bitcoinopentools.parkour.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import org.bitcoinopentools.parkour.presentation.ui.screens.HomeScreen
import org.bitcoinopentools.parkour.presentation.ui.screens.SendScreen

@Composable
fun NavigationRoot() {
    val backStack: NavBackStack = rememberNavBackStack(ScreenDestinations.Home)

    // TODO: Consider scoping the viewmodels to the NavEntries.
    //       https://developer.android.com/guide/navigation/navigation-3/save-state#scoping-viewmodels
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ScreenDestinations.Home> {
                HomeScreen()
                    // onNavigateToSendScreen = { backStack.add(ScreenDestinations.Receive) }
            }
            entry<ScreenDestinations.Receive> {
                SendScreen()
            }
        }
    )
}
