package me.devwckd.mcd_service.util

import io.ktor.server.application.*
import io.ktor.util.pipeline.*

fun PipelineContext<Unit, ApplicationCall>.getPaginationInfo() = PaginationInfo(
    this.call.request.queryParameters["page"]?.toInt() ?: 0,
    this.call.request.queryParameters["itemsPerPage"]?.toInt() ?: 15
)

data class PaginationInfo(
    val page: Int,
    val itemsPerPage: Int,
)