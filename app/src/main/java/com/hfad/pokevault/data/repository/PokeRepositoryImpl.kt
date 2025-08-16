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

        // Fetch details for each Pokémon to get its types
        return response.results.map { basicPokemon ->
            val details = api.getPokemonDetails(basicPokemon.name)
            val typeNames = details.types.map { it.type.name }
            PokemonListItem(
                name = basicPokemon.name,
                url = basicPokemon.url,
                types = typeNames // <-- we'll add this property in the model
            )
        }
    }
}