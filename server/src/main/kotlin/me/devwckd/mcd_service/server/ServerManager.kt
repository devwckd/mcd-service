package me.devwckd.mcd_service.server

import java.util.concurrent.ConcurrentHashMap

class ServerManager {
    private val servers = ConcurrentHashMap<String, Server>()

    fun getAll(): Collection<Server> {
        return servers.values
    }

    fun getById(id: String): Server? {
        return servers[id]
    }

    fun put(server: Server) {
        servers[server.id] = server
    }

    fun remove(id: String) {
        servers.remove(id)
    }
}