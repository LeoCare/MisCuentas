package com.app.miscuentas.model

data class Participante(
    var id: Int,
    var nombre: String,
    var correo: String? = "", //se inicializa para simular una sobrecarga como en Java
    var listaGastos: List<Gasto>?
)
