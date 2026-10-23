package com.example.trandify1.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.trandify1.R
import com.example.trandify1.data.model.CartItem
import com.example.trandify1.databinding.ItemCartProductBinding

class CartAdapter(
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onDeleteItem: (CartItem, Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    inner class CartViewHolder(val binding: ItemCartProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = getItem(position)
        with(holder.binding) {
            tvCartProductTitle.text = cartItem.product.title
            tvCartProductVariant.text = "Size: ${cartItem.selectedSize}"
            tvCartProductPrice.text = String.format("₹%.2f", cartItem.totalPrice)
            tvCartQuantity.text = cartItem.quantity.toString()

            ivCartProductImage.load(cartItem.product.imageUrls.firstOrNull()) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder_clothing)
                error(R.drawable.ic_placeholder_clothing)
            }

            btnPlus.setOnClickListener {
                onQuantityChange(cartItem, cartItem.quantity + 1)
            }

            btnMinus.setOnClickListener {
                if (cartItem.quantity > 1) {
                    onQuantityChange(cartItem, cartItem.quantity - 1)
                } else {
                    onDeleteItem(cartItem, holder.bindingAdapterPosition)
                }
            }

            btnDeleteCartItem.setOnClickListener {
                onDeleteItem(cartItem, holder.bindingAdapterPosition)
            }
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
            oldItem.product.id == newItem.product.id && oldItem.selectedSize == newItem.selectedSize

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
            oldItem == newItem
    }
}