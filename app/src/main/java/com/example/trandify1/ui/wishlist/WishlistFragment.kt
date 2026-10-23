package com.example.trandify1.ui.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.trandify1.R
import com.example.trandify1.data.model.Product
import com.example.trandify1.databinding.FragmentWishlistBinding
import com.example.trandify1.ui.details.ProductDetailsFragment
import com.example.trandify1.ui.home.ProductAdapter
import com.example.trandify1.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeWishlist()

        binding.btnWishlistExplore.setOnClickListener {
            val activity = activity as? com.example.trandify1.MainActivity
            activity?.navigateToTab(R.id.nav_home)
        }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onProductClick = { product ->
                openProductDetails(product)
            },
            onWishlistClick = { product ->
                viewModel.toggleWishlist(product.id)
            },
            onAddToCartClick = { product ->
                viewModel.addToCart(product)
                Snackbar.make(binding.root, "${product.title} added to bag!", Snackbar.LENGTH_SHORT).show()
            }
        )

        binding.rvWishlist.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun observeWishlist() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(viewModel.products, viewModel.wishlistIds) { products, wishlist ->
                products.filter { wishlist.contains(it.id) }
            }.collect { wishlistedProducts ->
                if (wishlistedProducts.isEmpty()) {
                    binding.rvWishlist.visibility = View.GONE
                    binding.emptyWishlistContainer.visibility = View.VISIBLE
                } else {
                    binding.rvWishlist.visibility = View.VISIBLE
                    binding.emptyWishlistContainer.visibility = View.GONE
                    productAdapter.submitList(wishlistedProducts)
                }
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
        _binding = null
    }
}