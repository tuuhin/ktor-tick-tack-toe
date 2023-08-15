package com.eva.tick_tack_toe.di

import com.eva.tick_tack_toe.utils.RoomAndPlayerServer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModules = module {
    singleOf(::RoomAndPlayerServer)
}
