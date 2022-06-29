package me.devwckd.mcd_service.transport

import java.util.concurrent.ConcurrentHashMap

class TransportManager {
    private val transports = ConcurrentHashMap<String, Transport>()

    fun put(transport: Transport) {
        transports[transport.id] = transport
    }

    fun getById(id: String): Transport? {
        return transports[id]
    }

    fun remove(transport: Transport) {
        transports.remove(transport.id)
    }
}