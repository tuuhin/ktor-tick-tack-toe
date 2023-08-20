package com.eva.tick_tack_toe.utils.constants

import io.ktor.http.*

object StatusPagesConstants {
    val NOT_FOUND_DETAIL = "Route not found the server respond with :${HttpStatusCode.NotFound.value}"

    val INTERNAL_SERVER_ERROR = "Internal SERVER ERROR :${HttpStatusCode.InternalServerError}"
}