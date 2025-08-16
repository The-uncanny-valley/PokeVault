package com.hfad.pokevault.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.domain.repository.PokeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _typeFilters = MutableStateFlow<List<String>>(emptyList())
    val typeFilters: StateFlow<List<String>> = _typeFilters

    val pokemonPagingFlow: Flow<PagingData<PokemonListItem>> =
        combine(_searchQuery, _typeFilters) { query, types ->
            Pair(query, types)
        }.flatMapLatest { (query, types) ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 5,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    when {
                        types.isNotEmpty() -> repository.filterByTypePaging(types)
                        query.isNotBlank() -> repository.searchPokemonsPaging(query)
                        else -> repository.getAllPokemonsPaging()
                    }
                }
            ).flow
        }
            .map { pagingData ->
                pagingData.map { entity ->
                    PokemonListItem(
                        name = entity.name,
                        url = entity.url,
                        types = entity.types
                    )
                }
            }
            .cachedIn(viewModelScope)


    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTypeFilters(types: List<String>) {
        _typeFilters.value = types
    }

    fun clearTypeFilters() {
        _typeFilters.value = emptyList()
    }

    fun refreshPokemons() {
        viewModelScope.launch {
            try {
                val pokemons = repository.getPokemonListEntities()
                repository.insertPokemons(pokemons)
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Failed to refresh Pokémon", e)
            }
        }
    }
}