import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.transitions.FadeTransition
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import kmpnavscreen.composeapp.generated.resources.Res
import kmpnavscreen.composeapp.generated.resources.compose_multiplatform
import screen.home.HomeScreen
import screen.tab.home.HomeTab
import screen.tab.profile.ProfileTab
import screen.tab.settings.SettingsTabTab

@Composable
@Preview
fun App() {
    MaterialTheme {
        // handle Tab Navigator
        TabNavigator(HomeTab) {
            Scaffold(
                bottomBar = {
                    BottomNavigation {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(ProfileTab)
                        TabNavigationItem(SettingsTabTab)
                    }
                }
            ) {
                CurrentTab()
            }
        }
        // handle navigate beetwen screen
//        Navigator(HomeScreen()) {navigator ->  
//            FadeTransition(navigator)
//        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        label = { Text(tab.options.title) },
        icon = {}
    )
}