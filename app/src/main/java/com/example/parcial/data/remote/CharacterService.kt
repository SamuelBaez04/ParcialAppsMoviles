package com.example.parcial.data.remote

import com.example.parcial.data.model.Character
import com.example.parcial.data.model.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CharacterService {
    @GET("character")
    suspend fun getCharacters(): CharacterResponse

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): Character
}
