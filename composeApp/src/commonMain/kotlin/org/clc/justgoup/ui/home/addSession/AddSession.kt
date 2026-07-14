package org.clc.justgoup.ui.home.addSession

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.BoulderButton
import org.clc.justgoup.ui.theme.components.BoulderTextField
import org.koin.compose.koinInject

@Composable
fun AddSession(
    onOpenSession: (String) -> Unit
) {
    val repository = koinInject<ClimbingSessionRepository>()
    val viewModel = remember { AddSessionViewModel(repository) }

    var location by remember { mutableStateOf("") }

    val minLength = 3
    val isValid = location.length >= minLength

    Column {
        Text(
            text = "Start Session",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        BoulderTextField(
            modifier = Modifier.fillMaxWidth(),
            value = location,
            onValueChange = { location = it },
            placeholder = "Location",
            maxLength = 50,
            isError = !isValid && location.isNotEmpty()
        )

        // Error message
        if (!isValid && location.isNotEmpty()) {
            Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

            Text(
                text = "Minimum $minLength characters",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        BoulderButton(
            text = "Start Session",
            enabled = isValid,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.startSession(
                    locationInput = location,
                    onSessionCreated = { newId -> onOpenSession(newId) }
                )
            },
        )
    }
}
