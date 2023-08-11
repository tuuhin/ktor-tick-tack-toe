package com.eva.tick_tack_toe.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStarted
import org.koin.ktor.plugin.KoinApplicationStopped
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules()
    }

    environment.monitor.subscribe(KoinApplicationStarted) {
        log.debug("Koin Started")
    }

    environment.monitor.subscribe(KoinApplicationStopped) {
        log.debug("Koin Stopped")
    }
}