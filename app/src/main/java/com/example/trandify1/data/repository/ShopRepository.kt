package com.example.trandify1.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.trandify1.data.db.TrendifyyDatabaseHelper
import com.example.trandify1.data.model.CartItem
import com.example.trandify1.data.model.Product
import com.example.trandify1.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.*

class ShopRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("trendifyy_prefs", Context.MODE_PRIVATE)
    private val dbHelper = TrendifyyDatabaseHelper(context)

    private val _products = MutableStateFlow<List<Product>>(dbHelper.getAllProducts())
    val products: StateFlow<List<Product>> = _products

    private val _cartItems = MutableStateFlow<List<CartItem>>(dbHelper.getCartItems(_products.value))
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _wishlistIds = MutableStateFlow<Set<String>>(
        _products.value.filter { it.isWishlisted }.map { it.id }.toSet()
    )
    val wishlistIds: StateFlow<Set<String>> = _wishlistIds

    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    private val _appliedPromo = MutableStateFlow<String?>(null)
    val appliedPromo: StateFlow<String?> = _appliedPromo

    init {
        val defaultEmail = "alex.morgan@trendifyy.com"
        if (dbHelper.getUserProfile(defaultEmail) == null) {
            dbHelper.registerUser(
                email = defaultEmail,
                pass = "password123",
                name = "Alex Morgan",
                phone = "9876543210",
                address = "742 Evergreen Terrace, NY",
                dob = "1998-05-15"
            )
        }

        // Fetch live products from REST API via Retrofit
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiProducts = com.example.trandify1.data.api.RetrofitClient.apiService.getProducts()
                val mappedProducts = apiProducts.map { api ->
                    Product(
                        id = "api_${api.id}",
                        title = api.title,
                        brand = api.category.replaceFirstChar { it.uppercase() },
                        description = api.description,
                        price = api.price * 80.0,
                        originalPrice = (api.price * 80.0) * 1.25,
                        discountPercentage = 20,
                        rating = api.rating.rate,
                        reviewCount = api.rating.count,
                        category = when (api.category.lowercase()) {
                            "men's clothing" -> "Men"
                            "women's clothing" -> "Women"
                            "jewelery", "electronics" -> "Accessories"
                            else -> "Women"
                        },
                        imageUrls = listOf(api.image)
                    )
                }
                if (mappedProducts.isNotEmpty()) {
                    _products.value = mappedProducts
                }
            } catch (_: Exception) {
                // Fallback to local SQLite / Sample products
            }
        }
    }

    fun toggleWishlist(productId: String) {
        val current = _wishlistIds.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _wishlistIds.value = current

        _products.value = _products.value.map {
            if (it.id == productId) it.copy(isWishlisted = current.contains(productId)) else it
        }
    }

    fun addToCart(product: Product, size: String = "M", colorHex: String = "#111111") {
        dbHelper.addToCart(product, size)
        _cartItems.value = dbHelper.getCartItems(_products.value)
    }

    fun updateCartQuantity(cartItem: CartItem, newQty: Int) {
        if (newQty <= 0) {
            removeCartItem(cartItem)
            return
        }
        removeCartItem(cartItem)
        for (i in 0 until newQty) {
            dbHelper.addToCart(cartItem.product, cartItem.selectedSize)
        }
        _cartItems.value = dbHelper.getCartItems(_products.value)
    }

    fun removeCartItem(cartItem: CartItem) {
        val current = _cartItems.value.toMutableList()
        current.removeAll { it.product.id == cartItem.product.id && it.selectedSize == cartItem.selectedSize }
        dbHelper.clearCart()
        for (item in current) {
            for (i in 0 until item.quantity) {
                dbHelper.addToCart(item.product, item.selectedSize)
            }
        }
        _cartItems.value = dbHelper.getCartItems(_products.value)
    }

    fun restoreCartItem(cartItem: CartItem, index: Int) {
        addToCart(cartItem.product, cartItem.selectedSize)
    }

    fun applyPromoCode(code: String): Boolean {
        return if (code.trim().equals("TRENDY20", ignoreCase = true)) {
            _appliedPromo.value = "TRENDY20"
            true
        } else {
            false
        }
    }

    fun clearCart() {
        dbHelper.clearCart()
        _cartItems.value = emptyList()
        _appliedPromo.value = null
    }

    fun saveUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        dbHelper.updateUserProfile(profile)
    }

    private fun loadUserProfile(): UserProfile {
        val loggedEmail = prefs.getString("logged_in_email", "alex.morgan@trendifyy.com") ?: "alex.morgan@trendifyy.com"
        return dbHelper.getUserProfile(loggedEmail) ?: UserProfile(
            name = "Alex Morgan",
            address = "742 Evergreen Terrace, NY",
            email = loggedEmail,
            phone = "9876543210",
            dob = "1998-05-15"
        )
    }
}