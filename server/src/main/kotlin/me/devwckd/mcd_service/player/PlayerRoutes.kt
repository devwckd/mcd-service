package me.devwckd.mcd_service.player

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.devwckd.mcd_service.CreatePlayerRequest
import me.devwckd.mcd_service.UpdatePlayerRequest
import me.devwckd.mcd_service.util.getPaginationInfo
import org.koin.ktor.ext.inject

fun Route.playerRoutes() {
    val playerHandler: PlayerHandler by inject()

    route("players") {
        get {
            val paginationInfo = getPaginationInfo()
            val listPlayersResponse = playerHandler.listPaginated(paginationInfo)

            call.respond(listPlayersResponse)
        }

        post {
            val createPlayerRequest: CreatePlayerRequest = call.receive()
            val createPlayerResponse = playerHandler.create(createPlayerRequest)

            call.respond(HttpStatusCode.Created, createPlayerResponse)
        }

        route("{term}") {
            get {
                val term = call.parameters["term"]!!
                val readPlayerResponse = playerHandler.read(term)

                call.respond(readPlayerResponse)
            }

            put {
                val term = call.parameters["term"]!!
                val updatePlayerRequest: UpdatePlayerRequest = call.receive()
                val updatePlayerResponse = playerHandler.update(term, updatePlayerRequest)

                call.respond(updatePlayerResponse)
            }

            delete {
                val term = call.parameters["term"]!!
                playerHandler.delete(term)

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}