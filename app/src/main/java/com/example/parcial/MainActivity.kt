package com.example.parcial

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parcial.data.local.AppDatabase
import com.example.parcial.data.remote.RetrofitClient
import com.example.parcial.data.repository.CharacterRepository
import com.example.parcial.ui.CharacterUiState
import com.example.parcial.ui.CharacterViewModel
import com.example.parcial.ui.CharacterViewModelFactory
import com.example.parcial.ui.NotificationHelper
import com.example.parcial.ui.screens.CharacterDetailScreen
import com.example.parcial.ui.screens.CharacterListScreen
import com.example.parcial.ui.theme.ParcialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = CharacterRepository(RetrofitClient.characterService, database.characterDao())
        val factory = CharacterViewModelFactory(repository)
        val notificationHelper = NotificationHelper(this)

        setContent {
            ParcialTheme {
                val navController = rememberNavController()
                val viewModel: CharacterViewModel = viewModel(factory = factory)
                val uiState by viewModel.uiState.collectAsState()
                var showRationale by remember { mutableStateOf(false) }

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (!isGranted) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                                showRationale = true
                            } else {
                                Toast.makeText(this, "Permiso denegado. Puedes activarlo en los ajustes de la aplicación.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                if (showRationale) {
                    AlertDialog(
                        onDismissRequest = { showRationale = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showRationale = false
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRationale = false }) { Text("Cancelar") }
                        },
                        title = { Text("Permiso de Notificaciones") },
                        text = { Text("Esta aplicación necesita enviarte notificaciones para informarte sobre el estado de la descarga de datos.") }
                    )
                }

                LaunchedEffect(uiState) {
                    if (uiState is CharacterUiState.Success) {
                        notificationHelper.sendNotification(
                            "Datos Cargados",
                            "Se han descargado los personajes exitosamente."
                        )
                    }
                }

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        CharacterListScreen(
                            viewModel = viewModel,
                            onCharacterClick = { id ->
                                navController.navigate("detail/$id")
                            }
                        )
                    }
                    composable("detail/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                        CharacterDetailScreen(
                            characterId = id,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
