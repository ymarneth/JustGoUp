package org.clc.justgoup.ui.climbingSessionDetail.addBoulder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import org.clc.justgoup.ui.theme.components.BoulderTextField
import org.clc.justgoup.ui.theme.components.ChipDivider
import org.clc.justgoup.ui.theme.components.SelectableChip

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
    var repeated by remember { mutableStateOf(false) }
    var color by remember { mutableStateOf<HoldColor?>(null) }
    var notes by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)) {
        Text(
            text = "Add Boulder",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        Text(
            text = "Grade",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(BoulderTheme.colors.surface)
        ) {
            SelectableChip(
                label = "French",
                value = GradingSystem.FRENCH,
                selectedValue = gradingSystem,
                onSelect = { gradingSystem = GradingSystem.FRENCH },
                modifier = Modifier.weight(1f)
            )
            ChipDivider()
            SelectableChip(
                label = "V-Scale",
                value = GradingSystem.V_SCALE,
                selectedValue = gradingSystem,
                onSelect = { gradingSystem = GradingSystem.V_SCALE },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

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
                onBeginnerChange = {
                    vGradeBeginner = it
                    vGradeValue = 0
                })
        }

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        Text(
            text = "Attempts",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        AttemptsSelector(
            value = attempts,
            onChange = {
                attempts = it
                if (it > 1) {
                    flash = false
                }
            })

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        Text(
            text = "Modifier",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        SentFlashChips(
            sent = sent,
            flash = flash,
            repeated = repeated,
            attempts = attempts,
            onSentChange = { sent = it },
            onFlashChange = { flash = it },
            onRepeatedChange = { repeated = it })

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        Text(
            text = "Hold Color",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        HoldColorPicker(
            selected = color,
            onSelected = { color = it }
        )

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        Text(
            text = "Notes (optional)",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        BoulderTextField(
            value = notes,
            onValueChange = { notes = it },
            placeholder = "Add thoughts about this boulder.",
            minLines = 2,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        BoulderButton(
            modifier = Modifier.fillMaxWidth(),
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
                            value = vGradeValue,
                            beginner = vGradeBeginner,
                            plus = vGradePlus
                        )
                    )
                }

                viewModel.addBoulderToSession(
                    grade = grade,
                    attempts = attempts,
                    sent = sent,
                    flash = flash,
                    repeated = repeated,
                    color = color,
                    notes = notes.ifBlank { null },
                    onBoulderAdded = onOpenSession
                )
            })

        Spacer(Modifier.height(BoulderTheme.spacing.extraLarge.dp))
    }
}

@Composable
fun HoldColorPicker(
    selected: HoldColor?,
    onSelected: (HoldColor?) -> Unit
) {
    val colors = HoldColor.entries.toTypedArray()

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp),
        verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp)
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
        horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.medium.dp)
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
    repeated: Boolean,
    attempts: Int,
    onSentChange: (Boolean) -> Unit,
    onFlashChange: (Boolean) -> Unit,
    onRepeatedChange: (Boolean) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SelectableChip(
            label = "Send",
            value = true,
            selectedValue = sent,
            onSelect = {
                onSentChange(!sent)
                if (sent) onFlashChange(false)
            }
        )

        SelectableChip(
            label = "Flash ⚡",
            value = true,
            selectedValue = flash,
            enabled = sent && attempts == 1,
            onSelect = { onFlashChange(!flash) }
        )

        SelectableChip(
            label = "Repeated",
            value = true,
            selectedValue = repeated,
            onSelect = { onRepeatedChange(!repeated) },
        )
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
    Column {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp),
            verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp)
        ) {
            (3..9).forEach { it ->
                SelectableChip(
                    label = it.toString(),
                    value = it,
                    selectedValue = number,
                    onSelect = { onNumberChange(it) }
                )
            }
        }

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp),
            verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp)
        ) {
            listOf('a', 'b', 'c').forEach { it ->
                SelectableChip(
                    label = it.toString(),
                    value = it,
                    selectedValue = letter,
                    onSelect = { onLetterChange(it) }
                )
            }

            SelectableChip(
                label = "+",
                value = true,
                selectedValue = plus,
                onSelect = { onPlusChange(!plus) }
            )
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
    Column {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp),
            verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp)
        ) {
            SelectableChip(
                label = "VB",
                value = true,
                selectedValue = beginner,
                onSelect = { onBeginnerChange(!beginner) }
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp),
            verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp)
        ) {
            (0..10).forEach { v ->
                SelectableChip(
                    label = "V$v",
                    value = v,
                    selectedValue = if (beginner) -1 else value,
                    onSelect = {
                        onValueChange(v)
                        onBeginnerChange(false) // deselect VB
                    }
                )
            }
        }

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        SelectableChip(
            label = "+",
            value = true,
            selectedValue = plus && !beginner,
            enabled = !beginner,
            onSelect = { onPlusChange(!plus) }
        )
    }
}
