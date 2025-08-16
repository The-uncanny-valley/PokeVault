package com.hfad.pokevault.data.api

data class PokemonListResponse(
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val url: String,
    val types: List<String> = emptyList() // e.g. ["fire", "electric"]
)