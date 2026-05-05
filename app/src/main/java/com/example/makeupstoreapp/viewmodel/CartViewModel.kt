package com.example.makeupstoreapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.makeupstoreapp.data.model.CartItem
import com.example.makeupstoreapp.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.FirebaseFirestore

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems
    private val _discountPercent = MutableStateFlow(0)
    val discountPercent: StateFlow<Int> = _discountPercent
    private val db = FirebaseFirestore.getInstance()

    fun applyDiscount(code: String, onResult: (Boolean) -> Unit) {

        db.collection("discountCodes")
            .whereEqualTo("code", code.uppercase().trim())
            .whereEqualTo("active", true)
            .get()
            .addOnSuccessListener { result ->

                if (!result.isEmpty) {
                    val document = result.documents.first()
                    val percent = document.getLong("percent")?.toInt() ?: 0

                    _discountPercent.value = percent
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getDiscountedTotal(): Double {
        val total = getTotalPrice()
        return total - (total * _discountPercent.value / 100.0)
    }

    fun addToCart(product: Product) {
        val currentItems = _cartItems.value
        val existingItem = currentItems.find { it.product.id == product.id }

        _cartItems.value = if (existingItem != null) {
            currentItems.map {
                if (it.product.id == product.id) {
                    if (it.quantity < product.stock) {
                        it.copy(quantity = it.quantity + 1)
                    } else {
                        it
                    }
                } else {
                    it
                }
            }
        } else {
            if (product.stock > 0) {
                currentItems + CartItem(product = product, quantity = 1)
            } else {
                currentItems
            }
        }
    }

    fun decreaseQuantity(product: Product) {
        val currentItems = _cartItems.value
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            _cartItems.value = if (existingItem.quantity > 1) {
                currentItems.map {
                    if (it.product.id == product.id) {
                        it.copy(quantity = it.quantity - 1)
                    } else {
                        it
                    }
                }
            } else {
                currentItems.filter { it.product.id != product.id }
            }
        }
    }

    fun removeFromCart(product: Product) {
        _cartItems.value = _cartItems.value.filter { it.product.id != product.id }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getTotalItems(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }

    fun getTotalPrice(): Double {
        return _cartItems.value.sumOf { it.product.price * it.quantity }
    }
}