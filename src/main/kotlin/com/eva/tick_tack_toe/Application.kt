package com.eva.tick_tack_toe

import com.eva.tick_tack_toe.plugins.*
import io.ktor.server.application.*
import io.ktor.util.logging.*

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

val Logger = KtorSimpleLogger("com.eva.tick_tac_toe")
fun Application.module() {
    configureStatusPages()
    configureKoin()
    configureSecurity()
    configureMonitoring()
    configureSockets()
    configureRouting()
}
