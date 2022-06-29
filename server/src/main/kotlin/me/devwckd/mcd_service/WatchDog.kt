package me.devwckd.mcd_service

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.isDistantPast
import me.devwckd.mcd_service.proxy.ProxyHandler
import me.devwckd.mcd_service.proxy.ProxyManager
import me.devwckd.mcd_service.server.ServerHandler
import me.devwckd.mcd_service.server.ServerManager
import me.devwckd.mcd_service.subscriber.SubscriberManager
import me.devwckd.mcd_service.util.globalInject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

class WatchDog : Thread() {

    companion object {
        private val threshold: Duration = 2000.milliseconds
    }

    private val proxyManager: ProxyManager by globalInject()
    private val proxyHandler: ProxyHandler by globalInject()

    private val serverManager: ServerManager by globalInject()
    private val serverHandler: ServerHandler by globalInject()

    override fun run() {
        runBlocking {
            while (true) {
                val now = Clock.System.now()

                proxyManager.getAll().filter {
                    val proxyHealth = it.proxyHealth
                    !proxyHealth.lastHeartbeat.isDistantPast && now - proxyHealth.lastHeartbeat > threshold
                }.forEach {
                    proxyHandler.doRemove(it)
                }

                serverManager.getAll().filter {
                    val serverHealth = it.serverHealth
                    !serverHealth.lastHeartbeat.isDistantPast && now - serverHealth.lastHeartbeat > threshold
                }.forEach {
                    serverHandler.doRemove(it)
                }

                delay(100)
            }
        }
    }
}