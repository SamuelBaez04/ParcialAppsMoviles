package com.example.parcial.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parcial.data.model.Character
import com.example.parcial.ui.CharacterUiState
import com.example.parcial.ui.CharacterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    viewModel: CharacterViewModel,
    onCharacterClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Personajes") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is CharacterUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CharacterUiState.Success -> {
                    CharacterList(state.characters, onCharacterClick)
                }
                is CharacterUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.fetchCharacters() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterList(characters: List<Character>, onCharacterClick: (Int) -> Unit) {
    LazyColumn {
        items(characters) { character ->
            CharacterItem(character, onCharacterClick)
        }
    }
}

@Composable
fun CharacterItem(character: Character, onCharacterClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCharacterClick(character.id) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = character.image,
                contentDescription = character.name,
                modifier = Modifier.size(80.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = character.name, style = MaterialTheme.typography.titleLarge)
                Text(text = character.species, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
