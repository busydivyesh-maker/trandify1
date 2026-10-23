package com.example.trandify1.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.trandify1.R
import com.example.trandify1.data.model.Product
import com.example.trandify1.databinding.ItemProductCardBinding

class ProductAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onWishlistClick: (Product) -> Unit,
    private val onAddToCartClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    inner class ProductViewHolder(val binding: ItemProductCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        with(holder.binding) {
            tvProductBrand.text = product.brand
            tvProductTitle.text = product.title
            tvPrice.text = String.format("₹%.2f", product.price)
            tvOriginalPrice.text = String.format("₹%.2f", product.originalPrice)
            tvDiscountBadge.text = "-${product.discountPercentage}%"
            tvRating.text = String.format("%.1f (%d)", product.rating, product.reviewCount)

            btnWishlist.setImageResource(
                if (product.isWishlisted) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )

            ivProductImage.load(product.imageUrls.firstOrNull()) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder_clothing)
                error(R.drawable.ic_placeholder_clothing)
            }

            btnWishlist.setOnClickListener {
                it.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.item_animation_fall_down))
                onWishlistClick(product)
            }

            btnAddToBag.setOnClickListener {
                onAddToCartClick(product)
            }

            cardProduct.setOnClickListener {
                onProductClick(product)
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
    }
}