package io2.hobbymatch.business.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

class BusinessScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<BusinessClientViewModel>()
        val businessClient by viewModel.state.collectAsState()

       Text("Nothing for now")
    }
}