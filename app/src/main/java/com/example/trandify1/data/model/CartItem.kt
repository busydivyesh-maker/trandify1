package com.example.trandify1.data.model

data class CartItem(
    val product: Product,
    var selectedSize: String = "M",
    var selectedColorHex: String = "#111111",
    var quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.price * quantity
}