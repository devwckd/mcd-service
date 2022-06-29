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
import me.devwckd.mcd_service.UpdateProxyRequest
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

            put {
                val id = call.parameters["id"]!!
                val updateProxyRequest = call.receive<UpdateProxyRequest>()
                val updateProxyResponse = proxyHandler.update(id, updateProxyRequest)

                call.respond(updateProxyResponse)
            }

            delete {
                val id = call.parameters["id"]!!
                proxyHandler.delete(id)

                call.respond(HttpStatusCode.OK)
            }

            post("heartbeat") {
                val id = call.parameters["id"]!!
                val proxyHeartbeatRequest: ProxyHeartbeatRequest = call.receive()
                proxyHandler.heartbeat(id, proxyHeartbeatRequest)

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}