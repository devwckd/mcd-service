package me.devwckd.mcd_service.proxy

class ProxyManager {
    private val proxies = hashMapOf<String, Proxy>()

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