@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.miscuentas.ui.login.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
private fun LoginContent(modifier: Modifier, onNavigate: () -> Unit) {

    //Uso de Hilt para el control de los estados del viewModel.
    val viewModel: LoginViewModel = hiltViewModel()
    val loginState by viewModel.loginState.collectAsState()

    // Estado para manejar mensajes de error al presionar Boton de inicio
    val uiErrorMessage = remember { mutableStateOf("") }

    //Si el login es correcto, navega a la siguiente pagina
    LaunchedEffect(loginState.loginOk) {
        if (loginState.loginOk) onNavigate()
    }

    // Actualiza el mensaje de error, al presionar el boton, si corresponde actualiza el estado de 'loginOk'.
    val onBotonInicioClick = {
        when {
            loginState.usuario.isEmpty() -> uiErrorMessage.value = "Falta usuario"
            !viewModel.contrasennaOk(loginState.contrasena) -> uiErrorMessage.value = "Pass con 6 dígitos mínimo (num, mayúsc. y minúsc.)"
            loginState.registro && !viewModel.emailOk(loginState.email) -> uiErrorMessage.value = "Email incorrecto"
            else -> {
                uiErrorMessage.value = ""
                viewModel.onLoginOkChanged(true)
            }
        }
        viewModel.onMensajeChanged(uiErrorMessage.value)
    }

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        item {

            //Imagen y texto
            HeaderImage(modifier)
            CustomSpacer(40.dp)

            TextoLogin(loginState.registro)
            CustomSpacer(24.dp)

            //TextFiedl Usuario
            CustomTextField(
                "Usuario",
                value = loginState.usuario
            ) { viewModel.onUsuarioFieldChanged(it) }
            CustomSpacer(24.dp)

            //TextFiedl Contraseña
            CustomTextField(
                "Contraseña",
                value = loginState.contrasena
            ) { viewModel.onContrasennaFieldChanged(it) }
            CustomSpacer(24.dp)

            //TextFiedl Email
            AnimatedVisibility(
                visible = loginState.registro,
                enter = expandIn(
                    animationSpec = tween(600, easing = EaseInOutBack),
                    expandFrom = Alignment.TopStart
                ),
                exit = shrinkOut(
                    tween(600, easing = EaseInBack),
                    shrinkTowards = Alignment.TopStart
                )
            ) {
                CustomTextField(
                    "Email",
                    value = loginState.email
                ) { viewModel.onEmailFieldChanged(it) }
            }

            //CheckBox Registro
            CustomCkeckbox(registroState = loginState.registro) {viewModel.onRegistroCheckChanged(it)
            }

            //Boton comprobacion
            BotonInicio(
                loginState.registro,
                loginState.mensaje,
                loginOk =  onBotonInicioClick)

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    placeholder: String,
    value: String,
    onTextFieldChange: (String) -> Unit
) {
    var isFocused by rememberSaveable { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = { onTextFieldChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        placeholder = { Text(text = placeholder) },
        keyboardOptions = when (placeholder) {
            "Email" -> KeyboardOptions(keyboardType = KeyboardType.Email)
            "Contraseña" -> KeyboardOptions(keyboardType = KeyboardType.Password)
            else -> KeyboardOptions(keyboardType = KeyboardType.Text)
        },
        visualTransformation = when (placeholder) {
            "Contraseña" -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },
        singleLine = true, //en una misma linea
        maxLines = 1,
        textStyle = TextStyle(
            fontSize = 20.sp
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = if (isFocused) Color(0xFFDFECF7) else Color(0xFFC0D6E7)
        )
    )
}

@Composable
fun CustomCkeckbox(
    registroState: Boolean,
    onRegistroCheckChange: (Boolean) -> Unit){

    Row {
        Checkbox(
            checked = registroState,
            onCheckedChange = { onRegistroCheckChange(it) },
            modifier = Modifier
                .padding(bottom = 7.dp)
        )
        Text(
            text = "Registrarme",
            modifier = Modifier
                .align(CenterVertically)
                .padding(bottom = 7.dp)
        )
    }
}

//BOTON INICIO
@Composable
fun BotonInicio(
    registroState: Boolean,
    mensaje: String,
    loginOk: () -> Unit
) {
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

