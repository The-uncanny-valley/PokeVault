package com.hfad.pokevault.data.api

data class PokemonTypeResponse(
    val results: List<PokemonType>
)

data class PokemonType(
    val name: String,
    val url: String
)

// add new models for Pokémon details:

data class PokemonDetailsResponse(
    val types: List<TypeSlot>
)

data class TypeSlot(
    val slot: Int,
    val type: TypeName
)

data class TypeName(
    val name: String
)