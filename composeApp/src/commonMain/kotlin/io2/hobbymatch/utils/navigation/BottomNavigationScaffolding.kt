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
import io2.hobbymatch.home.presentation.HomeScreen
import io2.hobbymatch.user.presentation.UserScreen

// --- Define desired shorter height ---
// Material 3 default is 80.dp. Try 64.dp or 56.dp
private val CustomBottomNavHeight = 80.dp

@Composable
fun BottomNavigationScaffolding() {
    // Let's start with HomeScreen as it's typically the first tab (index 0u)
    TabNavigator(HomeScreen()) { tabNavigator ->
        Scaffold(
            // The content lambda receives paddingValues calculated by Scaffold
            content = { paddingValues ->
                // Apply the paddingValues to a container wrapping your screen content
                Column(
                    modifier = Modifier
                        .padding(bottom = CustomBottomNavHeight)
                        .imePadding() // Apply padding here
                ) {
                    CurrentTab() // Render the content of the currently selected tab
                }
            },
            bottomBar = {
                NavigationBar(
                    // Apply the custom height modifier to the NavigationBar
                    modifier = Modifier.height(CustomBottomNavHeight)
                ) {
                    // Add your Tab items here
                    // Make sure HomeScreen and UserScreen implement Tab and provide options
                    TabNavigationItem(tab = HomeScreen())
                    TabNavigationItem(tab = UserScreen())
                    // Add more tabs if needed...
                    // TabNavigationItem(tab = SettingsScreen)
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
        // **Optional but Recommended: Add Labels**
        // Labels improve clarity but increase the minimum required height.
        // If you add labels, the forced `CustomBottomNavHeight` might look too cramped.
        // Consider removing the .height() modifier on NavigationBar if you use labels.
        //label = { Text(tab.options.title) },
        //
        // **Optional: Show label only for selected item?**
        alwaysShowLabel = true // Set to true to always show labels
        //
        // **Optional: Customize colors if needed**
        // colors = NavigationBarItemDefaults.colors(
        //     selectedIconColor = ...,
        //     unselectedIconColor = ...,
        //     selectedTextColor = ...,
        //     unselectedTextColor = ...,
        //     indicatorColor = ...
        // )
    )
}

//package io2.hobbymatch.utils.navigation
//
//import androidx.compose.foundation.layout.RowScope
//import androidx.compose.material3.Icon
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import cafe.adriel.voyager.navigator.tab.CurrentTab
//import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
//import cafe.adriel.voyager.navigator.tab.Tab
//import cafe.adriel.voyager.navigator.tab.TabNavigator
//import io2.hobbymatch.home.presentation.HomeScreen
//import io2.hobbymatch.user.presentation.UserScreen
//
//@Composable
//fun BottomNavigationScaffolding() {
//    TabNavigator(UserScreen()) {
//        Scaffold(
//            bottomBar = {
//                NavigationBar {
//                    TabNavigationItem(HomeScreen())
//                    TabNavigationItem(UserScreen())
//                }
//            }
//        ) {
//            CurrentTab()
//        }
//    }
//}
//
//@Composable
//private fun RowScope.TabNavigationItem(tab: Tab) {
//    val tabNavigator = LocalTabNavigator.current
//    NavigationBarItem(
//        selected = tabNavigator.current == tab,
//        onClick = { tabNavigator.current = tab },
//        icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title )},
//    )
//}