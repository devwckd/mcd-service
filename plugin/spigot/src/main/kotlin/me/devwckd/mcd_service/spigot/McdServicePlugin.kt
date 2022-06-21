package me.devwckd.mcd_service.spigot

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin

class McdServicePlugin : SuspendingJavaPlugin() {

    val mcdClient = McdServerClient(this)

    override suspend fun onEnableAsync() {
        if (!mcdClient.create()) return
        mcdClient.startHeartbeatThread()
    }

    override suspend fun onDisableAsync() {
        mcdClient.delete()
    }
}