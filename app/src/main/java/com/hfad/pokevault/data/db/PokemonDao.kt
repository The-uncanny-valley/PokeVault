package com.hfad.pokevault.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemons: List<PokemonEntity>)

    @Query("SELECT * FROM pokemon_table")
    fun getAllPokemons(): PagingSource<Int, PokemonEntity>

    @Query("SELECT * FROM pokemon_table WHERE name LIKE '%' || :query || '%'")
    fun searchPokemons(query: String): PagingSource<Int, PokemonEntity>

//    @Query("SELECT * FROM pokemon_table WHERE types LIKE '%' || :type || '%'")
//    fun filterByType(type: String): PagingSource<Int, PokemonEntity>

    @RawQuery(observedEntities = [PokemonEntity::class])
    fun filterByTypes(query: SupportSQLiteQuery): PagingSource<Int, PokemonEntity>
}