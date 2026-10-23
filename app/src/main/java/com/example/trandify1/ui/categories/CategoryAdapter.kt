package com.example.trandify1.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.trandify1.R
import com.example.trandify1.data.model.Category
import com.example.trandify1.databinding.ItemCategoryTileBinding

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryTileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        with(holder.binding) {
            tvCategoryName.text = category.name
            tvCategoryCount.text = "${category.itemCount} Items"

            ivCategoryImage.load(category.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder_clothing)
                error(R.drawable.ic_placeholder_clothing)
            }

            cardCategoryTile.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }

    override fun getItemCount(): Int = categories.size
}