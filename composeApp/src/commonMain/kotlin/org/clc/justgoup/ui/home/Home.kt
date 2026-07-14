package org.clc.justgoup.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Month
import org.clc.justgoup.climbingSession.RecentClimbingSession
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.koin.compose.koinInject
import org.clc.justgoup.ui.helpers.displayName
import org.clc.justgoup.ui.helpers.toDeviceLocalDateTime
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.BoulderButton
import org.clc.justgoup.ui.theme.components.ConfirmationCard
import org.clc.justgoup.ui.theme.components.SwipeItem

private const val LOAD_MORE_THRESHOLD = 5

private sealed interface SessionListItem {
    data class YearHeader(val year: Int) : SessionListItem
    data class MonthHeader(val year: Int, val month: Month) : SessionListItem
    data class Row(val session: RecentClimbingSession) : SessionListItem
}

private fun groupByYearAndMonth(sessions: List<RecentClimbingSession>): List<SessionListItem> = buildList {
    var lastYear: Int? = null
    var lastMonth: Month? = null

    sessions.forEach { session ->
        val local = session.date.toDeviceLocalDateTime()

        if (local.year != lastYear) {
            add(SessionListItem.YearHeader(local.year))
            lastYear = local.year
            lastMonth = null
        }
        if (local.month != lastMonth) {
            add(SessionListItem.MonthHeader(local.year, local.month))
            lastMonth = local.month
        }
        add(SessionListItem.Row(session))
    }
}

@Composable
private fun YearHeaderRow(year: Int) {
    Text(
        text = year.toString(),
        style = BoulderTheme.typography.titleLarge,
        color = BoulderTheme.colors.textPrimary
    )
}

@Composable
private fun MonthHeaderRow(month: Month) {
    Text(
        text = month.displayName(),
        style = BoulderTheme.typography.label,
        color = BoulderTheme.colors.textSecondary
    )
}

@Composable
private fun LoadingMoreRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(BoulderTheme.spacing.medium.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Loading more…",
            style = BoulderTheme.typography.label,
            color = BoulderTheme.colors.textSecondary
        )
    }
}

@Composable
fun Home(
    onStartSession: () -> Unit,
    onOpenSession: (String) -> Unit
) {
    val repository = koinInject<ClimbingSessionRepository>()
    val viewModel = remember { HomeViewModel(repository) }

    val recentSessions by viewModel.recentSessions.collectAsState(initial = emptyList())
    val isLoadingMore by viewModel.isLoadingMore.collectAsState(initial = false)

    val pendingDeleteId = remember { mutableStateOf<String?>(null) }
    val density = LocalDensity.current
    val groupedItems = groupByYearAndMonth(recentSessions)
    val listState = rememberLazyListState()

    val latestItemCount by rememberUpdatedState(groupedItems.size)
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= latestItemCount - LOAD_MORE_THRESHOLD) {
                    viewModel.loadMore()
                }
            }
    }

    Column {
        // ---- START NEW SESSION ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            BoulderButton(
                text = "Start Session",
                onClick = { onStartSession() },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        // ---- RECENT SESSIONS ----
        Text(
            text = "Recent Sessions",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.medium.dp),
            contentPadding = PaddingValues(bottom = BoulderTheme.spacing.extraLarge.dp)
        ) {
            items(
                items = groupedItems,
                key = { item ->
                    when (item) {
                        is SessionListItem.YearHeader -> "year-${item.year}"
                        is SessionListItem.MonthHeader -> "month-${item.year}-${item.month}"
                        is SessionListItem.Row -> item.session.id
                    }
                }
            ) { item ->
                when (item) {
                    is SessionListItem.YearHeader -> YearHeaderRow(item.year)
                    is SessionListItem.MonthHeader -> MonthHeaderRow(item.month)
                    is SessionListItem.Row -> {
                        val session = item.session
                        val isPending = pendingDeleteId.value == session.id
                        val cardHeight = remember { mutableStateOf(0.dp) }

                        if (isPending) {
                            ConfirmationCard(
                                message = "Delete this session?",
                                height = cardHeight.value,
                                onCancel = { pendingDeleteId.value = null },
                                onConfirm = {
                                    pendingDeleteId.value = null
                                    viewModel.deleteSession(session.id)
                                }
                            )
                        } else {
                            SwipeItem(
                                onSwipeLeft = { pendingDeleteId.value = session.id },
                                onSwipeRight = { pendingDeleteId.value = session.id }
                            ) {
                                SessionCard(
                                    session = session,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenSession(session.id) }
                                        .onGloballyPositioned { coordinates ->
                                            cardHeight.value = with(density) { coordinates.size.height.toDp() }
                                        }
                                )
                            }
                        }
                    }
                }
            }

            if (isLoadingMore) {
                item { LoadingMoreRow() }
            }
        }
    }
}
