package org.bitcoinopentools.parkour.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenDestinations: NavKey {
    @Serializable
    data object Home : ScreenDestinations()

    @Serializable
    data object Send : ScreenDestinations()

    @Serializable
    data object Receive : ScreenDestinations()
}
