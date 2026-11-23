package com.lrds.androidroadapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
