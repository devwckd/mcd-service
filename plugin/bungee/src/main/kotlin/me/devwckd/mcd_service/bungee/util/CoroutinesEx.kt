package me.devwckd.mcd_service.bungee.util

import kotlinx.coroutines.delay

suspend fun repeatWithDelay(millis: Long, block: suspend () -> Unit) {
    while (true) {
        block()
        delay(millis)
    }
}