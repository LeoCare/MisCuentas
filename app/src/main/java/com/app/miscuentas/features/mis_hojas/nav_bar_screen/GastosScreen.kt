package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.data.model.Gasto
import com.app.miscuentas.data.model.Hoja
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.util.MiAviso

/** Contenedor del resto de elementos para la pestaña Gastos **/


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GastosScreen(
    innerPadding: PaddingValues?,
    idHojaAMostrar: Int?,
    onNavNuevoGasto: (Int) -> Unit,
    viewModel: GastosViewModel = hiltViewModel()
) {
    val gastosState by viewModel.gastosState.collectAsState()
    val scaffoldState = rememberScaffoldState()

    val gasto = Gasto(20, "nombre", "ruta")
    var hojaDeGastos: HojaCalculo? = null

    //Hoja a mostrar pasada por el Screen Hojas (si es 0 es por defecto pasada por el NavBar)
    if (idHojaAMostrar != 0) viewModel.onHojaAMostrar(idHojaAMostrar)


    LaunchedEffect(Unit) {
        viewModel.getHojaCalculoPrincipal()

    }
    hojaDeGastos = gastosState.hojaAMostrar ?: gastosState.hojaPrincipal


    //Este aviso se lanzara cuando se deniega el permiso...
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialog) MiAviso(
        show = true,
        texto = "Tratar un aviso si los gastos no han sido guardados en BBDD, antes de salir atras."
    )
    { showDialog = false }
    Scaffold(
        scaffoldState = scaffoldState,
        content = {
            GastosContent(
                innerPadding,
                hojaDeGastos,
                { onNavNuevoGasto(it) },
                gasto
            )
        }
    )

}

@Composable
fun GastosContent(
    innerPadding: PaddingValues?,
    hojaDeGastos: HojaCalculo?,
    onNavNuevoGasto: (Int) -> Unit,
    gasto: Gasto
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding!!)
    ) {
        //Manejar la vuelta atras del usuario
//        BackHandler {
//            if (!gastosState.datosGuardados) {
//                showDialog = true
//                viewModel.setDatosGuardados(false)
//            }
//        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = hojaDeGastos?.titulo ?: "aun nada" ,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "principal",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Green
                )

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Fecha fin: " + (hojaDeGastos?.fechaCierre ?: "no tiene"),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "Limite: " + (hojaDeGastos?.limite ?: "no tiene"),
                    style = MaterialTheme.typography.labelLarge
                )

            }

            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)

            ) {
                item {
                    GastoDesing(gasto)
                    GastoDesing(gasto)
                    GastoDesing(gasto)
                    GastoDesing(gasto)
                    GastoDesing(gasto)
                    GastoDesing(gasto)
                    GastoDesing(gasto)
                    GastoDesing(gasto)
                    GastoDesing(gasto)
                    GastoDesing(gasto)


                    /*Prev -> LazyColumn{
                   items(gastosState.listaGastos){gasto ->
                       GastoDesing(gasto = gasto)

                   }
               }*/
                }
            }

        }

        CustomFloatButton(
            onNavNuevoGasto = { onNavNuevoGasto(hojaDeGastos!!.id) },
            modifier = Modifier.align(Alignment.BottomEnd) // Alinear el botón en la esquina inferior derecha
        )
    }
}

@Composable
fun GastoDesing(gasto: Gasto) {
    var isChecked by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
            .clip(MaterialTheme.shapes.extraSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Color.Black)
    ) {
        Column(
            modifier = Modifier
                .padding(start=10.dp, end=40.dp, bottom=10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically

            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.hoja), //IMAGEN DEL GASTO
                        contentDescription = "Logo Hoja",
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                    )
                }
                Column{
                    //Text(text = hoja.type)
                    Row(
                        modifier = Modifier
                            .padding(bottom = 15.dp)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Text(text = gasto.type) //NOMBRE DEL GASTO
                        Text(text = gasto.type) //FECHA DE CIERRE
                    }
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Text(text = gasto.type) //PARTICIPANTE
                        Text(text = gasto.price.toString()) //IMPORTE
                    }
                }
            }
        }
    }
}


@Composable
fun CustomFloatButton(
    onNavNuevoGasto: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = { onNavNuevoGasto() },
        modifier = modifier
            .height(90.dp)
            .width(90.dp)
            .padding(bottom = 14.dp, end = 14.dp), // Añade el padding al botón flotante
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Image(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inversePrimary),
            painter = painterResource(id = R.drawable.nuevo_gasto), //IMAGEN DEL GASTO
            contentDescription = "Logo Hoja",
        )
    }
}

//@Preview
//@Composable
//fun Preview(){
//    val innerPadding = PaddingValues()
//    val onNavNuevoGasto: (Int) -> Unit = {}
//    GastosScreen( innerPadding, {onNavNuevoGasto(3)})
//}
