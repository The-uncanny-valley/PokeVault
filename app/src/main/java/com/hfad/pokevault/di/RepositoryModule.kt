package com.hfad.pokevault.di

import com.hfad.pokevault.data.api.PokeApiService
import com.hfad.pokevault.data.repository.PokeRepositoryImpl
import com.hfad.pokevault.domain.repository.PokeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePokeRepository(api: PokeApiService): PokeRepository {
        return PokeRepositoryImpl(api)
    }
}