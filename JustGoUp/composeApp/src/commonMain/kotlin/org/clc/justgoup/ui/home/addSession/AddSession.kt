package org.clc.justgoup.ui.home.addSession

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.di.provideClimbingSessionRepository
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.BoulderButton
import org.clc.justgoup.ui.theme.components.BoulderTextField

@Composable
fun AddSession(
    onOpenSession: (String) -> Unit
) {
    val repository = provideClimbingSessionRepository()
    val viewModel = remember { AddSessionViewModel(repository) }

    val locationState = rememberTextFieldState()

    val minLength = 3
    val text = locationState.text
    val isValid = text.length >= minLength

    Column {
        Text(
            text = "Start Session",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        BoulderTextField(
            modifier = Modifier.fillMaxWidth(),
            state = locationState,
            placeholder = "Location",
            maxLength = 50,
            isError = !isValid && text.isNotEmpty()
        )

        // Error message
        if (!isValid && text.isNotEmpty()) {
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
                    locationInput = text.toString(),
                    onSessionCreated = { newId -> onOpenSession(newId) }
                )
            },
        )
    }
}
