package com.hfad.pokevault.domain.repository

import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.data.api.PokemonType

interface PokeRepository {
    suspend fun getPokemonTypes(): List<PokemonType>
    suspend fun getPokemonList(): List<PokemonListItem>
}