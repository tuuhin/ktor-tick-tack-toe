package com.eva.tick_tack_toe

import com.eva.tick_tack_toe.dto.BaseHttpResponse
import com.eva.tick_tack_toe.utils.constants.StatusPagesConstants
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RouteNotFoundTest {
    @Test
    fun `check if for a unknown route it sends a 404 not found expression`() = testApplication {
        val testClient = createClient {
            setNegotiationConfig()
        }

        val response = testClient.get("/")
        assertEquals(response.status, HttpStatusCode.NotFound, message = "This should produce not found status code")

        val body = response.body<BaseHttpResponse>()
        assertEquals(body.detail, StatusPagesConstants.NOT_FOUND_DETAIL, message = "Route Not found")

    }
}
