package com.hfad.pokevault.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_table")
data class PokemonEntity(
    @PrimaryKey val name: String,
    val url: String,
    val types: List<String> = emptyList()
)
