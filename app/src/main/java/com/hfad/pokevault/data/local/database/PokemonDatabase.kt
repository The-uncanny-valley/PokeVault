package com.hfad.pokevault.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfad.pokevault.data.local.dao.PokemonDao
import com.hfad.pokevault.data.local.entity.PokemonEntity
import com.hfad.pokevault.data.local.entity.PokemonTypeEntity

@Database(
    entities = [PokemonEntity::class, PokemonTypeEntity::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}