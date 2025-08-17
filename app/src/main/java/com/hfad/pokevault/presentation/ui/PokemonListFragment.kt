package com.hfad.pokevault.presentation.ui

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hfad.pokevault.R
import com.hfad.pokevault.presentation.adapter.PokemonAdapter
import com.hfad.pokevault.presentation.viewmodel.PokemonListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonListFragment : Fragment(R.layout.fragment_pokemon_list) {

    private lateinit var adapter: PokemonAdapter
    private val viewModel: PokemonListViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: TextInputEditText
    private lateinit var emptyLayout: LinearLayout
    private lateinit var resetFiltersButton: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupListeners()

        observePokemons()
        viewModel.refreshPokemons()
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.pokemon_recycler_view)
        searchEditText = view.findViewById(R.id.search_edit_text)
        emptyLayout = view.findViewById(R.id.empty_layout)
        resetFiltersButton = view.findViewById(R.id.reset_filters_button)
    }

    private fun setupRecyclerView() {
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val columnWidthDp = 180 // approximate width of your item
        val spanCount = (screenWidthDp / columnWidthDp).toInt().coerceAtLeast(2)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), spanCount)
        adapter = PokemonAdapter()
        recyclerView.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            emptyLayout.visibility = if (
                loadState.refresh is androidx.paging.LoadState.NotLoading &&
                adapter.itemCount == 0
            ) View.VISIBLE else View.GONE
        }
    }

    private fun setupListeners() {
        resetFiltersButton.setOnClickListener {
            viewModel.clearTypeFilters()
            viewModel.setSearchQuery("") // clear search query in ViewModel

            searchEditText.setText("") // clear the actual text in the search bar

            viewModel.refreshPokemons() // reload the list
            Toast.makeText(requireContext(), "Filters cleared", Toast.LENGTH_SHORT).show()
        }

        setupSearch()
        setupFilterClick()
    }

    private fun observePokemons() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pokemonPagingFlow.collect { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    private fun setupSearch() {
        val searchEditText = requireView().findViewById<TextInputEditText>(R.id.search_edit_text)
        searchEditText.addTextChangedListener { text ->
            viewModel.setSearchQuery(text?.toString().orEmpty())
        }
    }

    private fun setupFilterClick() {
        val searchLayout = requireView().findViewById<TextInputLayout>(R.id.search_layout)
        searchLayout.setEndIconOnClickListener {
            val filterSheet = FilterBottomSheetFragment(
                onApply = { selectedTypes ->
                    viewModel.setTypeFilters(selectedTypes)

                    Toast.makeText(
                        requireContext(),
                        "Filtering by: ${selectedTypes.joinToString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
            filterSheet.show(parentFragmentManager, "FilterBottomSheet")
        }
    }
}