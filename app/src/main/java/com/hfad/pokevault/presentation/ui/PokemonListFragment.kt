package com.hfad.pokevault.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hfad.pokevault.R
import com.hfad.pokevault.presentation.adapter.PokemonAdapter
import com.hfad.pokevault.presentation.viewmodel.PokemonListViewModel
import com.hfad.pokevault.presentation.viewmodel.PokemonViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonListFragment : Fragment(R.layout.fragment_pokemon_list) {

    private lateinit var adapter: PokemonAdapter
//    private val viewModel: PokemonViewModel by viewModels()
private val viewModel: PokemonListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokemon_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.pokemonRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns

        adapter = PokemonAdapter(emptyList())
        recyclerView.adapter = adapter

        observePokemons()
        viewModel.loadPokemon() // call use case via ViewModel

        // Observe state (optional for testing)
//        lifecycleScope.launchWhenStarted {
//            viewModel.pokemonListState.collect { list ->
//                Log.i("API_TEST", "Fetched ${list.size} Pokémon")
//                list.take(5).forEach { Log.i("API_TEST", it.name) }
//            }
//        }

        val searchEditText = view.findViewById<TextInputEditText>(R.id.searchEditText)

        searchEditText.addTextChangedListener { text ->
            val query = text?.toString().orEmpty()
            val filtered = viewModel.pokemonList.value.filter {
                it.name.contains(query, ignoreCase = true)
            }
            adapter.updateList(filtered)
        }

        // handle filter icon click
        val searchLayout = view.findViewById<TextInputLayout>(R.id.searchLayout)
        searchLayout.setEndIconOnClickListener {
            val filterSheet = FilterBottomSheetFragment()
            filterSheet.show(parentFragmentManager, "FilterBottomSheet")
        }

        // OBSERVE the filter click event here
//        viewModel.filterClickEvent.observe(viewLifecycleOwner) {
//            val filterSheet = FilterBottomSheetFragment()
//            filterSheet.show(parentFragmentManager, "FilterBottomSheet")
//        }

        lifecycleScope.launch {
            Log.i("API_TEST", "Fragment created")
        }

    }

    private fun observePokemons() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pokemonList.collect { pokemonList ->
                    Log.i("API_TEST", "Updating adapter with ${pokemonList.size} Pokémon")
                    adapter.updateList(pokemonList)
                }
            }

        }
    }
}