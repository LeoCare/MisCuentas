package com.app.miscuentas.repository

import com.app.miscuentas.model.Participante

class ParticipantesProvider {

    //LISTA PROVISIONAL (DEBE SER OBTENIDA DE LA BBDD)
    companion object{

        private val parti1 = Participante(1,"Leo",null,null)
        private val parti2 = Participante(2,"Ana","ana.correo@gmail.com",null)
        private val parti3 = Participante(3,"Samy",null,null)

        val listaParti = mutableListOf(parti1, parti2, parti3)


    }
}