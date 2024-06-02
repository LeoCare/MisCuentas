package com.app.miscuentas.features

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.features.inicio.InicioViewModel
import com.app.miscuentas.features.navegacion.AppNavHost
import com.app.miscuentas.features.navegacion.MiTopBar
import com.app.miscuentas.features.splash.SPLASH_ROUTE
import com.app.miscuentas.features.theme.MisCuentasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MisCuentasTheme{
                val scrollBehavior =
                    TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                //pila de Screen y valor actual
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen = backStackEntry?.destination?.route ?: SPLASH_ROUTE
                val canNavigateBack = backStackEntry != null

                if(currentScreen != "inicio"){
                    Scaffold(
                        scaffoldState = scaffoldState,
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            MiTopBar(
                                null, //solo inicio tiene menu lateral
                                currentScreen,
                                null,   //solo inicio tiene menu lateral
                                canNavigateBack = canNavigateBack,
                                navigateUp = { navController.popBackStack() },
                                scrollBehavior = scrollBehavior
                            )

                        },
                        content = { innerPadding ->
                            AppNavHost(
                                innerPadding,
                                navController
                            )
                        }
                    )
                }else { //inicio tiene su propia estructura con un menu lateral y topBar propio
                    AppNavHost(
                        null,
                        navController
                    )
                }
            }
        }
        Log.d(TAG, "onCreated Called")
    }
}