package org.bitcoinopentools.parkour.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenDestinations: NavKey {
    @Serializable
    data object Onboarding : ScreenDestinations()

    @Serializable
    data object Home : ScreenDestinations()

    @Serializable
    data object Send : ScreenDestinations()

    @Serializable
    data object Receive : ScreenDestinations()

    @Serializable
    data object History : ScreenDestinations()

    @Serializable
    data object TxDetails : ScreenDestinations()

    @Serializable
    data object About : ScreenDestinations()

    @Serializable
    data object Settings : ScreenDestinations()

    @Serializable
    data object Vtxos : ScreenDestinations()

    @Serializable
    data object VtxoDetails : ScreenDestinations()
}
