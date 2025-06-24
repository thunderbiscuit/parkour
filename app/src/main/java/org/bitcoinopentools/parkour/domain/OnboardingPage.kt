package org.bitcoinopentools.parkour.domain

import androidx.compose.ui.graphics.vector.ImageVector

// TODO: This is not business logic and just UI related, so should probably be in presentation package.
data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)
