package com.example.trandify1.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trandify1.R
import com.example.trandify1.databinding.DialogFilterBottomSheetBinding
import com.example.trandify1.viewmodel.MainViewModel
import com.example.trandify1.viewmodel.SortOption
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheetDialogFragment(
    private val viewModel: MainViewModel
) : BottomSheetDialogFragment() {

    private var _binding: DialogFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sync current state
        when (viewModel.sortOption.value) {
            SortOption.POPULARITY -> binding.rbPopularity.isChecked = true
            SortOption.PRICE_LOW_HIGH -> binding.rbPriceLowHigh.isChecked = true
            SortOption.PRICE_HIGH_LOW -> binding.rbPriceHighLow.isChecked = true
            SortOption.NEWEST -> binding.rbNewest.isChecked = true
        }

        val range = viewModel.priceRange.value
        binding.sliderPriceRange.setValues(range.start, range.endInclusive)

        binding.btnApplyFilter.setOnClickListener {
            val selectedSort = when (binding.rgSortOptions.checkedRadioButtonId) {
                R.id.rb_price_low_high -> SortOption.PRICE_LOW_HIGH
                R.id.rb_price_high_low -> SortOption.PRICE_HIGH_LOW
                R.id.rb_newest -> SortOption.NEWEST
                else -> SortOption.POPULARITY
            }
            viewModel.setSortOption(selectedSort)

            val values = binding.sliderPriceRange.values
            if (values.size >= 2) {
                viewModel.setPriceRange(values[0]..values[1])
            }

            dismiss()
        }

        binding.btnResetFilter.setOnClickListener {
            viewModel.setSortOption(SortOption.POPULARITY)
            viewModel.setPriceRange(0f..10000f)
            binding.rbPopularity.isChecked = true
            binding.sliderPriceRange.setValues(0f, 10000f)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}