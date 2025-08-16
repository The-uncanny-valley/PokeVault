package com.hfad.pokevault.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 200,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetails(
        @Path("name") name: String
    ): PokemonDetailsResponse

    @GET("type")
    suspend fun getTypes(): PokemonTypeResponse

    companion object {
        @JvmStatic
        fun create(): PokeApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(PokeApiService::class.java)
        }
    }
}


// tells Retrofit to fetch /type from https://pokeapi.co/api/v2/