package com.hfad.pokevault.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hfad.pokevault.data.db.PokemonDao
import com.hfad.pokevault.data.db.PokemonDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS pokemon_types (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
        """.trimIndent())
    }
}

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
            ).addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(database: PokemonDatabase): PokemonDao {
        return database.pokemonDao()
    }
}
