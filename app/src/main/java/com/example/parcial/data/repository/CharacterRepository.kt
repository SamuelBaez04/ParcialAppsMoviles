package com.example.parcial.data.repository

import com.example.parcial.data.local.CharacterDao
import com.example.parcial.data.model.Character
import com.example.parcial.data.remote.CharacterService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class CharacterRepository(
    private val service: CharacterService,
    private val dao: CharacterDao
) {
    fun getCharacters(): Flow<Result<List<Character>>> {
        return dao.getAllCharacters()
            .map { Result.success(it) }
            .onStart {
                try {
                    val response = service.getCharacters()
                    dao.insertCharacters(response.results)
                } catch (e: Exception) {
                }
            }
    }

    suspend fun getCharacterById(id: Int): Character? {
        return try {
            val remoteCharacter = service.getCharacter(id)
            dao.insertCharacters(listOf(remoteCharacter))
            remoteCharacter
        } catch (e: Exception) {
            dao.getCharacterById(id)
        }
    }
}
