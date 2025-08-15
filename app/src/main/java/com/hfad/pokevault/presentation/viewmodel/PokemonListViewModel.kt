package com.hfad.pokevault.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.domain.usecase.GetPokemonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val getPokemonListUseCase: GetPokemonListUseCase
) : ViewModel() {

    private val _pokemonListState = MutableStateFlow<List<PokemonListItem>>(emptyList())
    val pokemonList: StateFlow<List<PokemonListItem>> = _pokemonListState

    fun loadPokemon() {
        viewModelScope.launch {
            _pokemonListState.value = getPokemonListUseCase()
        }
    }

    // Run automatically when ViewModel is created
    init {
        viewModelScope.launch {
            try {
                val pokemons = getPokemonListUseCase() // call the use case, not repository
                _pokemonListState.value = pokemons
                Log.i("API_TEST", "Fetched ${pokemons.size} Pokémon")
            } catch (e: Exception) {
                Log.e("API_TEST", "API call failed", e)
            }
        }
    }
}