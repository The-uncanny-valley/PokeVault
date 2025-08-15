package com.hfad.pokevault.data.api

data class PokemonTypeResponse(
    val results: List<PokemonType>
)

data class PokemonType(
    val name: String,
    val url: String
)
