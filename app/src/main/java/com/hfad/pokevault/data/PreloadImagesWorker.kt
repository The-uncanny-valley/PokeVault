package com.hfad.pokevault.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.Coil
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.hfad.pokevault.data.api.PokeApiService
import com.hfad.pokevault.data.db.PokemonDao
import com.hfad.pokevault.data.db.PokemonEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// 1. HiltWorker allows dependency injection in Workers
@HiltWorker
class PreloadImagesWorker @AssistedInject constructor(
    // 2. Context and WorkerParameters are mandatory for every Worker
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,

    // 3. Inject your API and DAO
    private val api: PokeApiService,
    private val dao: PokemonDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 4. Fetch Pokémon list
            val response = api.getPokemonList()
            val pokemons = response.results.map { basic ->
                val details = api.getPokemonDetails(basic.name)
                val types = details.types.map { it.type.name }
                val imageUrl = details.sprites?.frontDefault ?: ""

                // 5. Save Pokémon in Room
                PokemonEntity(basic.name, basic.url, types, imageUrl).also {
                    // 6. Preload image with Coil
                    Coil.imageLoader(applicationContext).enqueue(
                        ImageRequest.Builder(applicationContext)
                            .data(imageUrl)
                            .diskCachePolicy(CachePolicy.ENABLED)   // save on disk
                            .memoryCachePolicy(CachePolicy.ENABLED) // save in memory
                            .build()
                    )
                }
            }

            // 7. Insert all into Room
            dao.insertAll(pokemons)

            // 8. Work succeeded
            Result.success()
        } catch (e: Exception) {
            // 9. Retry if network fails
            Result.retry()
        }
    }
}