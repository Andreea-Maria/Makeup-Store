package com.example.makeupstoreapp.data.model

data class Product(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val category: String = "",
    val subcategory: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val color: String = "#FFFFFF",
    val isPopular: Boolean = false,
    val isOffer: Boolean = false,
    val productGroupId: String = "",
    val shadeName: String = "",
    val rating: Double = 0.0,
    val stock: Int = 0
)