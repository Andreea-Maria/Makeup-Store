package com.example.makeupstoreapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.makeupstoreapp.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadProducts() {
        _isLoading.value = true

        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val productList = result.documents.map { document ->
                    Product(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        brand = document.getString("brand") ?: "",
                        category = document.getString("category") ?: "",
                        subcategory = document.getString("subcategory") ?: "",
                        price = document.getDouble("price") ?: 0.0,
                        description = document.getString("description") ?: "",
                        imageUrl = document.getString("imageUrl") ?: "",
                        color = document.getString("color") ?: "#FFFFFF",
                        isPopular = document.getBoolean("isPopular") ?: false,
                        isOffer = document.getBoolean("isOffer") ?: false,
                        productGroupId = document.getString("productGroupId") ?: "",
                        shadeName = document.getString("shadeName") ?: "",
                        rating = document.getDouble("rating") ?: 0.0,
                        stock = document.getLong("stock")?.toInt() ?: 0
                    )
                }

                _products.value = productList
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }
}