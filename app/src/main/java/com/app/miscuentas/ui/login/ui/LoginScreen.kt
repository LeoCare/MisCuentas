package com.app.miscuentas.ui.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.R

//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
@Preview
@Composable
fun Prev(){
    val navController = rememberNavController()
    Login(onNavigate = { navController.navigate("inicio") })
}

@Composable
fun Login(onNavigate: () -> Unit){

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(color = 0xFFF5EFEF))
    ) {
        LoginContent(Modifier.align(Alignment.Center), onNavigate)
    }
}


@Composable
fun LoginContent(modifier: Modifier, onNavigate: () -> Unit) {

    val viewModel: LoginViewModel = viewModel()
    //variables que delegan sus valores al cambio del viewModel
    val statusUsuario :String by viewModel.usuario.collectAsState()
    val statusContrasenna :String by viewModel.contrasenna.collectAsState()
    val statusEmail :String by viewModel.email.collectAsState()
    val mensajeClick :String by viewModel.mensaje.collectAsState()
    val loginState :Boolean by viewModel.login.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState) onNavigate()
    }

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        item {

            HeaderImage(modifier)
            CustomSpacer(40.dp)
            TextoLogin()
            CustomSpacer(24.dp)
            UsuarioField(statusUsuario) { viewModel.onUsuarioFieldChanged(it) }
            CustomSpacer(24.dp)
            ContrasennaField(statusContrasenna) { viewModel.onContrasennaFieldChanged(it) }
            CustomSpacer(24.dp)
            EmailField(statusEmail) { viewModel.onEmailFieldChanged(it) }
            CustomSpacer(30.dp)
            BotonInicio(mensajeClick) { viewModel.MensajeLoginClick() }

        }
    }
}


/** COMPONENTES **/
/** *********** **/

//IMAGEN LOGO
@Composable
fun HeaderImage(modifier: Modifier) {
    val robotoBlack = FontFamily(Font(R.font.roboto_black))
    Image(
        painter = painterResource(id = R.drawable.logologin),
        contentDescription = "Logo",
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
    )
    Text(
        text = "Mis Cuentas",
        Modifier.fillMaxWidth(),
        fontSize = 40.sp,
        fontFamily = robotoBlack,
        textAlign = TextAlign.Center
    )
}

//CAMPO TEXTO AVISO
@Composable
fun TextoLogin(){
    val robotoBold = FontFamily(Font(R.font.roboto_bold))
    Text(
        text = "Registrar / Iniciar",
        fontSize = 20.sp,
        fontFamily = robotoBold,
        textAlign = TextAlign.Center
    )

    Text(
        text = stringResource(R.string.noPublicidad),
        fontSize = 15.sp,
        textAlign = TextAlign.Center
    )
}

//CAMPO USUARIO
@Composable
fun UsuarioField(usuario: String, onUsuarioFieldChanged: (String) -> Unit) {

    TextField(
        value = usuario,
        onValueChange = { onUsuarioFieldChanged(it) }, //cada vez que el valor cambia, se llama a la funcion lambda, pasandole el valor actual (it). Este valor sera tratado en el viewModel.
        Modifier.fillMaxWidth(),
        placeholder = { Text(text = "usuario") },
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
fun ContrasennaField(contrasenna: String, onContrasennaFieldChanged: (String) -> Unit) {

    TextField(
        value = contrasenna,
        onValueChange = { onContrasennaFieldChanged(it) },
        Modifier.fillMaxWidth(),
        placeholder = { Text(text = "contraseña") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(), //transforma el valor en *
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
fun EmailField(email: String, onEmailFieldChange: (String) -> Unit) {

    TextField(
        value = email,
        onValueChange = { onEmailFieldChange(it) },
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
fun BotonInicio(mensaje: String, loginOk: () -> Unit) {
    val robotoBold = FontFamily(Font(R.font.roboto_bold))

    Text(
        mensaje,
        fontSize = 20.sp,
        fontFamily = robotoBold,
        color = Color(color = 0xFFEE1808)
    )
    Button(
        onClick = { loginOk() },
        modifier = Modifier
            .height(60.dp)
            .width(190.dp)
    ) {
        Text(
            "INICIAR",
            fontSize = 20.sp,
            fontFamily = robotoBold)
    }
}

//ESPACIADOR
@Composable
fun CustomSpacer(size: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
    )
}

