package com.example.trandify1.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.trandify1.R
import com.example.trandify1.data.SampleData
import com.example.trandify1.data.model.Product
import com.example.trandify1.databinding.FragmentHomeBinding
import com.example.trandify1.ui.details.ProductDetailsFragment
import com.example.trandify1.viewmodel.MainViewModel
import com.example.trandify1.viewmodel.SortOption
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var productAdapter: ProductAdapter

    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBannerCarousel()
        setupCategoryChips()
        setupProductsGrid()
        setupSearchAndFilter()
        setupSwipeRefresh()
        observeData()

        // Initial Skeleton Shimmer Simulation
        simulateShimmerLoading()
    }

    private fun setupBannerCarousel() {
        val bannerAdapter = BannerAdapter(SampleData.bannerSlides)
        binding.bannerViewPager.adapter = bannerAdapter

        // Parallax / Scale Page Transformer
        binding.bannerViewPager.setPageTransformer { page, position ->
            val absPos = Math.abs(position)
            page.scaleY = 0.85f + (1 - absPos) * 0.15f
            page.alpha = 0.5f + (1 - absPos) * 0.5f
        }

        setupBannerDots(SampleData.bannerSlides.size)

        binding.bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateBannerDots(position)
            }
        })

        // Auto Scroll every 4s
        bannerRunnable = object : Runnable {
            override fun run() {
                val current = binding.bannerViewPager.currentItem
                val next = (current + 1) % SampleData.bannerSlides.size
                binding.bannerViewPager.setCurrentItem(next, true)
                autoScrollHandler.postDelayed(this, 4000)
            }
        }
        autoScrollHandler.postDelayed(bannerRunnable!!, 4000)
    }

    private fun setupBannerDots(count: Int) {
        binding.bannerDotContainer.removeAllViews()
        for (i in 0 until count) {
            val dot = ImageView(requireContext()).apply {
                val size = if (i == 0) 24 else 8
                val params = LinearLayout.LayoutParams(size, 8).apply {
                    setMargins(6, 0, 6, 0)
                }
                layoutParams = params
                setImageResource(R.drawable.bg_live_badge)
                alpha = if (i == 0) 1.0f else 0.4f
            }
            binding.bannerDotContainer.addView(dot)
        }
    }

    private fun updateBannerDots(selectedPos: Int) {
        val container = binding.bannerDotContainer
        for (i in 0 until container.childCount) {
            val dot = container.getChildAt(i) as? ImageView ?: continue
            if (i == selectedPos) {
                dot.layoutParams.width = 24
                dot.alpha = 1.0f
            } else {
                dot.layoutParams.width = 8
                dot.alpha = 0.4f
            }
            dot.requestLayout()
        }
    }

    private fun setupCategoryChips() {
        val categories = SampleData.categories
        binding.chipGroupCategories.removeAllViews()

        categories.forEachIndexed { index, cat ->
            val chip = Chip(requireContext()).apply {
                text = cat.name
                isCheckable = true
                isChecked = (index == 0)
                setChipBackgroundColorResource(
                    if (isChecked) R.color.primary_charcoal else R.color.app_surface
                )
                setTextColor(
                    if (isChecked) resources.getColor(R.color.primary_on, null)
                    else resources.getColor(R.color.text_primary, null)
                )
                setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        viewModel.setSelectedCategory(cat.name)
                        // Update chip colors
                        for (i in 0 until binding.chipGroupCategories.childCount) {
                            val child = binding.chipGroupCategories.getChildAt(i) as? Chip
                            val isSelected = child == this
                            child?.setChipBackgroundColorResource(
                                if (isSelected) R.color.primary_charcoal else R.color.app_surface
                            )
                            child?.setTextColor(
                                if (isSelected) resources.getColor(R.color.primary_on, null)
                                else resources.getColor(R.color.text_primary, null)
                            )
                        }
                    }
                }
            }
            binding.chipGroupCategories.addView(chip)
        }
    }

    private fun setupProductsGrid() {
        productAdapter = ProductAdapter(
            onProductClick = { product ->
                openProductDetails(product)
            },
            onWishlistClick = { product ->
                viewModel.toggleWishlist(product.id)
            },
            onAddToCartClick = { product ->
                if (com.example.trandify1.utils.AirplaneModeUtils.isAirplaneModeOn(requireContext())) {
                    Snackbar.make(binding.root, "Cannot add to cart while Airplane Mode is active", Snackbar.LENGTH_SHORT).show()
                } else {
                    viewModel.addToCart(product)
                    Snackbar.make(binding.root, "${product.title} added to bag!", Snackbar.LENGTH_SHORT).show()
                }
            }
        )

        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun setupSearchAndFilter() {
        binding.etHomeSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.setSearchQuery(text.toString())
        }

        binding.btnTopSearch.setOnClickListener {
            binding.etHomeSearch.requestFocus()
        }

        binding.btnFilterDialog.setOnClickListener {
            val filterBottomSheet = FilterBottomSheetDialogFragment(viewModel)
            filterBottomSheet.show(parentFragmentManager, "FilterBottomSheet")
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            simulateShimmerLoading()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun simulateShimmerLoading() {
        binding.shimmerContainer.visibility = View.VISIBLE
        binding.rvProducts.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            if (_binding != null) {
                binding.shimmerContainer.visibility = View.GONE
                binding.rvProducts.visibility = View.VISIBLE
            }
        }, 1200)
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.products,
                viewModel.selectedCategory,
                viewModel.searchQuery,
                viewModel.sortOption,
                viewModel.priceRange
            ) { products, category, query, sort, range ->
                var filtered = products.filter { product ->
                    val matchesCategory = (category == "All" || product.category.equals(category, ignoreCase = true))
                    val matchesQuery = query.isEmpty() || product.title.contains(query, ignoreCase = true) || product.brand.contains(query, ignoreCase = true)
                    val matchesPrice = product.price in range
                    matchesCategory && matchesQuery && matchesPrice
                }

                filtered = when (sort) {
                    SortOption.PRICE_LOW_HIGH -> filtered.sortedBy { it.price }
                    SortOption.PRICE_HIGH_LOW -> filtered.sortedByDescending { it.price }
                    SortOption.NEWEST -> filtered.reversed()
                    SortOption.POPULARITY -> filtered.sortedByDescending { it.rating }
                }

                filtered
            }.collect { filteredList ->
                productAdapter.submitList(filteredList)
            }
        }
    }

    private fun openProductDetails(product: Product) {
        val detailsFragment = ProductDetailsFragment.newInstance(product)
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_left
            )
            .replace(R.id.nav_host_fragment, detailsFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bannerRunnable?.let { autoScrollHandler.removeCallbacks(it) }
        _binding = null
    }
}