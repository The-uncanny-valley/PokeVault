package com.hfad.pokevault.di

import android.content.Context
import com.hfad.pokevault.data.api.PokeApiService
import com.hfad.pokevault.data.db.PokemonDao
import com.hfad.pokevault.data.repository.PokeRepositoryImpl
import com.hfad.pokevault.domain.repository.PokeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePokeRepository(
        apiService: PokeApiService,
        pokemonDao: PokemonDao,
        @ApplicationContext context: Context
    ): PokeRepository {
        return PokeRepositoryImpl(apiService, pokemonDao, context)
    }
}