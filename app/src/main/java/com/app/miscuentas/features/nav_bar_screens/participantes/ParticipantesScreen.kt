package com.app.miscuentas.features.nav_bar_screens.participantes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

/** Contenedor del resto de elementos para la pestaña Participantes **/
@Composable
fun ParticipantesScreen(

) {
    Box{
        LazyColumn {
            item {

                Row(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Screen de Participantes",
                        textAlign = TextAlign.Center
                    )

                }
            }
        }
    }
}