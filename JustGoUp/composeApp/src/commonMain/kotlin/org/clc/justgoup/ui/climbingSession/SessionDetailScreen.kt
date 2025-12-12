package org.clc.justgoup.ui.climbingSession

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun SessionDetailScreen(
    sessionId: String,
    onBack: () -> Unit
) {
    val viewModel: SessionDetailScreenViewModel = viewModel(
        factory = sessionDetailScreenViewModelFactory(sessionId)
    )

    val session by viewModel.session.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = BoulderTheme.spacing.medium.dp)
    ) {
        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        // Back button
        Button(onClick = onBack) {
            Text("Back")
        }

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        // Session info
        Text(
            text = "Session ID: $sessionId",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        session?.let { session ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(session.boulders) { boulder ->
                    Text(
                        text = "Boulder: ${boulder.grade}, attempts: ${boulder.attempts}, sent: ${boulder.sent}",
                        style = BoulderTheme.typography.body,
                        color = BoulderTheme.colors.textSecondary
                    )
                }
            }
        }
    }
}
