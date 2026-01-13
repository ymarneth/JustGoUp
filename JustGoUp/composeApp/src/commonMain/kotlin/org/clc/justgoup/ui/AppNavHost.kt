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
import org.clc.justgoup.ui.climbingSessionDetail.SessionDetailScreen
import org.clc.justgoup.ui.climbingSessionDetail.addBoulder.AddBoulder
import org.clc.justgoup.ui.header.HeaderScreen
import org.clc.justgoup.ui.home.Home
import org.clc.justgoup.ui.home.addSession.AddSession
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode

// ---------- ROUTES ----------
@Serializable
object Home

@Serializable
data class SessionDetail(val sessionId: String)

@Serializable
object AddSession

@Serializable
data class AddBoulder(val sessionId: String)

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
                Home(
                    onStartSession = { nav.navigate(AddSession) },
                    onOpenSession = { id -> nav.navigate(SessionDetail(id)) }
                )
            }

            composable<SessionDetail> { backStack ->
                val args = backStack.toRoute<SessionDetail>()
                SessionDetailScreen(
                    onAddBoulder = { id -> nav.navigate(AddBoulder(id)) },
                    sessionId = args.sessionId
                )
            }

            composable<AddSession> {
                AddSession(
                    onOpenSession = { id ->
                        nav.navigate(SessionDetail(id)) {
                            popUpTo<AddSession> {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable<AddBoulder> { backStack ->
                val args = backStack.toRoute<AddBoulder>()
                AddBoulder(
                    onOpenSession = { id ->
                        nav.navigate(SessionDetail(id)) {
                            popUpTo<AddSession> {
                                inclusive = true
                            }
                        }
                    },
                    sessionId = args.sessionId
                )
            }
        }
    }
}
