package me.devwckd.mcd_service.server

class ServerManager {
    private val servers = hashMapOf<String, Server>()

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