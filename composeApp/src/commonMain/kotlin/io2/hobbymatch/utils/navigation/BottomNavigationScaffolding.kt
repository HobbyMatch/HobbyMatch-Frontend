package io2.hobbymatch.utils.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import io2.hobbymatch.events.presentation.ActivityScreen
import io2.hobbymatch.home.presentation.HomeScreen
import io2.hobbymatch.user.presentation.UserScreen

private val CustomBottomNavHeight = 80.dp

@Composable
fun BottomNavigationScaffolding() {
    TabNavigator(HomeScreen()) { tabNavigator ->
        Scaffold(
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(bottom = CustomBottomNavHeight)
                        .imePadding()
                ) {
                    CurrentTab()
                }
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.height(CustomBottomNavHeight)
                ) {
                    TabNavigationItem(tab = HomeScreen())
                    TabNavigationItem(tab = ActivityScreen())
                    TabNavigationItem(tab = UserScreen())
                }
            }
        )
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(
        selected = tabNavigator.current.key == tab.key, // Robust selection check
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let { // Use safe call for icon painter
                Icon(painter = it, contentDescription = tab.options.title)
            }
        },
        alwaysShowLabel = true
    )
}