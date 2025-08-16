package com.hfad.pokevault.di

import android.content.Context
import androidx.room.Room
import com.hfad.pokevault.data.db.PokemonDao
import com.hfad.pokevault.data.db.PokemonDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PokemonDatabase {
        return Room.databaseBuilder(
                context,
                PokemonDatabase::class.java,
                "pokemon_db"
            ).fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(database: PokemonDatabase): PokemonDao {
        return database.pokemonDao()
    }
}
