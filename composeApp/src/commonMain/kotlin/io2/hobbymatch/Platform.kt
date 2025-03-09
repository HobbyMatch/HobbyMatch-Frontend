package io2.hobbymatch

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform