package org.clc.justgoup.ui.climbingSession

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun SessionDetailScreen(
    sessionId: String,
) {
    val viewModel: SessionDetailScreenViewModel = viewModel(
        factory = sessionDetailScreenViewModelFactory(sessionId)
    )

    val session by viewModel.session.collectAsState()

    session?.let { session ->

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.large.dp)
        ) {

            // --- HEADER ---
            item {
                SessionHeader(session = session)
            }

            // --- ADD BOULDER BUTTON ----
            item {
                AddBoulderButton(onAdd = {
                    // TODO: Implement
                })
            }

            // --- BOULDER LIST ---
            items(session.boulders) { boulder ->
                BoulderCard(boulder = boulder)
            }
        }
    } ?: run {
        // Show loading / placeholder
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading session…", color = BoulderTheme.colors.textSecondary)
        }
    }
}
