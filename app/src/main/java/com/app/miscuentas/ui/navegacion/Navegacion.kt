package com.app.miscuentas.ui.navegacion

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.miscuentas.ui.inicio.ui.ScaffoldContent
import com.app.miscuentas.ui.login.LoginContent
import com.app.miscuentas.ui.login.LoginViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginContent(LoginViewModel(), onNavigate = { navController.navigate("inicio") })
        }
        composable("inicio") {
            ScaffoldContent()
        }
    }
}