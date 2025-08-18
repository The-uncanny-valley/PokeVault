package com.hfad.pokevault.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hfad.pokevault.MainActivity
import com.hfad.pokevault.R
import com.hfad.pokevault.domain.repository.PokeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.content.edit

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: PokeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val firstLaunch = prefs.getBoolean("first_launch", true)

        if (firstLaunch) {
            prefs.edit { putBoolean("first_launch", false) } // mark as launched

            // Launch image preloading
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val pokemons = repository.getPokemonListEntities()
                    repository.insertPokemons(pokemons) // cache them
                } catch (e: Exception) {
                    Log.e("SplashActivity", "Failed to preload images", e)
                } finally {
                    // Go to main screen after preloading
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        } else {
            // Not first launch, go straight to main screen
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}
