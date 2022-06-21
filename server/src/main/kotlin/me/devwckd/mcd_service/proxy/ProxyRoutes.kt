package me.devwckd.mcd_service.proxy

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import me.devwckd.mcd_service.CreateProxyRequest
import me.devwckd.mcd_service.ProxyHeartbeatRequest
import me.devwckd.mcd_service.util.getPaginationInfo
import org.koin.ktor.ext.inject
import java.time.Duration
import java.util.concurrent.Executors

fun Route.proxyRoutes() {
    val proxyHandler: ProxyHandler by inject()

    route("proxies") {
        get {
            val paginationInfo = getPaginationInfo()
            val listProxiesResponse = proxyHandler.listPaginated(paginationInfo)

            call.respond(listProxiesResponse)
        }

        post {
            val createProxyRequest = call.receive<CreateProxyRequest>()
            val createProxyResponse = proxyHandler.create(createProxyRequest)

            call.respond(HttpStatusCode.Created, createProxyResponse)
        }

        route("{id}") {
            get {
                val id = call.parameters["id"]!!
                val readProxyResponse = proxyHandler.read(id)

                call.respond(readProxyResponse)
            }

            delete {
                val id = call.parameters["id"]!!
                proxyHandler.delete(id)

                call.respond(HttpStatusCode.OK)
            }

            post("heartbeat") {
                val id = call.parameters["id"]!!
                val proxyHeartbeatRequest = call.receive<ProxyHeartbeatRequest>()
                proxyHandler.heartbeat(id, proxyHeartbeatRequest)

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}