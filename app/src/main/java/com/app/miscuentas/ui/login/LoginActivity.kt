package com.app.miscuentas.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.miscuentas.R

@Preview()
@Composable
fun prev(){
    LoginContent(LoginViewModel())
}

@Composable
fun LoginContent(viewModel: LoginViewModel){
    Box(
        Modifier
            .fillMaxSize()
    ) {
        Login(Modifier.align(Alignment.Center), viewModel)
    }
}


@Composable
fun Login(align: Modifier, viewModel: LoginViewModel) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HeaderImage(align)

        Text(
            text = "Registrar / Iniciar",
            fontSize = 30.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.noPublicidad),
            fontSize = 18.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier

        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.White)
                .padding(top = 15.dp)
        )
        UsuarioField()

        ContraseñaField()

        EmailField()

        BotonInicio()

    }
}


/** COMPONENTES **/
/** *********** **/

//IMAGEN LOGO
@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.mis_hojas),
        contentDescription = "Logo",
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
    )
}

//CAMPO USUARIO
@Composable
fun UsuarioField() {
    TextField(
        value = "",
        onValueChange = {},
        Modifier.fillMaxWidth(),
        placeholder = { Text(text = "usuario")},
        singleLine = true, //en una misma linea
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFF233CDA),
            backgroundColor = Color(0xFFB1CDE2)
        )
    )
}

//CAMPO CONTRASEÑA
@Composable
fun ContraseñaField() {
    TextField(
        value = "",
        onValueChange = {},
        Modifier.fillMaxWidth(),
        placeholder = { Text(text = "contraseña")},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), //comprobara que la sintaxis sea correcta
        singleLine = true, //en una misma linea
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFF233CDA),
            backgroundColor = Color(0xFFB1CDE2)
        )
    )
}

//CAMPO EMAIL
@Composable
fun EmailField() {
    TextField(
        value = "",
        onValueChange = {},
        Modifier.fillMaxWidth(),
        placeholder = { Text(text = "email")},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), //comprobara que la sintaxis sea correcta
        singleLine = true, //en una misma linea
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFF233CDA),
            backgroundColor = Color(0xFFB1CDE2)
        )
    )
}

//BOTON INICIO
@Composable
fun BotonInicio() {
    Button(
        onClick = { /* TODO: Handle click */ },
        modifier = Modifier
            .height(60.dp)
            .width(190.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor =  Color(0xFFB3E4B5),
            disabledBackgroundColor = Color(0xFF87E61A)
        )

    ) {
        Text("INICIAR")
    }
}


