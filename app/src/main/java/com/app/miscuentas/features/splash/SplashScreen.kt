package com.app.miscuentas.features.splash

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Imagen.Companion.createTempPictureUri
import com.app.miscuentas.util.Imagen.Companion.permisosRequeridos
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Delay


@Composable
fun SplashScreen(
    onLoginNavigate: () -> Unit,
    onInicioNavigate: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.splashState.collectAsState()
    val context = LocalContext.current

    @Composable
    fun PermissionRequestEffect(onResult: (Boolean) -> Unit) {
        val permissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedPermissions ->
                if(grantedPermissions.all { it.value}){
                    onResult(true)
                }
                else onResult(false)
            }

        LaunchedEffect(Unit) {
            permissionLauncher.launch(permisosRequeridos)
        }
    }

    PermissionRequestEffect() { granted ->
        if (granted){
            Toast.makeText(context, "Todos los permisos aceptados.", Toast.LENGTH_SHORT).show()
            viewModel.onPermisoTratadoChanged(true)
        }
        else {
            Toast.makeText(context, "Algunos permisos se han denegado.", Toast.LENGTH_SHORT).show()
            viewModel.onPermisoTratadoChanged(true)
        }
    }

    //Comprobacion de la version de la BBDD y de las Preferences
    LaunchedEffect(splashState.permisosTratados) {
        if (splashState.permisosTratados){
            viewModel.checkAndClearDataStore()
        }
    }

    //Luego de la comprobacion inicial del viewmodel:
    LaunchedEffect(splashState.continuar) {
        if(splashState.continuar) {
            if (splashState.autoInicio) onInicioNavigate()
            else onLoginNavigate()
        }
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
