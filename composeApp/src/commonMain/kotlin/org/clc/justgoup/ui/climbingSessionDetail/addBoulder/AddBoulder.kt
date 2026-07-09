package org.clc.justgoup.ui.climbingSessionDetail.addBoulder

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.clc.justgoup.boulder.FrenchGrade
import org.clc.justgoup.boulder.Grade
import org.clc.justgoup.boulder.GradingSystem
import org.clc.justgoup.boulder.HoldColor
import org.clc.justgoup.boulder.VGrade
import org.clc.justgoup.boulder.frenchGradeSequence
import org.clc.justgoup.boulder.toColor
import org.clc.justgoup.boulder.vGradeSequence
import org.clc.justgoup.di.provideClimbingSessionRepository
import org.clc.justgoup.di.provideGradingSystemPreference
import org.clc.justgoup.di.provideLastGradePreference
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
    val gradingSystemPreference = provideGradingSystemPreference()
    val lastGradePreference = provideLastGradePreference()
    val viewModel = remember {
        AddBoulderViewModel(repository, sessionId, gradingSystemPreference, lastGradePreference)
    }

    val scrollState = rememberScrollState()

    val gradingSystem by viewModel.gradingSystem.collectAsState()
    val initialFrenchGrade = remember { viewModel.lastFrenchGrade() }
    val initialVGrade = remember { viewModel.lastVGrade() }
    var gradeNumber by remember { mutableStateOf(initialFrenchGrade.number) }
    var gradeLetter by remember { mutableStateOf(initialFrenchGrade.letter) }
    var gradePlus by remember { mutableStateOf(initialFrenchGrade.modifier == FrenchGrade.Modifier.Plus) }
    var vGradeValue by remember { mutableStateOf(initialVGrade.value) }
    var vGradePlus by remember { mutableStateOf(initialVGrade.plus) }
    var vGradeBeginner by remember { mutableStateOf(initialVGrade.beginner) }
    var showAllGrades by remember { mutableStateOf(false) }

    var attempts by remember { mutableStateOf(1) }
    var sent by remember { mutableStateOf(false) }
    var flash by remember { mutableStateOf(false) }
    var repeated by remember { mutableStateOf(false) }
    var color by remember { mutableStateOf<HoldColor?>(HoldColor.entries[HoldColor.entries.size / 2]) }
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
                onSelect = {
                    viewModel.updateGradingSystem(GradingSystem.FRENCH)
                    showAllGrades = false
                },
                modifier = Modifier.weight(1f)
            )
            ChipDivider()
            SelectableChip(
                label = "V-Scale",
                value = GradingSystem.V_SCALE,
                selectedValue = gradingSystem,
                onSelect = {
                    viewModel.updateGradingSystem(GradingSystem.V_SCALE)
                    showAllGrades = false
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        when {
            gradingSystem == GradingSystem.FRENCH && !showAllGrades -> {
                val selected = FrenchGrade(number = gradeNumber, letter = gradeLetter)

                GradeCarousel(
                    items = remember { frenchGradeSequence() },
                    selected = selected,
                    onSelect = {
                        gradeNumber = it.number
                        gradeLetter = it.letter
                    }
                ) { grade, isSelected ->
                    GradeChipContent(
                        label = if (grade == selected && gradePlus) {
                            grade.copy(modifier = FrenchGrade.Modifier.Plus).toString()
                        } else {
                            grade.toString()
                        },
                        isSelected = isSelected
                    )
                }
            }

            gradingSystem == GradingSystem.FRENCH -> FrenchGradePicker(
                number = gradeNumber,
                letter = gradeLetter,
                plus = gradePlus,
                onNumberChange = { gradeNumber = it },
                onLetterChange = { gradeLetter = it },
                onPlusChange = { gradePlus = it })

            gradingSystem == GradingSystem.V_SCALE && !showAllGrades -> {
                val selected = VGrade(value = vGradeValue, beginner = vGradeBeginner)

                GradeCarousel(
                    items = remember { vGradeSequence() },
                    selected = selected,
                    onSelect = {
                        vGradeValue = it.value
                        vGradeBeginner = it.beginner
                    }
                ) { grade, isSelected ->
                    GradeChipContent(
                        label = if (grade == selected && vGradePlus) {
                            grade.copy(plus = true).toString()
                        } else {
                            grade.toString()
                        },
                        isSelected = isSelected
                    )
                }
            }

            else -> VScalePicker(
                value = vGradeValue,
                plus = vGradePlus,
                beginner = vGradeBeginner,
                onValueChange = { vGradeValue = it },
                onPlusChange = { vGradePlus = it },
                onBeginnerChange = { vGradeBeginner = it }
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        if (showAllGrades) {
            Text(
                text = "Show less",
                style = BoulderTheme.typography.body,
                color = BoulderTheme.colors.textSecondary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { showAllGrades = false }
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (gradingSystem) {
                    GradingSystem.FRENCH -> SelectableChip(
                        label = "+",
                        value = true,
                        selectedValue = gradePlus,
                        onSelect = { gradePlus = !gradePlus }
                    )

                    GradingSystem.V_SCALE -> SelectableChip(
                        label = "+",
                        value = true,
                        selectedValue = vGradePlus && !vGradeBeginner,
                        enabled = !vGradeBeginner,
                        onSelect = { vGradePlus = !vGradePlus }
                    )
                }

                Text(
                    text = "Show all",
                    style = BoulderTheme.typography.body,
                    color = BoulderTheme.colors.textSecondary,
                    modifier = Modifier.clickable { showAllGrades = true }
                )
            }
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
                flash = sent && it == 1
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
    GradeCarousel(
        items = HoldColor.entries,
        selected = selected,
        onSelect = onSelected,
        itemWidth = 56.dp,
        itemHeight = 56.dp
    ) { color, isSelected ->
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color?.toColor() ?: BoulderTheme.colors.surface)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) BoulderTheme.colors.primary else BoulderTheme.colors.textSecondary,
                    shape = CircleShape
                )
        )
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
                val newSent = !sent
                onSentChange(newSent)
                onFlashChange(newSent && attempts == 1)
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

private const val CAROUSEL_JUMP_AMOUNT = 3

/**
 * A horizontally swipeable, snap-to-center picker, with < / > buttons that jump by
 * [CAROUSEL_JUMP_AMOUNT] items. Whichever item is centered is always the selection --
 * live, continuously, while swiping or animating, not just once a gesture settles.
 * [items] should be a small, finite, already-ordered sequence (e.g. every French
 * number+letter combination).
 */
@Composable
fun <T> GradeCarousel(
    items: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemWidth: Dp = 72.dp,
    itemHeight: Dp = 48.dp,
    itemContent: @Composable (item: T, isSelected: Boolean) -> Unit
) {
    val selectedIndex = remember(items) { items.indexOf(selected).coerceAtLeast(0) }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val flingBehavior = rememberSnapFlingBehavior(listState)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            layoutInfo.visibleItemsInfo.minByOrNull { info ->
                kotlin.math.abs((info.offset + info.size / 2) - viewportCenter)
            }?.index
        }.collect { centeredIndex ->
            if (centeredIndex != null) {
                items.getOrNull(centeredIndex)?.let(onSelect)
            }
        }
    }

    fun jump(amount: Int) {
        val currentIndex = items.indexOf(selected).coerceAtLeast(0)
        val targetIndex = (currentIndex + amount).coerceIn(0, items.lastIndex)
        coroutineScope.launch { listState.animateScrollToItem(targetIndex) }
    }

    val currentIndex = items.indexOf(selected).coerceAtLeast(0)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp)
    ) {
        BoulderButton(
            text = "<",
            enabled = currentIndex > 0,
            onClick = { jump(-CAROUSEL_JUMP_AMOUNT) },
            modifier = Modifier.width(48.dp)
        )

        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val sidePadding = ((maxWidth - itemWidth) / 2).coerceAtLeast(0.dp)

            LazyRow(
                state = listState,
                flingBehavior = flingBehavior,
                contentPadding = PaddingValues(horizontal = sidePadding),
                horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp)
            ) {
                itemsIndexed(items) { index, item ->
                    Box(
                        modifier = Modifier
                            .width(itemWidth)
                            .height(itemHeight)
                            .clickable {
                                coroutineScope.launch { listState.animateScrollToItem(index) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        itemContent(item, item == selected)
                    }
                }
            }
        }

        BoulderButton(
            text = ">",
            enabled = currentIndex < items.lastIndex,
            onClick = { jump(CAROUSEL_JUMP_AMOUNT) },
            modifier = Modifier.width(48.dp)
        )
    }
}

/** The rounded, primary/surface-filled chip used for text-based grade carousel items. */
@Composable
fun GradeChipContent(label: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) BoulderTheme.colors.primary else BoulderTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = BoulderTheme.typography.titleMedium,
            color = if (isSelected) BoulderTheme.colors.textPrimary else BoulderTheme.colors.textSecondary
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
                        onBeginnerChange(false)
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
