package com.example.parcial.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.parcial.data.model.Character
import com.example.parcial.data.repository.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CharacterUiState {
    object Loading : CharacterUiState()
    data class Success(val characters: List<Character>) : CharacterUiState()
    data class Error(val message: String) : CharacterUiState()
}

class CharacterViewModel(private val repository: CharacterRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val uiState: StateFlow<CharacterUiState> = _uiState.asStateFlow()

    init {
        fetchCharacters()
    }

    fun fetchCharacters() {
        viewModelScope.launch {
            _uiState.value = CharacterUiState.Loading
            repository.getCharacters().collect { result ->
                result.onSuccess {
                    _uiState.value = CharacterUiState.Success(it)
                }.onFailure {
                    _uiState.value = CharacterUiState.Error(it.message ?: "Unknown Error")
                }
            }
        }
    }

    suspend fun getCharacterById(id: Int): Character? {
        return repository.getCharacterById(id)
    }
}

class CharacterViewModelFactory(private val repository: CharacterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CharacterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
