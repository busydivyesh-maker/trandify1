package com.example.trandify1.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.trandify1.R
import com.example.trandify1.databinding.ItemBannerSlideBinding

class ImageGalleryAdapter(
    private val imageUrls: List<String>
) : RecyclerView.Adapter<ImageGalleryAdapter.GalleryViewHolder>() {

    inner class GalleryViewHolder(val binding: ItemBannerSlideBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemBannerSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val url = imageUrls[position]
        with(holder.binding) {
            tvBannerCapsTitle.text = ""
            tvBannerSubtitle.text = ""
            tvLiveBadge.visibility = android.view.View.GONE
            ivBannerImage.load(url) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder_clothing)
                error(R.drawable.ic_placeholder_clothing)
            }
        }
    }

    override fun getItemCount(): Int = imageUrls.size
}