package com.eva.tick_tack_toe.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseHttpResponse(
    @SerialName("detail") val detail: String
)