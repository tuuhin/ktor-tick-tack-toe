package com.eva.tick_tack_toe

import com.eva.tick_tack_toe.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureKoin()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureSockets()
    configureRouting()
}
