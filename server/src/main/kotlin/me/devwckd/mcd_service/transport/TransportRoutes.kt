package me.devwckd.mcd_service.transport

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.devwckd.mcd_service.CreateTransportRequest
import org.koin.ktor.ext.inject

fun Route.teleportRoutes() {
    val transportHandler: TransportHandler by inject()

    route("transports") {
        post {
            val createTransportRequest = call.receive<CreateTransportRequest>()
            val createTeleportResponse = transportHandler.create(createTransportRequest)

            call.respond(createTeleportResponse)
        }

        route("{id}") {
            get("authorize") {
                val id = call.parameters["id"]!!
                transportHandler.authorize(id)

                call.respond(HttpStatusCode.OK)
            }

            delete {
                val id = call.parameters["id"]!!
                transportHandler.delete(id)

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}