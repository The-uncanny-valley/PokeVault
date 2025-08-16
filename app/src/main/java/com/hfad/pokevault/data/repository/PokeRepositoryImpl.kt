package com.hfad.pokevault.data.repository

import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hfad.pokevault.data.api.PokeApiService
import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.data.api.PokemonType
import com.hfad.pokevault.data.db.PokemonDao
import com.hfad.pokevault.data.db.PokemonEntity
import com.hfad.pokevault.domain.repository.PokeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class PokeRepositoryImpl @Inject constructor(
    private val api: PokeApiService,
    private val dao: PokemonDao
) : PokeRepository {
    // Implement getPokemonList()
    override suspend fun getPokemonList(): List<PokemonListItem> {
        val response = api.getPokemonList()
        val pokemons = response.results.map { basic ->
            val details = api.getPokemonDetails(basic.name)
            val typeNames = details.types.map { it.type.name }
            PokemonListItem(
                name = basic.name,
                url = basic.url,
                types = typeNames
            )
        }

        // Cache in Room
        dao.insertAll(pokemons.map { PokemonEntity(it.name, it.url, it.types) })
        return pokemons
    }

    override suspend fun getPokemonTypes(): List<PokemonType> =
        api.getTypes().results

    override suspend fun getPokemonListEntities(): List<PokemonEntity> = coroutineScope {
        val response = api.getPokemonList()

        // Launch detail requests in parallel
        val deferred = response.results.map { basic ->
            async {
                val details = api.getPokemonDetails(basic.name)
                val typeNames = details.types.map { it.type.name }
                PokemonEntity(
                    name = basic.name,
                    url = basic.url,
                    types = typeNames
                )
            }
        }

        val pokemons = deferred.awaitAll()

        // Cache in Room
        dao.insertAll(pokemons)
        pokemons
    }


    override suspend fun insertPokemons(pokemons: List<PokemonEntity>) {
        dao.insertAll(pokemons)
    }

    override fun getAllPokemonsPaging(): PagingSource<Int, PokemonEntity> =
        dao.getAllPokemons()

    override fun searchPokemonsPaging(query: String): PagingSource<Int, PokemonEntity> =
        dao.searchPokemons(query)

//    override fun filterByTypePaging(type: String): PagingSource<Int, PokemonEntity> =
//        dao.filterByType(type)

    override fun filterByTypePaging(types: List<String>): PagingSource<Int, PokemonEntity> {
        if (types.isEmpty()) {
            return dao.getAllPokemons()
        }

        val conditions = types.joinToString(" AND ") { "types LIKE '%$it%'" }
        val sql = "SELECT * FROM pokemon_table WHERE $conditions"
        val query = SimpleSQLiteQuery(sql)

        return dao.filterByTypes(query)
    }
}