package com.hfad.pokevault.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.pokevault.data.api.PokemonType
import com.hfad.pokevault.domain.usecase.GetPokemonTypesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonTypeViewModel @Inject constructor(
    private val getPokemonTypes: GetPokemonTypesUseCase
) : ViewModel() {

    private val _types = MutableStateFlow<List<PokemonType>>(emptyList())
    val types: StateFlow<List<PokemonType>> = _types

    fun loadTypes() {
        viewModelScope.launch {
            _types.value = getPokemonTypes()
        }
    }
}