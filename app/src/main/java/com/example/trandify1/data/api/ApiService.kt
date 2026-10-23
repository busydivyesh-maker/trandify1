package com.example.trandify1.data.api

import retrofit2.http.GET

data class ApiProduct(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: ApiRating
)

data class ApiRating(
    val rate: Float,
    val count: Int
)

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<ApiProduct>

    companion object {
        const val BASE_URL = "https://fakestoreapi.com/"
    }
}