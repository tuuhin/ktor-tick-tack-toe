package com.eva.tick_tack_toe

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun HttpClientConfig<out HttpClientEngineConfig>.setNegotiationConfig() {
    install(ContentNegotiation) {
        json()
    }
    headers {
        append(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}

fun HttpClientConfig<out HttpClientEngineConfig>.setWebsocketConfig() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    headers {
        append(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}