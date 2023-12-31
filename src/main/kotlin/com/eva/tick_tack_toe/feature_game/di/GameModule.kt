package com.eva.tick_tack_toe.feature_game.di

import com.eva.tick_tack_toe.feature_game.game.RealtimeBoardGame
import com.eva.tick_tack_toe.feature_game.utils.ServerSendUtilities
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val gameModule = module {

    factoryOf(::ServerSendUtilities)

    factoryOf(::RealtimeBoardGame)
}