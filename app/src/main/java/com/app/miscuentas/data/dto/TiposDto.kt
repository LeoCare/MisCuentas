package com.app.miscuentas.data.dto

import com.google.gson.annotations.SerializedName

data class TipoBalanceDto(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("descripcion") val descripcion: String
)

data class TipoStatusDto(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("descripcion") val descripcion: String
)

data class TipoPerfilDto(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("descripcion") val descripcion: String
)