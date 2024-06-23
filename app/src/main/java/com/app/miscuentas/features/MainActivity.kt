package com.app.miscuentas.features

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.features.gastos.GastosViewModel

import com.app.miscuentas.features.navegacion.AppNavHost
import com.app.miscuentas.features.navegacion.BottomNavigationBar
import com.app.miscuentas.features.navegacion.MiTopBar
import com.app.miscuentas.features.navegacion.MisHojasScreen
import com.app.miscuentas.features.splash.SPLASH_ROUTE
import com.app.miscuentas.features.theme.MisCuentasTheme
import com.app.miscuentas.util.Validaciones
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    /** MANEJO DE LA CAMARA Y DE LA FOTO **/
    private lateinit var gastoViewModel: GastosViewModel
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var getImageLauncher: ActivityResultLauncher<String>
    /**************************************/

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** MANEJO DE LA CAMARA **/
        gastoViewModel = ViewModelProvider(this)[GastosViewModel::class.java]
        // Registrar el launcher para tomar la foto
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                gastoViewModel.gastosState.value.imageUri?.let {
                    val photoPath = it.path ?: return@registerForActivityResult
                    gastoViewModel.insertFoto(photoPath)
                }
            }
        }

        /** MANEJO DE LA GALERIA **/
        getImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Manejar la URI de la imagen seleccionada
                Log.d("MainActivity", "Imagen seleccionada: $uri")
                gastoViewModel.onImageUriChanged(it)
            }
        }
        /*************************/
        setContent {
            MisCuentasTheme{
                val scrollBehavior =
                    TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()) //eleva el topbar al hacer scroll (se lo pasa al TopBar)
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                //pila de Screen y valor actual
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen = backStackEntry?.destination?.route ?: SPLASH_ROUTE
                val canNavigateBack = backStackEntry != null

                val items = listOf(
                    MisHojasScreen.MisHojas,
                    MisHojasScreen.MisGastos,
                    MisHojasScreen.Participantes
                )

                if(currentScreen != "INICIO"){
                    Scaffold(
                        scaffoldState = scaffoldState,
                        modifier = Modifier
                            .fillMaxSize(),
                           // .nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
                            MiTopBar(
                                null, //solo inicio tiene menu lateral
                                currentScreen,
                                null,   //solo inicio tiene menu lateral
                                canNavigateBack = canNavigateBack,
                                navigateUp = { navController.popBackStack() }
                            )

                        },
                        bottomBar = {
                            items.forEach { screen ->
                                val screenStr = screen.route
                                if(currentScreen == screenStr){
                                    BottomNavigationBar(navController)
                                }
                            }
                        },
                        content = { innerPadding ->
                            AppNavHost(
                                innerPadding,
                                navController,
                                { selectImage() }
                            )
                        }
                    )
                }else { //inicio tiene su propia estructura con un menu lateral y topBar propio
                    AppNavHost(
                        null,
                        navController,
                        { }
                    )
                }
            }
        }
        Log.d(TAG, "onCreated Called")
    }

    /** MANEJO DE LA FOTO **/
    fun tomarFoto(context: Context) {
        val file = createImageFile(context)
        val image = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        gastoViewModel.onImageUriChanged(image)
        takePictureLauncher.launch(image)
    }

    private fun createImageFile(context: Context): File {
        val timeStamp = Validaciones.fechaToStringFormatFoto(LocalDate.now())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    /** MANEJO DE LA FOTO DE LA GALERIA **/
    private fun selectImage() {
        getImageLauncher.launch("image/*")
    }
    /************************************/
}