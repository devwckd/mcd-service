package me.devwckd.mcd_service.bungee.util

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun ProxiedPlayer.connectAsync(serverInfo: ServerInfo): Boolean = suspendCoroutine {
    this.connect(serverInfo) { result, throwable ->
        if(throwable != null) {
            it.resumeWithException(throwable)
            return@connect
        }

        it.resume(result)
    }
}