package com.example.trandify1.data.model

data class UserProfile(
    val name: String = "",
    val address: String = "",
    val email: String = "",
    val phone: String = "",
    val dob: String = "",
    val imageUriPath: String? = null
)