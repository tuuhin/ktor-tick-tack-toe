package com.eva.tick_tack_toe.plugins

import com.eva.tick_tack_toe.dto.BaseHttpException
import com.eva.tick_tack_toe.utils.constants.StatusPagesConstants
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status, message = BaseHttpException(detail = StatusPagesConstants.NOT_FOUND_DETAIL)
            )
        }
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respond(BaseHttpException(detail = StatusPagesConstants.INTERNAL_SERVER_ERROR))
        }
    }
}