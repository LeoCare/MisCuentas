@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.miscuentas.features.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.util.BiometricAuthenticator
import com.app.miscuentas.util.Desing.Companion.RecuperarContrasenaDialog
import com.app.miscuentas.util.Validaciones.Companion.contrasennaOk
import com.app.miscuentas.util.Validaciones.Companion.emailCorrecto
import kotlinx.coroutines.launch
import kotlin.math.log

/** Composable principal de la Screen **/
@Composable
fun Login(
    innerPadding: PaddingValues,
    onNavigate: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
){

    val controlTeclado = LocalSoftwareKeyboardController.current
    val loginState by viewModel.loginState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color(color = 0xFFF5EFEF))
            .pointerInput(Unit) { //Oculta el teclado al colocar el foco en la caja
                detectTapGestures(onPress = {
                    controlTeclado?.hide()
                    awaitRelease()
                })
            }
    ) {
        LoginContent(
            Modifier.align(Alignment.Center),
            loginState,
            onNavigate,
            viewModel::onIsLoadingOkChanged,
            viewModel::onBiometricAuthenticationSuccess,
            viewModel::onBiometricAuthenticationFailed,
            viewModel::onMensajeChanged,
            viewModel::onUsuarioFieldChanged,
            viewModel::onContrasennaFieldChanged,
            viewModel::onRepitaContrasennaFieldChanged,
            viewModel::onEmailFieldChanged,
            viewModel::onRegistroCheckChanged,
            viewModel::iniciarSesion,
            viewModel::inicioInsertRegistro,
            viewModel::onEnviarCorreo,
            viewModel::onVerifyCodigoRecupChanged,
            viewModel::comprobarCodigoRecup,
            viewModel::onRepetirPassChanged,
            viewModel::updatePass
        )
    }
}

/** Contenedor del resto de elementos para Login **/
@Composable
private fun LoginContent(
    modifier: Modifier,
    loginState: LoginState,
    onNavigate: () -> Unit,
    onIsLoadingOkChanged: (Boolean) -> Unit,
    bioAuthSuccess: () -> Unit,
    bioAuthFailed: () -> Unit,
    mensajeChanged: (String) -> Unit,
    onUsuarioFieldChanged: (String) -> Unit,
    onContrasennaFieldChanged: (String) -> Unit,
    onRepitaContrasennaFieldChanged: (String) -> Unit,
    onEmailFieldChanged: (String) -> Unit,
    onRegistroCheckChanged: (Boolean) -> Unit,
    iniciarSesion: () -> Unit,
    inicioInsertRegistro: () -> Unit,
    onEnviarCorreo: (String) -> Unit,
    onVerifyCodigoRecupChanged: (String) -> Unit,
    comprobarCodigoRecup: (String, String) -> Unit,
    onRepetirPassChanged: (Boolean) -> Unit,
    updatePass: () -> Unit
) {

    //Inicio por huella digital
    val context = LocalContext.current
    val biometricAuthenticator = BiometricAuthenticator(context)
    val activity = LocalContext.current as FragmentActivity

    // Estado para manejar mensajes de error al presionar Boton de inicio
    val uiErrorMessage = remember { mutableStateOf("") }


    //Uso de la huella digita
    LaunchedEffect(loginState.biometricAuthenticationState) {
        when (loginState.biometricAuthenticationState) {
            is LoginState.BiometricAuthenticationState.Authenticating -> {
                // Iniciar lector huella
                biometricAuthenticator.promptBiometricAuth(
                    fragmentActivity = activity,
                    onSuccess = {
                        bioAuthSuccess()
                        onNavigate()
                    },
                    onError = { bioAuthFailed() },
                    onFailure = { bioAuthFailed() }
                )
            }
            else -> {}
        }
    }

    //Si el login es correcto, navega a la siguiente pagina
    LaunchedEffect(loginState.loginOk) {
        if (loginState.loginOk){ onNavigate() }
    }


    // Actualiza el mensaje de error, al presionar el boton, si corresponde actualiza el estado de 'loginOk'.
    val onBotonInicioClick = {
        when { //Mensajes de error:
            !emailCorrecto(loginState.email) -> uiErrorMessage.value = "Email incorrecto"
            !contrasennaOk(loginState.contrasenna) -> uiErrorMessage.value = "Pass con 6 dígitos mínimo (num, mayúsc. y minúsc.)"
            loginState.registro && loginState.usuario.isEmpty() -> uiErrorMessage.value = "Falta usuario"

            //Si los campos son correctos...
            else -> {
                //MODO REGISTRO
                if (loginState.registro) {
                    onIsLoadingOkChanged(true)
                    uiErrorMessage.value = ""
                    inicioInsertRegistro() //inserta el registro
                    onIsLoadingOkChanged(false)
                }
                //MODO RECUPERAR PASS
                else if(loginState.repetirPass){
                    if(loginState.contrasenna != loginState.repitaContrasenna){
                        uiErrorMessage.value = "Las contraseñas no coinciden."
                    }
                    else {
                        uiErrorMessage.value = ""
                        updatePass() //inserta la nueva pass
                    }
                }
                //MODO LOGIN
                else {
                    uiErrorMessage.value = ""
                    iniciarSesion() //Comprueba si el login es correcto (ya esta registrado)
                }
            }
        }
        mensajeChanged(uiErrorMessage.value)
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item {
            //Imagen y texto
            HeaderImage(modifier)
            CustomSpacer(40.dp)

            TextoLogin(
                loginState.registro,
                loginState.isLoading
            )
            CustomSpacer(24.dp)

            //TextFiedl Email
            CustomTextField(
                "Email",
                value = loginState.email
            ) { onEmailFieldChanged(it) }
            CustomSpacer(24.dp)

            //TextFiedl Contraseña
            CustomTextField(
                placeholder = if(loginState.repetirPass) "Nueva Contraseña" else "Contraseña",
                value = loginState.contrasenna
            ) { onContrasennaFieldChanged(it) }
            CustomSpacer(24.dp)

            //TextFiedl Usuario
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
                    "Nombre de usuario",
                    value = loginState.usuario
                ) { onUsuarioFieldChanged(it) }
            }

            //TextFiedl Repetir contraseña
            AnimatedVisibility(
                visible = loginState.repetirPass,
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
                    placeholder = "Repita Contraseña",
                    value = loginState.repitaContrasenna
                ) { onRepitaContrasennaFieldChanged(it) }
            }

            //CheckBox Registro
            CustomCkeckbox(
                registroState = loginState.registro
            ) {
                mensajeChanged("")
                onRegistroCheckChanged(it)
            }

            //Recurar la contraseña
            RecuperarContrasenna(
                onEnviarCorreo,
                onVerifyCodigoRecupChanged,
                comprobarCodigoRecup,
                onRepetirPassChanged,
                loginState.verifyCodigoRecup,
                loginState.repetirPass
            )

            //Boton comprobacion
            BotonInicio(
                loginState.registro,
                loginState.repetirPass,
                loginState.mensaje,
                onBotonInicioClick =  onBotonInicioClick)
        }
    }
}


/** Composable para la imagen del logo **/
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

/** Composable para el texto inicial **/
@Composable
fun TextoLogin(
    registroState: Boolean,
    loginState: Boolean
) {
    if(loginState){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(44.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    else {
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

}

/** Composable para la creacion de los TextField **/
@Composable
fun CustomTextField(
    placeholder: String,
    value: String,
    onTextFieldChange: (String) -> Unit
) {
    var isFocused by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
        visualTransformation = when {
            placeholder == "Contraseña" && !passwordVisible -> PasswordVisualTransformation()
            placeholder == "Repita Contraseña" && !passwordVisible -> PasswordVisualTransformation()
            placeholder == "Nueva Contraseña" && !passwordVisible -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },
        trailingIcon = {
            if (placeholder == "Contraseña" || placeholder == "Repita Contraseña" || placeholder == "Nueva Contraseña") {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
        },
        singleLine = true, //en una misma linea
        maxLines = 1,
        textStyle = TextStyle(
            fontSize = 20.sp,
            color = Color.Blue
        ),
        colors = TextFieldDefaults.colors(
             if (isFocused) Color(0xFFDFECF7) else Color(0xFFC0D6E7)
        )
    )
}

/** Composable para el TextBox de opcion a registro **/
@Composable
fun CustomCkeckbox(
    registroState: Boolean,
    onRegistroCheckChange: (Boolean) -> Unit
){

    Row {
        Checkbox(
            checked = registroState,
            onCheckedChange = { onRegistroCheckChange(it) },
            modifier = Modifier
                .padding(bottom = 3.dp)
        )
        Text(
            text = "Registrarme",
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(CenterVertically)
                .padding(bottom = 3.dp)
        )
    }
}


/** Recuperar contraseña **/
@Composable
fun RecuperarContrasenna(
    onEnviarCorreo: (String) -> Unit,
    onVerifyCodigoRecupChanged: (String) -> Unit,
    comprobarCodigoRecup: (String, String) -> Unit,
    onRepetirPassChanged: (Boolean) -> Unit,
    verifyCodigoRecup: String,
    repetirPass: Boolean,
){
    var showDialog by remember { mutableStateOf(false) }
    var codigoStatus by rememberSaveable { mutableStateOf("") }
    if(verifyCodigoRecup.isNotEmpty()) codigoStatus = verifyCodigoRecup

    if(repetirPass){
        Text(
            text = "Cancelar",
            color = Color.Blue.copy(alpha = 1.2f),
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .padding(bottom = 35.dp)
                .clickable {
                    onRepetirPassChanged(false)
                }
        )
    }
    else {
        Text(
            text = "Recuperar contraseña",
            color = Color.Blue.copy(alpha = 1.2f),
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .padding(bottom = 35.dp)
                .clickable {
                    showDialog = true
                }
        )
    }
    RecuperarContrasenaDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onEnviarCorreo = { correo -> onEnviarCorreo( correo) },
        onCodigoIntroducido = { correo, codigo -> comprobarCodigoRecup(correo, codigo) }
    )

    if(codigoStatus == "NOK"){
        onVerifyCodigoRecupChanged("")
        Text(
            text = "El codigo no es correcto",
            color = MaterialTheme.colorScheme.error,
            fontStyle = FontStyle.Italic
        )
    }

}

/** Boton de inicio **/
@Composable
fun BotonInicio(
    registroState: Boolean,
    repetirPass: Boolean,
    mensaje: String,
    onBotonInicioClick: () -> Unit
) {
    var texto = "INICIAR"
    val robotoBold = FontFamily(Font(R.font.roboto_bold))
    Button(
        onClick = { onBotonInicioClick() },
        modifier = Modifier
            .height(60.dp)
            .width(210.dp)
    ) {

        if(registroState) texto = "REGISTRAR"
        else if(repetirPass) texto = "CAMBIAR PASS"
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


/** ESPACIADOR  **/
@Composable
fun CustomSpacer(size: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
    )
}


