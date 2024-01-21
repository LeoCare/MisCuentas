package com.app.miscuentas.repository

import com.app.miscuentas.model.HojaCalculo
import java.time.LocalDate

class HojasProvider {

    //LISTA PROVISIONAL (DEBE SER OBTENIDA DE LA BBDD)
    companion object {
        private val listaHojas = mutableListOf(
            HojaCalculo(1, "Hoja 1", null, 100.0, "ACTIVA",ParticipantesProvider.listaParti),
            HojaCalculo(2, "Hoja 2", LocalDate.now(), 200.0, "FINALIZADA", ParticipantesProvider.listaParti),
            HojaCalculo(3, "Hoja 3", LocalDate.now(), 300.0, "FINALIZADA", ParticipantesProvider.listaParti),
            HojaCalculo(4, "Hoja 4", null, 400.0, "ACTIVA", ParticipantesProvider.listaParti),
            HojaCalculo(5, "Hoja 5", LocalDate.now(), 500.0, "FINALIZADA", ParticipantesProvider.listaParti)
        )

        init {
            // Ejemplo de uso de la propiedad fechaCierre
            listaHojas[1].fechaCierre = "30/03/2024"
//            listaHojas[3].fechaCierre = "17/02/2024"
        }

        fun getListHoja(): List<HojaCalculo>{
            return listaHojas
        }

    }


}