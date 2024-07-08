package com.app.miscuentas.features.splash

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.util.Imagen.Companion.permisosRequeridos


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
