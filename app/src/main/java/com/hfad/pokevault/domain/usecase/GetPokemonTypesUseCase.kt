package com.hfad.pokevault.domain.usecase

import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.data.api.PokemonType
import com.hfad.pokevault.domain.repository.PokeRepository
import javax.inject.Inject

class GetPokemonTypesUseCase @Inject constructor(
    private val repository: PokeRepository
) {
    suspend operator fun invoke(): List<PokemonType> {
        return repository.getPokemonTypes()
    }
}

class GetPokemonListUseCase @Inject constructor(
    private val repository: PokeRepository
) {
    suspend operator fun invoke(): List<PokemonListItem> {
        return repository.getPokemonListEntities().map { entity ->
            PokemonListItem(
                name = entity.name,
                url = entity.url,
                types = entity.types,
                imageUrl = entity.imageUrl
            )
        }
    }
}
