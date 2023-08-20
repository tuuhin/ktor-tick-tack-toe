package com.eva.tick_tack_toe

import com.eva.tick_tack_toe.dto.BaseHttpException
import com.eva.tick_tack_toe.utils.constants.StatusPagesConstants
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RouteNotFoundTest {

    @Test
    fun `check if for a unknown route it sends a 404 not found expression`() = testApplication {
        createClient {
            install(ContentNegotiation) {
                json()
            }
        }.get {
            url {
                host = "localhost"
                port = 80
                path("/")
            }
        }.apply {
            assertEquals(status, HttpStatusCode.NotFound)
            body<BaseHttpException>().apply {
                assertEquals(detail, StatusPagesConstants.NOT_FOUND_DETAIL)
            }
        }

    }
}
