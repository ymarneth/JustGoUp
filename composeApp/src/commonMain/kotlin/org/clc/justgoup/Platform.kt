package org.clc.justgoup

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform