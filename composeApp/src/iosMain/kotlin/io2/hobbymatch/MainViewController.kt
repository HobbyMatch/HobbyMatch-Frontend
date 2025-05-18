package io2.hobbymatch

import androidx.compose.ui.window.ComposeUIViewController
import io2.hobbymatch.di.initializeKoin

fun MainViewController() = ComposeUIViewController (
    configure = { initializeKoin() }
) { App() }