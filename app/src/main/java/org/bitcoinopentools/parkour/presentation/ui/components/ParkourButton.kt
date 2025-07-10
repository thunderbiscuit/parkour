package org.bitcoinopentools.parkour.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bitcoinopentools.parkour.R

@Composable
fun ParkourButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(0.75f)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(3.dp, Color.Black),
        shape = RoundedCornerShape(12.dp),
        interactionSource = remember { MutableInteractionSource() }
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_medium)),
            // fontFamily = FontFamily(Font(R.font.courierprime_bold))
            // fontFamily = FontFamily(Font(R.font.orbitron_semibold))
        )
    }
}
