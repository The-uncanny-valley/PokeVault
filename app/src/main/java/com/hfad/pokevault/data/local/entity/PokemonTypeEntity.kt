package com.hfad.pokevault.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_types")
data class PokemonTypeEntity(
    @PrimaryKey val name: String
)