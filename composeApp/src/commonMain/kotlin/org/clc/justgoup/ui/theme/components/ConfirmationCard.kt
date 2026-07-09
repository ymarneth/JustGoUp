package org.clc.justgoup.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun ConfirmationCard(
    message: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    height: Dp = Dp.Unspecified,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 400.dp)
            .height(height)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = message,
                style = BoulderTheme.typography.body,
                color = BoulderTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(BoulderTheme.spacing.medium.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BoulderButton(
                    text = "Cancel",
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
                BoulderButton(
                    text = "Delete",
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
