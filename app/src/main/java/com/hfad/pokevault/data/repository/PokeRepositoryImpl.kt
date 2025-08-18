package com.hfad.pokevault.data.repository

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import coil.Coil
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.hfad.pokevault.data.api.PokeApiService
import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.data.api.PokemonType
import com.hfad.pokevault.data.db.PokemonDao
import com.hfad.pokevault.data.db.PokemonEntity
import com.hfad.pokevault.data.db.PokemonTypeEntity
import com.hfad.pokevault.domain.repository.PokeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PokeRepositoryImpl @Inject constructor(
    private val api: PokeApiService,
    private val dao: PokemonDao,
    @param:ApplicationContext private val context: Context
) : PokeRepository {
    // Implement getPokemonList()
    override suspend fun getPokemonList(): List<PokemonListItem> {
        return try {
            // Try fetching from API
            val response = api.getPokemonList()
            val pokemons = response.results.map { basic ->
                val details = api.getPokemonDetails(basic.name)
                val typeNames = details.types.map { it.type.name }
                val imageUrl = details.sprites?.frontDefault ?: ""

                PokemonListItem(
                    name = basic.name,
                    url = basic.url,
                    types = typeNames,
                    imageUrl = imageUrl
                )
            }

            // Cache in Room
            dao.insertAll(pokemons.map {
                PokemonEntity(it.name, it.url, it.types, it.imageUrl)
            })

            // Preload and cache images with Coil
            pokemons.forEach { pokemon ->
                val imageUrl = pokemon.imageUrl
                if (imageUrl.isNotEmpty()) {
                    Coil.imageLoader(context).enqueue(
                        ImageRequest.Builder(context)
                            .data(imageUrl)
                            .diskCachePolicy(CachePolicy.ENABLED)   // store on disk
                            .memoryCachePolicy(CachePolicy.ENABLED) // store in memory
                            .build()
                    )
                }
            }

            pokemons
        } catch (e: Exception) {
            Log.e("PokeRepository", "Failed to fetch Pokémon from API, loading cached data", e)
            // Offline fallback: load cached Pokémon from Room
            val cached = dao.getAllPokemonsEntities()
            Log.d("PokeRepository", "Loaded ${cached.size} Pokémon from cache")
            cached.forEach { Log.d("PokeRepository", "Cached Pokémon: $it") }

            cached.map {
                PokemonListItem(
                    name = it.name,
                    url = it.url,
                    types = it.types,
                    imageUrl = it.imageUrl
                )
            }
        }
    }

    override suspend fun getPokemonTypes(): List<PokemonType> {
        return try {
            val types = api.getTypes().results
            Log.d("PokeRepo", "Fetched ${types.size} Pokémon types from API")

            // Cache in Room
            dao.insertTypes(types.map { PokemonTypeEntity(it.name) })

            types
        } catch (e: Exception) {
            Log.e("PokeRepo", "Failed to fetch Pokémon types from API, returning empty list", e)

            // Offline fallback
            dao.getAllTypes().map { PokemonType(it.name, "") }
//            emptyList() // fallback if offline
        }
    }

    override suspend fun getPokemonListEntities(): List<PokemonEntity> {
        return try {
            val response = api.getPokemonList()
            val pokemons = response.results.map { basic ->
                val details = api.getPokemonDetails(basic.name)
                val types = details.types.map { it.type.name }
                val imageUrl = details.sprites?.frontDefault ?: ""
                PokemonEntity(basic.name, basic.url, types, imageUrl)
            }
            dao.insertAll(pokemons)
            Log.d("PokeRepo", "Fetched and cached ${pokemons.size} Pokémon from API")

            // Preload images
            pokemons.forEach { pokemon ->
                val imageUrl = pokemon.imageUrl
                if (imageUrl.isNotEmpty()) {
                    Coil.imageLoader(context).enqueue(
                        ImageRequest.Builder(context)
                            .data(imageUrl)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build()
                    )
                }
            }

            pokemons
        } catch (e: Exception) {
            Log.e("PokeRepo", "Failed to fetch Pokémon from API, loading cached data", e)

            val cached = dao.getAllPokemonsEntities()
            Log.d("PokeRepo", "Loaded ${cached.size} Pokémon from cache")
            cached.forEach { Log.d("PokeRepo", "Cached Pokémon: $it") }

            cached
        }
    }

    override suspend fun insertPokemons(pokemons: List<PokemonEntity>) {
        dao.insertAll(pokemons)
    }

    override fun getAllPokemonsPaging(): PagingSource<Int, PokemonEntity> =
        dao.getAllPokemons()

    override fun searchPokemonsPaging(query: String): PagingSource<Int, PokemonEntity> =
        dao.searchPokemons(query)

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