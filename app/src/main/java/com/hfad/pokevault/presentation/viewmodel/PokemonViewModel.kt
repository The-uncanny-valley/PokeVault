package com.hfad.pokevault.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.domain.repository.PokeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokeRepository
) : ViewModel() {

    // Holds applied filters
    val filterLiveData = MutableLiveData<Set<String>>()

    // User clicked the filter icon
    private val _filterClickEvent = MutableLiveData<Unit>()
    val filterClickEvent: LiveData<Unit> = _filterClickEvent

    fun onFilterClicked() {
        _filterClickEvent.value = Unit
    }

    // StateFlow to hold Pokémon list
    private val _pokemonListState = MutableStateFlow<List<PokemonListItem>>(emptyList())
    val pokemonListState: StateFlow<List<PokemonListItem>> = _pokemonListState

    // Fetch Pokémon list from repository
    fun loadPokemons() {
        viewModelScope.launch {
            try {
                val pokemons = repository.getPokemonList()
                _pokemonListState.value = pokemons
            } catch (e: Exception) {
                _pokemonListState.value = emptyList()
            }
        }
    }
}