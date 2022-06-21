package me.devwckd.mcd_service.bungee.util

import io.ktor.client.plugins.*
import io.ktor.client.request.*

fun noTimeoutRequestBuilder(): HttpRequestBuilder.() -> Unit = {
    timeout {
        connectTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
        socketTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
    }
}