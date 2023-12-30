package com.app.miscuentas.ui.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
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
    val registroState :Boolean by viewModel.registro.collectAsState()
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
            TextoLogin(registroState)
            CustomSpacer(24.dp)
            UsuarioField(statusUsuario) { viewModel.onUsuarioFieldChanged(it) }
            CustomSpacer(24.dp)
            ContrasennaField(statusContrasenna) { viewModel.onContrasennaFieldChanged(it) }
            CustomSpacer(24.dp)
            EmailField(
                registroState,
                statusEmail,
                onEmailFieldChange = { viewModel.onEmailFieldChanged(it) },
                onRegistroCheckChange = { viewModel.onRegistroCheckChanged(it) }
            )
            //CustomSpacer(30.dp)
            BotonInicio(
                registroState,
                mensajeClick) { viewModel.MensajeLoginClick() }

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
fun TextoLogin(registroState: Boolean) {
    val robotoBold = FontFamily(Font(R.font.roboto_bold))
    Text(
        text = "Registrar / Iniciar",
        fontSize = 20.sp,
        fontFamily = robotoBold,
        textAlign = TextAlign.Center
    )

    if( registroState){
        Text(
            text = stringResource(R.string.noPublicidad),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

//CAMPO USUARIO
@Composable
fun UsuarioField(usuario: String, onUsuarioFieldChanged: (String) -> Unit) {
    var isFocused by rememberSaveable { mutableStateOf(false) }
    TextField(
        value = usuario,
        onValueChange = { onUsuarioFieldChanged(it) }, //cada vez que el valor cambia, se llama a la funcion lambda, pasandole el valor actual (it). Este valor sera tratado en el viewModel.
        Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        placeholder = { Text(text = "usuario") },
        singleLine = true, //en una misma linea
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFF233CDA),
            backgroundColor = if (isFocused) Color(0xFFC0D6E7) else Color(0xFFC7CED3)
        )
    )
}

//CAMPO CONTRASEÑA
@Composable
fun ContrasennaField(contrasenna: String, onContrasennaFieldChanged: (String) -> Unit) {
    var isFocused by rememberSaveable { mutableStateOf(false) }
    TextField(
        value = contrasenna,
        onValueChange = { onContrasennaFieldChanged(it) },
        Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
        },
        placeholder = { Text(text = "contraseña") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(), //transforma el valor en *
        singleLine = true, //en una misma linea
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFF233CDA),
            backgroundColor = if (isFocused) Color(0xFFC0D6E7) else Color(0xFFC7CED3)
        )
    )
}

//CAMPO EMAIL
@Composable
fun EmailField(registroState: Boolean, email: String, onEmailFieldChange: (String) -> Unit, onRegistroCheckChange: (Boolean) -> Unit) {

    if (registroState){
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

    Row {
        Checkbox(
            checked = registroState,
            onCheckedChange = { onRegistroCheckChange(it) })
        Text(
            text = "Registrarme",
            modifier = Modifier.align(CenterVertically))
    }
}

//BOTON INICIO
@Composable
fun BotonInicio(registroState: Boolean, mensaje: String, loginOk: () -> Unit) {

    var texto = "INICIAR"
    val robotoBold = FontFamily(Font(R.font.roboto_bold))
    Button(
        onClick = { loginOk() },
        modifier = Modifier
            .height(60.dp)
            .width(190.dp)
    ) {

        if(registroState) texto = "REGISTRAR"
        Text(
            texto,
            fontSize = 20.sp,
            fontFamily = robotoBold)
    }
    Text(
        mensaje,
        fontSize = 20.sp,
        fontFamily = robotoBold,
        color = Color(color = 0xFFEE1808)
    )
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

