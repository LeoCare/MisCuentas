package com.app.miscuentas.domain.model

import com.app.miscuentas.domain.Validaciones
import java.time.LocalDate

data class HojaCalculo(
    var id: Long,
    var titulo: String,
    private var _fechaCierre: LocalDate?,
    var limite: Double?,
    var status: String,
    private var _participantesHoja: MutableList<Participante> = mutableListOf()
){
    /** Asigna la fecha de tipo LocalDate **/
    var fechaCierre: String
        get() = _fechaCierre.toString()
        set(value){ _fechaCierre = Validaciones.fechaToDateFormat(value) }


    fun addListaParticipantes(participante: Participante){
        _participantesHoja.add(participante)
    }

    fun getTotalParticipantes(): Int{
        return _participantesHoja.size
    }

}
