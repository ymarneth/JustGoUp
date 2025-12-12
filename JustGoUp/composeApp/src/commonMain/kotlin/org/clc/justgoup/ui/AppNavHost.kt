package org.clc.justgoup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import org.clc.justgoup.ui.climbingSession.SessionDetailScreen
import org.clc.justgoup.ui.header.HeaderScreen
import org.clc.justgoup.ui.home.HomeScreen
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode

// ---------- ROUTES ----------
@Serializable
object Home

@Serializable
data class SessionDetail(val sessionId: String)

// ---------- NAVIGATION HOST ----------
@Composable
fun AppNavHost(
    currentTheme: ThemeMode,
    onChangeTheme: (ThemeMode) -> Unit
) {
    val nav = rememberNavController()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoulderTheme.colors.background)
            .padding(horizontal = BoulderTheme.spacing.medium.dp)
    ) {
        HeaderScreen(
            currentTheme = currentTheme,
            onChangeTheme = onChangeTheme
        )

        NavHost(navController = nav, startDestination = Home) {
            composable<Home> {
                HomeScreen(
                    onStartSession = { /* TODO */ },
                    onOpenSession = { id -> nav.navigate(SessionDetail(id)) }
                )
            }

            composable<SessionDetail> { backStack ->
                val args = backStack.toRoute<SessionDetail>()
                SessionDetailScreen(
                    sessionId = args.sessionId,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}

