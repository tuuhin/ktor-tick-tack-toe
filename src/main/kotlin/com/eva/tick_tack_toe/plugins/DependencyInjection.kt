package com.eva.tick_tack_toe.plugins

import com.eva.tick_tack_toe.di.appModules
import com.eva.tick_tack_toe.feature_game.di.gameModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.KoinApplicationStarted
import org.koin.ktor.plugin.KoinApplicationStopped
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModules, gameModule)
    }

    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin Started")
    }

    environment.monitor.subscribe(KoinApplicationStopped) {
        log.info("Koin Stopped")
    }
}