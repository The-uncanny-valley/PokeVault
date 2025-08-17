package com.hfad.pokevault

import android.app.Application
import android.util.Log
import com.hfad.pokevault.domain.usecase.GetPokemonTypesUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PokeVaultApp : Application() {

    @Inject
    lateinit var getPokemonTypesUseCase: GetPokemonTypesUseCase

    override fun onCreate() {
        super.onCreate()

        // Preload Pokémon types at app startup
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val types = getPokemonTypesUseCase()
                // You can cache types somewhere, e.g., in a singleton or in your repository
                Log.d("PokeVaultApp", "Pokémon types preloaded: ${types.size}")
            } catch (e: Exception) {
                Log.e("PokeVaultApp", "Failed to preload Pokémon types", e)
            }
        }
    }
}