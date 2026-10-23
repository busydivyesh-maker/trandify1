package com.example.trandify1.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.trandify1.R
import com.example.trandify1.data.BannerSlide
import com.example.trandify1.databinding.ItemBannerSlideBinding

class BannerAdapter(
    private val slides: List<BannerSlide>
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(val binding: ItemBannerSlideBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val slide = slides[position]
        with(holder.binding) {
            tvBannerCapsTitle.text = slide.title
            tvBannerSubtitle.text = slide.subtitle
            tvLiveBadge.visibility = if (slide.isLive) View.VISIBLE else View.GONE

            ivBannerImage.load(slide.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder_clothing)
                error(R.drawable.ic_placeholder_clothing)
            }
        }
    }

    override fun getItemCount(): Int = slides.size
}