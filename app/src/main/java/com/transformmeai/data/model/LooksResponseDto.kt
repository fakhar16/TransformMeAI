package com.transformmeai.data.model

import com.google.gson.annotations.SerializedName

data class LooksResponseDto(
    @SerializedName("images")
    val images: List<String>,
)
