package com.example.trandify1.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.trandify1.R
import com.example.trandify1.data.model.Product
import com.example.trandify1.databinding.FragmentProductDetailsBinding
import com.example.trandify1.viewmodel.MainViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private var product: Product? = null
    private var selectedSize: String = "M"

    companion object {
        private const val ARG_PRODUCT = "arg_product"
        fun newInstance(product: Product): ProductDetailsFragment {
            return ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PRODUCT, product)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = arguments?.getSerializable(ARG_PRODUCT) as? Product
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prod = product ?: run {
            parentFragmentManager.popBackStack()
            return
        }

        with(binding) {
            tvDetailsBrand.text = prod.brand
            tvDetailsTitle.text = prod.title
            tvDetailsPrice.text = String.format("₹%.2f", prod.price)
            tvDetailsOriginalPrice.text = String.format("₹%.2f", prod.originalPrice)
            tvDetailsRating.text = String.format("%.1f (%d reviews)", prod.rating, prod.reviewCount)
            tvDetailsDescription.text = prod.description

            val galleryAdapter = ImageGalleryAdapter(prod.imageUrls)
            vpProductGallery.adapter = galleryAdapter

            toolbarDetails.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }

            // Size Chips
            chipGroupSizes.removeAllViews()
            prod.sizes.forEachIndexed { index, size ->
                val chip = Chip(requireContext()).apply {
                    text = size
                    isCheckable = true
                    isChecked = (index == 0)
                    if (isChecked) selectedSize = size
                    setChipBackgroundColorResource(if (isChecked) R.color.primary_charcoal else R.color.app_surface)
                    setTextColor(if (isChecked) resources.getColor(R.color.primary_on, null) else resources.getColor(R.color.text_primary, null))
                    setOnCheckedChangeListener { _, checked ->
                        if (checked) {
                            selectedSize = size
                            for (i in 0 until chipGroupSizes.childCount) {
                                val c = chipGroupSizes.getChildAt(i) as? Chip
                                val isSel = (c == this)
                                c?.setChipBackgroundColorResource(if (isSel) R.color.primary_charcoal else R.color.app_surface)
                                c?.setTextColor(if (isSel) resources.getColor(R.color.primary_on, null) else resources.getColor(R.color.text_primary, null))
                            }
                        }
                    }
                }
                chipGroupSizes.addView(chip)
            }

            btnDetailsAddCart.setOnClickListener {
                if (com.example.trandify1.utils.AirplaneModeUtils.isAirplaneModeOn(requireContext())) {
                    Snackbar.make(binding.root, "Cannot add to cart while Airplane Mode is active", Snackbar.LENGTH_SHORT).show()
                } else {
                    viewModel.addToCart(prod, selectedSize)
                    Snackbar.make(binding.root, "${prod.title} ($selectedSize) added to cart", Snackbar.LENGTH_SHORT).show()
                }
            }

            btnDetailsBuyNow.setOnClickListener {
                if (com.example.trandify1.utils.AirplaneModeUtils.isAirplaneModeOn(requireContext())) {
                    Snackbar.make(binding.root, "Cannot proceed to checkout while Airplane Mode is active", Snackbar.LENGTH_SHORT).show()
                } else {
                    viewModel.addToCart(prod, selectedSize)
                    val activity = activity as? com.example.trandify1.MainActivity
                    activity?.navigateToTab(R.id.nav_cart)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}