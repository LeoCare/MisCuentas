package com.app.miscuentas.features

import android.Manifest
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.features.inicio.INICIO_ROUTE
import com.app.miscuentas.features.inicio.NavigateToInicio
import com.app.miscuentas.features.navegacion.AppNavHost
import com.app.miscuentas.features.navegacion.BottomNavigationBar
import com.app.miscuentas.features.navegacion.MiTopBar
import com.app.miscuentas.features.navegacion.MisHojasScreen
import com.app.miscuentas.features.splash.SPLASH_ROUTE
import com.app.miscuentas.features.theme.MisCuentasTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() //hace que ocupe toda la pantalla del movil

        setContent {
            val mainActivityViewModel: MainActivityViewModel = hiltViewModel()

            MisCuentasTheme {
                val scrollBehavior =
                    TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()) //eleva el topbar al hacer scroll (se lo pasa al TopBar)
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                //pila de Screen y valor actual
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen = backStackEntry?.destination?.route ?: INICIO_ROUTE
                val canNavigateBack = backStackEntry != null

                val title by mainActivityViewModel.title.collectAsState()

                val items = listOf(
                    MisHojasScreen.MisHojas,
                    MisHojasScreen.MisGastos,
                    MisHojasScreen.Participantes
                )


                Scaffold(
                    scaffoldState = scaffoldState,
                    modifier = Modifier
                        .fillMaxSize(),
                    // .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        if (currentScreen != "INICIO" && currentScreen != "SPLASH" && currentScreen != "LOGIN") {
                            MiTopBar(
                                title = title,
                                canNavigateBack = canNavigateBack,
                                navigateUp = { navController.popBackStack() }
                            )
                        }
                    }
                   ,
                    bottomBar = {
                        if (currentScreen in items.map { it.route }) {
                            BottomNavigationBar(navController)
                        }
                    },
                    contentWindowInsets = WindowInsets.safeDrawing,
                    content = { innerPadding ->
                        AppNavHost(
                            innerPadding,
                            navController,
                            mainActivityViewModel
                        )
                    }
                )

            }
        }
        Log.d(TAG, "onCreated Called")
    }

}