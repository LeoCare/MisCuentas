package com.app.miscuentas.domain.dto

import com.google.gson.annotations.SerializedName

data class ImagenDto(
    @SerializedName("idImagen") val idImagen: Long,
    @SerializedName("imagen") val imagen: String // Puede ser Base64 o cualquier formato que estés usando
)

data class ImagenCrearDto(
    @SerializedName("imagen") val imagen: String // Puede ser Base64 o cualquier formato que estés usando
)