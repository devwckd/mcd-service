package me.devwckd.mcd_service.proxy

import java.util.concurrent.ConcurrentHashMap

class ProxyManager {
    private val proxies = ConcurrentHashMap<String, Proxy>()

    fun getAll(): Collection<Proxy> {
        return proxies.values
    }

    fun getById(id: String): Proxy? {
        return proxies[id]
    }

    fun put(proxy: Proxy) {
        proxies[proxy.id] = proxy
    }

    fun remove(id: String) {
        proxies.remove(id)
    }
}