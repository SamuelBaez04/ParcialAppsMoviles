package com.example.parcial.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parcial.data.model.Character
import com.example.parcial.ui.CharacterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    viewModel: CharacterViewModel,
    onBackClick: () -> Unit
) {
    var character by remember { mutableStateOf<Character?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(characterId) {
        character = viewModel.getCharacterById(characterId)
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(character?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                character?.let { char ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = char.image,
                            contentDescription = char.name,
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = char.name, style = MaterialTheme.typography.headlineMedium)
                        Text(text = "Status: ${char.status}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Species: ${char.species}", style = MaterialTheme.typography.bodyLarge)
                    }
                } ?: Text(text = "Personaje no encontrado", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
