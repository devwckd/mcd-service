package me.devwckd.mcd_service.room

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.devwckd.mcd_service.CreateRoomRequest
import me.devwckd.mcd_service.util.getPaginationInfo
import org.koin.ktor.ext.inject

fun Route.roomRoutes() {
    val roomHandler: RoomHandler by inject()

    route("rooms") {
        get {
            val serverId = call.parameters["id"]!!
            val paginationInfo = getPaginationInfo()
            val listRoomsResponse = roomHandler.listPaginated(serverId, paginationInfo)

            call.respond(listRoomsResponse)
        }

        post {
            val serverId = call.parameters["id"]!!
            val createRoomRequest = call.receive<CreateRoomRequest>()
            val createRoomResponse = roomHandler.create(serverId, createRoomRequest)

            call.respond(HttpStatusCode.Created, createRoomResponse)
        }
    }
}