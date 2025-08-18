package com.hfad.pokevault

import android.app.Application
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.hfad.pokevault.data.PreloadImagesWorker
import com.hfad.pokevault.domain.usecase.GetPokemonTypesUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
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

        // Schedule image preloading worker
        setupImagePreloadWorker()
    }

    private fun setupImagePreloadWorker() {
        val request = PeriodicWorkRequestBuilder<PreloadImagesWorker>(
            24, TimeUnit.HOURS // run once per day
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PreloadPokemonImages",
            ExistingPeriodicWorkPolicy.KEEP, // keep existing schedule
            request
        )

        val oneTimeRequest = OneTimeWorkRequestBuilder<PreloadImagesWorker>().build()
        WorkManager.getInstance(this).enqueue(oneTimeRequest)
    }
}