package org.clc.justgoup.ui.climbingSessionDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.clc.justgoup.boulder.Boulder
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.boulder.toDisplayString
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.GradeChip

@Composable
fun BoulderCard(boulder: Boulder) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BoulderTheme.colors.surface)
            .padding(16.dp)
    ) {
        GradeChip(
            grade = boulder.grade.toDisplayString(),
            color = (boulder.color?.toColor() ?: Color.Gray)
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Attempts: ${boulder.attempts}",
                color = BoulderTheme.colors.textPrimary,
                style = BoulderTheme.typography.body
            )

            if (boulder.sent) {
                Text(
                    text = if (boulder.flash) "FLASH" else "SEND",
                    style = BoulderTheme.typography.body,
                    color = BoulderTheme.colors.success
                )
            }
        }

        if (boulder.notes != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = boulder.notes,
                style = BoulderTheme.typography.body,
                color = BoulderTheme.colors.textSecondary
            )
        }
    }
}

fun HoldColor.toColor(): Color = when (this) {
    HoldColor.RED -> Color(0xFFE53935)
    HoldColor.PINK -> Color(0xFFF06292)
    HoldColor.BLUE -> Color(0xFF1E88E5)
    HoldColor.GREEN -> Color(0xFF43A047)
    HoldColor.TEAL -> Color(0xFF009688)
    HoldColor.YELLOW -> Color(0xFFFDD835)
    HoldColor.ORANGE -> Color(0xFFFB8C00)
    HoldColor.PURPLE -> Color(0xFF8E24AA)
    HoldColor.BLACK -> Color(0xFF212121)
    HoldColor.WHITE -> Color(0xFFFAFAFA)
    HoldColor.GREY -> Color(0xFF9E9E9E)
}
