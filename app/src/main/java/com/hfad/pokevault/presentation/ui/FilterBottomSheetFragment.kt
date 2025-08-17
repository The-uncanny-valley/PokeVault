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

    private val selectedTypes = mutableSetOf<String>()
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

        observeTypes(chipGroup)
        setupApplyButton(applyButton)

        viewModel.loadTypes()
    }

    private fun observeTypes(chipGroup: ChipGroup) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.types.collect { types ->
                    chipGroup.removeAllViews() // clear any existing chips in the chip group
                    // For each type, create a Chip view
                    types.forEach { type ->
                        chipGroup.addView(createTypeChip(type.name))
                    }
                }
            }
        }
    }

    private fun createTypeChip(typeName: String): Chip {
        return Chip(requireContext()).apply {
            text = typeName.replaceFirstChar { it.uppercase() }
            isCheckable = true
            chipStrokeWidth = 0f

            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedTypes.add(typeName)
                else selectedTypes.remove(typeName)
            }
        }
    }

    private fun setupApplyButton(button: Button) {
        button.setOnClickListener {
            if (selectedTypes.isEmpty()) {
                Toast.makeText(requireContext(), "Select at least one type", Toast.LENGTH_SHORT).show()
            } else {
                onApply(selectedTypes.toList())
                dismiss()
            }
        }
    }
}