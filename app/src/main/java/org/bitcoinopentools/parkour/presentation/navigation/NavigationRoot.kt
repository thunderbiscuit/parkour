package org.bitcoinopentools.parkour.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import org.bitcoinopentools.parkour.presentation.ui.screens.HomeScreen
import org.bitcoinopentools.parkour.presentation.ui.screens.OnboardingScreen
import org.bitcoinopentools.parkour.presentation.ui.screens.OnchainScreen
import org.bitcoinopentools.parkour.presentation.ui.screens.PlaceHolderScreen
import org.bitcoinopentools.parkour.presentation.ui.screens.ReceiveScreen
import org.bitcoinopentools.parkour.presentation.ui.screens.SendScreen

@Composable
fun NavigationRoot(
    onboardingDone: Boolean
) {
    val backStack: NavBackStack = if (onboardingDone) rememberNavBackStack(ScreenDestinations.Home) else rememberNavBackStack(ScreenDestinations.Onboarding)

    // TODO: Consider scoping the viewmodels to the NavEntries.
    //       https://developer.android.com/guide/navigation/navigation-3/save-state#scoping-viewmodels
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ScreenDestinations.Onboarding> {
                OnboardingScreen(
                    onFinishOnboarding = {
                        backStack.removeLastOrNull()
                        backStack.add(ScreenDestinations.Home)
                    }
                )
            }

            entry<ScreenDestinations.Home> {
                HomeScreen(
                    onNavigation = { destination ->
                        when (destination) {
                            ScreenDestinations.History  -> backStack.add(ScreenDestinations.History)
                            ScreenDestinations.About    -> backStack.add(ScreenDestinations.About)
                            ScreenDestinations.Settings -> backStack.add(ScreenDestinations.Settings)
                            ScreenDestinations.Vtxos    -> backStack.add(ScreenDestinations.Vtxos)
                            ScreenDestinations.Onchain  -> backStack.add(ScreenDestinations.Onchain)
                            ScreenDestinations.Receive  -> backStack.add(ScreenDestinations.Receive)
                            else -> Unit
                        }
                    }
                )
            }

            entry<ScreenDestinations.Receive> {
                ReceiveScreen(
                    onBack = { backStack.removeLastOrNull() }
                )
            }

            entry<ScreenDestinations.History> {
                PlaceHolderScreen("History")
            }

            entry<ScreenDestinations.About> {
                PlaceHolderScreen("About")
            }

            entry<ScreenDestinations.Settings> {
                PlaceHolderScreen("Settings")
            }

            entry<ScreenDestinations.Vtxos> {
                PlaceHolderScreen("Vtxos")
            }

            entry<ScreenDestinations.Onchain> {
                OnchainScreen(
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
