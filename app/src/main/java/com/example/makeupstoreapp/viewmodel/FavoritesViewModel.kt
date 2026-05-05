package com.example.makeupstoreapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.makeupstoreapp.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoritesViewModel : ViewModel() {

    private val _favoriteProducts = MutableStateFlow<List<Product>>(emptyList())
    val favoriteProducts: StateFlow<List<Product>> = _favoriteProducts

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun loadFavorites(allProducts: List<Product>) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .get()
            .addOnSuccessListener { result ->
                val favoriteIds = result.documents.map { it.id }

                _favoriteProducts.value = allProducts.filter { product ->
                    favoriteIds.contains(product.id)
                }
            }
    }

    fun toggleFavorite(product: Product) {
        val userId = auth.currentUser?.uid ?: return
        val currentList = _favoriteProducts.value
        val isAlreadyFavorite = currentList.any { it.id == product.id }

        if (isAlreadyFavorite) {
            _favoriteProducts.value = currentList.filter { it.id != product.id }

            db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(product.id)
                .delete()
        } else {
            _favoriteProducts.value = currentList + product

            db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(product.id)
                .set(
                    mapOf(
                        "productId" to product.id
                    )
                )
        }
    }

    fun isFavorite(product: Product): Boolean {
        return _favoriteProducts.value.any { it.id == product.id }
    }
}