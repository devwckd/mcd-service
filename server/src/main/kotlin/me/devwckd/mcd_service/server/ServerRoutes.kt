package me.devwckd.mcd_service.server

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.devwckd.mcd_service.CreateServerRequest
import me.devwckd.mcd_service.ServerHeartbeatRequest
import me.devwckd.mcd_service.room.roomRoutes
import me.devwckd.mcd_service.util.getPaginationInfo
import org.koin.ktor.ext.inject

fun Route.serverRoutes() {
    val serverHandler: ServerHandler by inject()

    route("servers") {
        get {
            val paginationInfo = getPaginationInfo()
            val listServersResponse = serverHandler.listPaginated(paginationInfo)

            call.respond(listServersResponse)
        }

        post {
            val createServerRequest = call.receive<CreateServerRequest>()
            val createServerResponse = serverHandler.create(createServerRequest)

            call.respond(createServerResponse)
        }

        route("{id}") {
            get {
                val id = call.parameters["id"]!!
                val readServerResponse = serverHandler.read(id)

                call.respond(readServerResponse)
            }

            delete {
                val id = call.parameters["id"]!!
                serverHandler.delete(id)

                call.respond(HttpStatusCode.OK)
            }

            post("heartbeat") {
                val id = call.parameters["id"]!!
                val serverHeartbeatRequest = call.receive<ServerHeartbeatRequest>()
                serverHandler.heartbeat(id, serverHeartbeatRequest)

                call.respond(HttpStatusCode.OK)
            }

            roomRoutes()
        }
    }
}