package com.app.miscuentas

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.ui.navegacion.AppNavHost
import com.app.miscuentas.ui.theme.MisCuentasTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MisCuentasTheme{
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //Navegacion con el componente de Composable
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
        Log.d(TAG, "onCreated Called")
    }

    //Observar ciclo de vida
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart Called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }
}