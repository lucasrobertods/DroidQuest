package com.lrds.androidroadapp

class JvmPlatform : Platform {
    override val name: String = "Desktop JVM (${System.getProperty("java.version")})"
}

actual fun getPlatform(): Platform = JvmPlatform()
