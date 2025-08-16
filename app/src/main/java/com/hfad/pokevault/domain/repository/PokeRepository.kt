package com.hfad.pokevault.domain.repository

import androidx.paging.PagingSource
import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.data.api.PokemonType
import com.hfad.pokevault.data.db.PokemonEntity

interface PokeRepository {
    suspend fun getPokemonList(): List<PokemonListItem>
    suspend fun getPokemonTypes(): List<PokemonType>
    suspend fun getPokemonListEntities(): List<PokemonEntity>
    suspend fun insertPokemons(pokemons: List<PokemonEntity>)
    fun getAllPokemonsPaging(): PagingSource<Int, PokemonEntity>
    fun searchPokemonsPaging(query: String): PagingSource<Int, PokemonEntity>
//    fun filterByTypePaging(type: String): PagingSource<Int, PokemonEntity>
    fun filterByTypePaging(types: List<String>): PagingSource<Int, PokemonEntity>
}