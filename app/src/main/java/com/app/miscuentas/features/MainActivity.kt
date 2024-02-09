package com.app.miscuentas.features

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.features.navegacion.AppNavHost
import com.app.miscuentas.features.theme.MisCuentasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
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
}