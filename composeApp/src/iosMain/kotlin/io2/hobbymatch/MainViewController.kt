package io2.hobbymatch

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController (
    configure = { initializeKoin() }
) { App() }