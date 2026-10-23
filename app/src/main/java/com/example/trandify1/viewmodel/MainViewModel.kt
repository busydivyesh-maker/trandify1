package com.example.trandify1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trandify1.data.model.CartItem
import com.example.trandify1.data.model.Product
import com.example.trandify1.data.model.UserProfile
import com.example.trandify1.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SortOption { POPULARITY, PRICE_LOW_HIGH, PRICE_HIGH_LOW, NEWEST }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = ShopRepository(application)

    val products = repository.products
    val cartItems = repository.cartItems
    val wishlistIds = repository.wishlistIds
    val userProfile = repository.userProfile
    val appliedPromo = repository.appliedPromo

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.POPULARITY)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _priceRange = MutableStateFlow(0f..10000f)
    val priceRange: StateFlow<ClosedFloatingPointRange<Float>> = _priceRange.asStateFlow()

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun setPriceRange(range: ClosedFloatingPointRange<Float>) {
        _priceRange.value = range
    }

    fun toggleWishlist(productId: String) {
        repository.toggleWishlist(productId)
    }

    fun addToCart(product: Product, size: String = "M", colorHex: String = "#111111") {
        repository.addToCart(product, size, colorHex)
    }

    fun updateCartQuantity(item: CartItem, qty: Int) {
        repository.updateCartQuantity(item, qty)
    }

    fun removeCartItem(item: CartItem) {
        repository.removeCartItem(item)
    }

    fun restoreCartItem(item: CartItem, index: Int) {
        repository.restoreCartItem(item, index)
    }

    fun applyPromoCode(code: String): Boolean {
        return repository.applyPromoCode(code)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun saveUserProfile(profile: UserProfile) {
        repository.saveUserProfile(profile)
    }
}