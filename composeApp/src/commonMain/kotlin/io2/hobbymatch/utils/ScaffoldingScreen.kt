package io2.hobbymatch.utils

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import io2.hobbymatch.utils.navigation.BottomNavigationScaffolding

class ScaffoldingScreen : Screen {
    @Composable
    override fun Content() {
        BottomNavigationScaffolding()
    }
}