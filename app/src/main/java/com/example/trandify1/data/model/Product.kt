package com.example.trandify1.data.model

import java.io.Serializable

data class Product(
    val id: String,
    val title: String,
    val brand: String,
    val description: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercentage: Int,
    val rating: Float,
    val reviewCount: Int,
    val category: String,
    val imageUrls: List<String>,
    val sizes: List<String> = listOf("XS", "S", "M", "L", "XL"),
    val colorsHex: List<String> = listOf("#111111", "#FF3B5C", "#7C5CFC", "#E0E0E6"),
    var isWishlisted: Boolean = false,
    val isLive: Boolean = false
) : Serializable