package com.eva.tick_tack_toe.dto

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class BaseHttpException(
    @EncodeDefault @SerialName("detail") val detail: String = ""
)