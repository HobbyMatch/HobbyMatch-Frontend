package io2.hobbymatch.utils.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import io2.hobbymatch.home.presentation.HomeScreen
import io2.hobbymatch.user.presentation.UserScreen

@Composable
fun BottomNavigationScaffolding() {
    TabNavigator(UserScreen()) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    TabNavigationItem(HomeScreen())
                    TabNavigationItem(UserScreen())
                }
            }
        ) {
            CurrentTab()
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title )},
    )
}