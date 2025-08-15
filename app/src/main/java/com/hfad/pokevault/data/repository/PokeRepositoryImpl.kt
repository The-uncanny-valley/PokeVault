package com.hfad.pokevault.data.repository

import com.hfad.pokevault.data.api.PokeApiService
import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.data.api.PokemonType
import com.hfad.pokevault.domain.repository.PokeRepository
import javax.inject.Inject

class PokeRepositoryImpl @Inject constructor(
    private val api: PokeApiService
) : PokeRepository {

    override suspend fun getPokemonTypes(): List<PokemonType> {
        val response = api.getTypes()
        return response.results
    }

    override suspend fun getPokemonList(): List<PokemonListItem> {
        val response = api.getPokemonList()
        return response.results
    }
}