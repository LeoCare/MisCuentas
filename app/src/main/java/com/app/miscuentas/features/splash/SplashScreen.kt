package com.app.miscuentas.features.splash

import android.Manifest
import android.app.Activity
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.app.miscuentas.R
import com.app.miscuentas.util.MiAviso
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashScreen(
    activity: FragmentActivity,
    onLoginNavigate: () -> Unit,
    onInicioNavigate: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.splashState.collectAsState()

    var continuar by rememberSaveable { mutableStateOf(true) } //Continua con la app una vez se comprueban los permisos

    /** **Inicio comprobacion de permisos**  **/
    val statePermisoCamara = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val scope = rememberCoroutineScope()

//    LaunchedEffect(Dispatchers.IO){
//            viewModel.solicitaPermiso(statePermisoCamara)
//    }


    //Comprobacion del permiso solicitado

    if (statePermisoCamara.status.isGranted)
        viewModel.setPermisoConcedido()
    else if (statePermisoCamara.status.shouldShowRationale)
        viewModel.setPermisoDenegPermanente()
    else if (splashState.permisoState == null){}
    else viewModel.setPermisoDenegado()



    //Este aviso se lanzara cuando se deniega el permiso...
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo

    if (showDialog) {
        if (splashState.permisoState == SplashState.PermissionState.DenegPermanente) {
            MiAviso(
                show = true,
                texto = "El permiso es necesaro para enviar una captura.\nSi se deniega una vez mas, solo se podrá otorgar desde la configuracion del dispositivo."
                ) {
                showDialog = false
                scope.launch {
                    viewModel.solicitaPermiso(statePermisoCamara)
                }
            }
        }
    }
    /** ***Fin comprobacion de permisos** */


    //Accion despues de la comprobacion
    LaunchedEffect(splashState.permisoState){
        when(splashState.permisoState){
            is SplashState.PermissionState.Concedido -> {
                continuar = true}
            is  SplashState.PermissionState.DenegPermanente -> {
                showDialog = true }
            is  SplashState.PermissionState.Denegado -> {
                continuar = true}

            else -> {}
        }
    }


    // Permiso concedido, continuar con la lógica del SplashScreen
    if (continuar) {
        if (splashState.autoInicio) onInicioNavigate()
        else onLoginNavigate()
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logologin),
            contentDescription = "Logo App")
        Text(
            text = "Cargando..",
            style = MaterialTheme.typography.titleLarge
        )
    }


}
