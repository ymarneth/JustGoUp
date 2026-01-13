package org.clc.justgoup.ui.climbingSessionDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.toColor
import org.clc.justgoup.boulder.toDisplayString
import org.clc.justgoup.ui.helpers.asShortDateTime
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun BoulderCard(
    boulder: Boulder,
    modifier: Modifier = Modifier
) {
    val cardColor = boulder.color?.toColor()?.copy(alpha = 0.1f) ?: BoulderTheme.colors.surface

    Column(
        modifier = modifier
            .background(
                color = cardColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(BoulderTheme.spacing.medium.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GradeChip(
                grade = boulder.grade.toDisplayString(),
                color = boulder.color?.toColor() ?: BoulderTheme.colors.surface.copy(alpha = 0.2f)
            )

            val statusParts = mutableListOf<String>()
            if (boulder.flash) statusParts.add("FLASH")
            else if (boulder.sent) statusParts.add("SEND")
            if (boulder.repeated) statusParts.add("REPEATED")

            val statusText = statusParts.joinToString(" • ")

            if (statusText.isNotEmpty()) {
                Text(
                    text = statusText,
                    color = BoulderTheme.colors.textPrimary,
                    style = BoulderTheme.typography.titleMedium
                )
            }
        }

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Attempts: ${boulder.attempts}",
                style = BoulderTheme.typography.body,
                color = BoulderTheme.colors.textPrimary
            )

            Text(
                text = boulder.createdAt.asShortDateTime(),
                style = BoulderTheme.typography.label,
                color = BoulderTheme.colors.textSecondary
            )
        }

        boulder.notes?.let { note ->
            Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

            Text(
                text = note,
                style = BoulderTheme.typography.body,
                color = BoulderTheme.colors.textSecondary
            )
        }
    }
}

@Composable
fun GradeChip(
    grade: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val textColor = if (color.luminance() < 0.5f) Color.White else Color.Black

    Box(
        modifier = modifier
            .background(color, RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = grade,
            style = BoulderTheme.typography.titleMedium,
            color = textColor
        )
    }
}
