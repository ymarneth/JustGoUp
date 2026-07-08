package org.clc.justgoup.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.clc.justgoup.di.provideClimbingSessionRepository
import org.clc.justgoup.export.rememberBackupExporter
import org.clc.justgoup.export.rememberBackupImporter
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode
import org.clc.justgoup.ui.theme.components.BoulderButton
import org.clc.justgoup.ui.theme.components.ChipDivider
import org.clc.justgoup.ui.theme.components.SelectableChip
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    onChangeTheme: (ThemeMode) -> Unit,
    onBack: () -> Unit
) {
    val repository = provideClimbingSessionRepository()
    val viewModel = remember { SettingsViewModel(repository) }
    val scope = rememberCoroutineScope()

    var statusMessage by remember { mutableStateOf<String?>(null) }

    val exportBackup = rememberBackupExporter { success ->
        statusMessage = if (success) "Backup exported" else "Export cancelled or failed"
    }

    val importBackup = rememberBackupImporter { content ->
        if (content == null) {
            statusMessage = "Import cancelled"
        } else {
            scope.launch {
                viewModel.importFromJson(content)
                    .onSuccess { count -> statusMessage = "Imported $count new session(s)" }
                    .onFailure { statusMessage = "Import failed: invalid file" }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ---- TOP BAR ----
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = BoulderTheme.spacing.extraLarge.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BoulderTheme.colors.textPrimary
                )
            }

            Text(
                text = "Settings",
                style = BoulderTheme.typography.titleLarge,
                color = BoulderTheme.colors.textPrimary
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        // ---- THEME ----
        Text(
            text = "Theme",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textSecondary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(BoulderTheme.colors.surface)
        ) {
            SelectableChip(
                label = "System",
                value = ThemeMode.SYSTEM,
                selectedValue = currentTheme,
                onSelect = onChangeTheme,
                modifier = Modifier.weight(1f)
            )
            ChipDivider()
            SelectableChip(
                label = "Light",
                value = ThemeMode.LIGHT,
                selectedValue = currentTheme,
                onSelect = onChangeTheme,
                modifier = Modifier.weight(1f)
            )
            ChipDivider()
            SelectableChip(
                label = "Dark",
                value = ThemeMode.DARK,
                selectedValue = currentTheme,
                onSelect = onChangeTheme,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        // ---- DATA ----
        Text(
            text = "Data",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textSecondary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.medium.dp)) {
            BoulderButton(
                text = "Export Data",
                onClick = {
                    scope.launch {
                        val json = viewModel.exportToJson()
                        exportBackup(exportFileName(), json)
                    }
                },
                modifier = Modifier.weight(1f)
            )
            BoulderButton(
                text = "Import Data",
                onClick = importBackup,
                modifier = Modifier.weight(1f)
            )
        }

        statusMessage?.let { message ->
            Spacer(Modifier.height(BoulderTheme.spacing.small.dp))
            Text(
                text = message,
                style = BoulderTheme.typography.body,
                color = BoulderTheme.colors.textSecondary
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun exportFileName(): String {
    val date = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
    return "justgoup-backup-$date.json"
}
