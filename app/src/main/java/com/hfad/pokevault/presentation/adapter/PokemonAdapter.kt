package com.hfad.pokevault.presentation.adapter

import coil.load
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.request.CachePolicy
import com.hfad.pokevault.R
import com.hfad.pokevault.data.api.PokemonListItem
import com.hfad.pokevault.presentation.ui.applyColoredShadow

class PokemonAdapter :
    PagingDataAdapter<PokemonListItem, PokemonAdapter.PokemonViewHolder>(DiffCallback()) {

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.pokemon_name)
        val imageView: ImageView = itemView.findViewById(R.id.pokemon_image)
        val cardView: CardView = itemView.findViewById(R.id.pokemon_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = getItem(position) ?: return

        holder.nameText.text = pokemon.name.replaceFirstChar { it.uppercase() }

        val id = pokemon.url.trimEnd('/').substringAfterLast("/").toInt()
        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

        holder.imageView.load(imageUrl) {
            placeholder(R.drawable.loading)
            error(R.drawable.image_error)
            crossfade(true)
            diskCachePolicy(CachePolicy.ENABLED) // ensure cached on disk
            memoryCachePolicy(CachePolicy.ENABLED)
        }

        holder.cardView.applyColoredShadow()
    }
}

class DiffCallback : DiffUtil.ItemCallback<PokemonListItem>() {
    override fun areItemsTheSame(oldItem: PokemonListItem, newItem: PokemonListItem) =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: PokemonListItem, newItem: PokemonListItem) =
        oldItem == newItem
}

