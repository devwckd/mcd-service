package me.devwckd.mcd_service.proxy

import kotlinx.datetime.Clock
import me.devwckd.mcd_service.*
import me.devwckd.mcd_service.subscriber.SubscriberManager
import me.devwckd.mcd_service.util.PaginationInfo
import me.devwckd.mcd_service.util.generateProxyId
import kotlin.math.ceil

class ProxyHandler(
    private val proxyManager: ProxyManager,
    private val subscriberManager: SubscriberManager
) {

    fun listPaginated(
        paginationInfo: PaginationInfo,
    ): ListProxiesResponse {
        val (page, itemsPerPage) = paginationInfo
        val servers = proxyManager.getAll()

        return servers
            .drop(page * itemsPerPage)
            .take(itemsPerPage)
            .map { proxy -> ProxyInfo(proxy.id, proxy.ip, proxy.port) }
            .let {
                Paginated(page, ceil(servers.size.toDouble() / itemsPerPage).toInt(), itemsPerPage, it)
            }
    }

    suspend fun create(
        createProxyRequest: CreateProxyRequest
    ): CreateProxyResponse {
        val proxy = Proxy(newProxyId(), createProxyRequest.ip, createProxyRequest.port)
        proxyManager.put(proxy)

        val proxyInfo = ProxyInfo(proxy.id, proxy.ip, proxy.port)
        subscriberManager.broadcast(ProxyCreatedEvent(proxyInfo))

        return proxyInfo
    }

    fun read(
        id: String
    ): ReadProxyResponse {
        val proxy = proxyManager.getById(id) ?: throw ProxyNotFoundException("proxy with id $id not found.")
        return ProxyInfo(proxy.id, proxy.ip, proxy.port)
    }

    fun heartbeat(
        id: String,
        proxyHeartbeatRequest: ProxyHeartbeatRequest
    ) {
        val proxy = proxyManager.getById(id) ?: throw ProxyNotFoundException("proxy with id $id not found.")

        proxy.proxyHealth.pushHeartbeatPing(System.currentTimeMillis() - proxyHeartbeatRequest.sentAt)
        proxy.proxyHealth.lastHeartbeat = Clock.System.now()
    }

    suspend fun delete(
        id: String
    ) {
        val proxy = proxyManager.getById(id) ?: throw ProxyNotFoundException("proxy with id $id not found.")
        subscriberManager.broadcast(ProxyDeletedEvent(ProxyInfo(proxy.id, proxy.ip, proxy.port)))

        proxyManager.remove(id)
    }

    private fun newProxyId(): String {
        var id: String
        do {
            id = generateProxyId()
        } while (proxyManager.getById(id) != null)

        return id
    }
}