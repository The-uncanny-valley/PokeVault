package com.hfad.pokevault.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.hfad.pokevault.R
import com.hfad.pokevault.presentation.viewmodel.PokemonTypeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterBottomSheetFragment(
    private val onApply: (List<String>) -> Unit // callback to return selected types
) : BottomSheetDialogFragment() {

    // list to store all selected types
    private val selectedTypes = mutableListOf<String>()
    private val viewModel: PokemonTypeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chipGroup = view.findViewById<ChipGroup>(R.id.type_chip_group)
        val applyButton = view.findViewById<Button>(R.id.apply_button)

        // Launch a coroutine tied to the fragment's view lifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.types.collect { types ->
                    // Clear any existing chips in the chip group
                    chipGroup.removeAllViews()
                    // For each type, create a Chip view
                    types.forEach { type ->
                        val chip = Chip(requireContext()).apply {
                            text = type.name.replaceFirstChar { it.uppercase() }
                            isCheckable = true
                            // Update selectedTypes set when the chip is checked or unchecked
                            setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) selectedTypes.add(type.name)
                                else selectedTypes.remove(type.name)
                            }
                        }
                        // Add the chip to the chip group
                        chipGroup.addView(chip)
                    }
                }
            }
        }

        applyButton.setOnClickListener {
            if (selectedTypes.isEmpty()) {
                Toast.makeText(requireContext(), "Select at least one type", Toast.LENGTH_SHORT).show()
            } else {
                onApply(selectedTypes)
                dismiss()
            }
        }

        // Load types
        viewModel.loadTypes()
    }
}