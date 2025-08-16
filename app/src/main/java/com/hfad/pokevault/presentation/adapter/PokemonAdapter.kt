package com.hfad.pokevault.presentation.adapter

import coil.load
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.pokevault.R
import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.presentation.ui.applyColoredShadow

class PokemonAdapter(private var pokemonList: List<PokemonListItem>) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.pokemonName)
        val imageView: ImageView = itemView.findViewById(R.id.pokemonImage)
        val cardView: CardView = itemView.findViewById(R.id.pokemonCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {

        val pokemon = pokemonList[position]

        holder.nameText.text = pokemon.name.replaceFirstChar { it.uppercase() }

        // Load image from URL using Coil/Glide/Picasso instead of imageResId
        // Example with Coil:
        val id = pokemon.url.trimEnd('/').substringAfterLast("/").toInt()
        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
        holder.imageView.load(imageUrl) // requires: implementation "io.coil-kt:coil:2.x"

        // Apply the blue shadow to the card
        holder.cardView.applyColoredShadow()
    }

    override fun getItemCount(): Int = pokemonList.size

    fun updateList(newList: List<PokemonListItem>) {
        pokemonList = newList
        notifyDataSetChanged()
    }
}

