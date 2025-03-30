package io2.hobbymatch.utils.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class ScaffoldingScreen : Screen {
    @Composable
    override fun Content() {
        BottomNavigationScaffolding()
    }
}