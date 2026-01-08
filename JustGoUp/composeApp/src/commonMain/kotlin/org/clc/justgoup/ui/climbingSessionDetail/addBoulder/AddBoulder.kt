package org.clc.justgoup.ui.climbingSessionDetail.addBoulder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.clc.justgoup.boulder.FrenchGrade
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.GradingSystem
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.boulder.VGrade
import org.clc.justgoup.boulder.toColor
import org.clc.justgoup.di.provideClimbingSessionRepository
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.BoulderButton

@Composable
fun AddBoulder(
    onOpenSession: (String) -> Unit, sessionId: String
) {
    val repository = provideClimbingSessionRepository()
    val viewModel = remember { AddBoulderViewModel(repository, sessionId) }

    val scrollState = rememberScrollState()

    var gradingSystem by remember { mutableStateOf(GradingSystem.FRENCH) }
    var gradeNumber by remember { mutableStateOf(6) }
    var gradeLetter by remember { mutableStateOf<Char?>('a') }
    var gradePlus by remember { mutableStateOf(false) }
    var vGradeValue by remember { mutableStateOf(0) }
    var vGradePlus by remember { mutableStateOf(false) }
    var vGradeBeginner by remember { mutableStateOf(false) }

    var attempts by remember { mutableStateOf(1) }
    var sent by remember { mutableStateOf(false) }
    var flash by remember { mutableStateOf(false) }
    var color by remember { mutableStateOf<HoldColor?>(null) }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Add Boulder",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = gradingSystem == GradingSystem.FRENCH,
                onClick = { gradingSystem = GradingSystem.FRENCH },
                label = { Text("French") },
            )
            FilterChip(
                selected = gradingSystem == GradingSystem.V_SCALE,
                onClick = { gradingSystem = GradingSystem.V_SCALE },
                label = { Text("V-Scale") })
        }

        when (gradingSystem) {
            GradingSystem.FRENCH -> FrenchGradePicker(
                number = gradeNumber,
                letter = gradeLetter,
                plus = gradePlus,
                onNumberChange = { gradeNumber = it },
                onLetterChange = { gradeLetter = it },
                onPlusChange = { gradePlus = it })

            GradingSystem.V_SCALE -> VScalePicker(
                value = vGradeValue,
                plus = vGradePlus,
                beginner = vGradeBeginner,
                onValueChange = { vGradeValue = it },
                onPlusChange = { vGradePlus = it },
                onBeginnerChange = { vGradeBeginner = it })
        }

        Text(
            text = "Attempts",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textPrimary
        )
        AttemptsSelector(
            value = attempts,
            onChange = {
                attempts = it
                if (it > 1) {
                    flash = false
                }
            })

        SentFlashChips(
            sent = sent,
            flash = flash,
            attempts = attempts,
            onSentChange = { sent = it },
            onFlashChange = { flash = it })

        Text(
            text = "Hold Color",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textPrimary
        )

        HoldColorPicker(
            selected = color, onSelected = { color = it })

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            minLines = 2
        )

        BoulderButton(
            text = "Save Boulder", onClick = {
                val grade = when (gradingSystem) {
                    GradingSystem.FRENCH -> Grade.French(
                        FrenchGrade(
                            number = gradeNumber,
                            letter = gradeLetter,
                            modifier = if (gradePlus) FrenchGrade.Modifier.Plus else null
                        )
                    )

                    GradingSystem.V_SCALE -> Grade.VScale(
                        value = VGrade(
                            value = vGradeValue
                        )
                    )
                }

                viewModel.addBoulderToSession(
                    grade = grade,
                    attempts = attempts,
                    sent = sent,
                    flash = flash,
                    color = color,
                    notes = notes.ifBlank { null },
                    onBoulderAdded = onOpenSession
                )
            })
    }
}

@Composable
fun HoldColorPicker(
    selected: HoldColor?, onSelected: (HoldColor?) -> Unit
) {
    val colors = HoldColor.entries.toTypedArray()

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        colors.forEach { color ->
            val isSelected = selected == color

            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(color.toColor())
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) BoulderTheme.colors.primary
                        else BoulderTheme.colors.textSecondary,
                        shape = CircleShape
                    ).clickable { onSelected(color) })
        }
    }
}

@Composable
fun AttemptsSelector(
    value: Int, onChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BoulderButton(
            text = "-",
            onClick = { if (value > 1) onChange(value - 1) },
            modifier = Modifier.width(64.dp),
        )

        Text(
            text = value.toString(),
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary,
            modifier = Modifier.width(64.dp),
            textAlign = TextAlign.Center
        )

        BoulderButton(
            text = "+", onClick = { onChange(value + 1) }, modifier = Modifier.width(64.dp)
        )
    }
}

@Composable
fun SentFlashChips(
    sent: Boolean,
    flash: Boolean,
    attempts: Int,
    onSentChange: (Boolean) -> Unit,
    onFlashChange: (Boolean) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FilterChip(selected = sent, onClick = {
            onSentChange(!sent)
            if (sent) onFlashChange(false)
        }, label = { Text(text = "Send", color = BoulderTheme.colors.textPrimary) })

        FilterChip(
            selected = flash,
            enabled = sent && attempts == 1,
            onClick = { onFlashChange(!flash) },
            label = { Text(text = "Flash ⚡", color = BoulderTheme.colors.textPrimary) })
    }
}

@Composable
fun FrenchGradePicker(
    number: Int,
    letter: Char?,
    plus: Boolean,
    onNumberChange: (Int) -> Unit,
    onLetterChange: (Char?) -> Unit,
    onPlusChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (3..9).forEach {
                FilterChip(
                    selected = number == it,
                    onClick = { onNumberChange(it) },
                    label = { Text(it.toString()) })
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf('a', 'b', 'c').forEach {
                FilterChip(
                    selected = letter == it,
                    onClick = { onLetterChange(it) },
                    label = { Text(it.toString()) })
            }

            FilterChip(selected = plus, onClick = { onPlusChange(!plus) }, label = { Text("+") })
        }
    }
}

@Composable
fun VScalePicker(
    value: Int,
    plus: Boolean,
    beginner: Boolean,
    onValueChange: (Int) -> Unit,
    onPlusChange: (Boolean) -> Unit,
    onBeginnerChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // VB toggle
        FilterChip(
            selected = beginner,
            onClick = { onBeginnerChange(!beginner) },
            label = { Text("VB") })

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (0..17).forEach { v ->
                FilterChip(selected = !beginner && value == v, onClick = {
                    onValueChange(v)
                    onBeginnerChange(false) // deselect VB
                }, label = { Text("V$v") })
            }
        }

        // Plus toggle
        FilterChip(
            selected = plus,
            onClick = { onPlusChange(!plus) },
            enabled = !beginner,
            label = { Text("+") })
    }
}
