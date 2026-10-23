package com.example.trandify1.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.trandify1.R
import com.example.trandify1.data.SampleData
import com.example.trandify1.databinding.FragmentCategoriesBinding
import com.example.trandify1.ui.home.HomeFragment
import com.example.trandify1.viewmodel.MainViewModel

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CategoryAdapter(SampleData.categories) { category ->
            viewModel.setSelectedCategory(category.name)
            // Navigate back to Home tab with selected category
            val activity = activity as? com.example.trandify1.MainActivity
            activity?.navigateToTab(R.id.nav_home)
        }

        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}