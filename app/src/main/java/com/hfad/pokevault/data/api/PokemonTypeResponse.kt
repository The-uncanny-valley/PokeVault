package com.hfad.pokevault.data.api

import com.google.gson.annotations.SerializedName

data class PokemonTypeResponse(
    val results: List<PokemonType>
)

data class PokemonType(
    val name: String,
    val url: String
)

// add new models for Pokémon details:

data class PokemonDetailsResponse(
    val types: List<TypeSlot>,
    val sprites: Sprites?
)

data class TypeSlot(
    val slot: Int,
    val type: TypeName
)

data class TypeName(
    val name: String
)

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String?
)