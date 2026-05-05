package com.example.makeupstoreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Order(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val paymentMethod: String = "",
    val total: Double = 0.0,
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen(
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    orders = result.documents.map { document ->
                        Order(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            phone = document.getString("phone") ?: "",
                            address = document.getString("address") ?: "",
                            city = document.getString("city") ?: "",
                            paymentMethod = document.getString("paymentMethod") ?: "",
                            total = document.getDouble("total") ?: 0.0,
                            timestamp = document.getLong("timestamp") ?: 0L
                        )
                    }.sortedByDescending { it.timestamp }

                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Istoric comenzi") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Înapoi")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(20.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                orders.isEmpty() -> {
                    EmptyOrdersCard(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders) { order ->
                            OrderCard(order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyOrdersCard(
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.History,
                contentDescription = "Istoric comenzi",
                tint = Color(0xFFB85C7A),
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text("Nu ai comenzi plasate momentan.")
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    val formattedDate = remember(order.timestamp) {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        formatter.format(Date(order.timestamp))
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Comandă #${order.id.take(6)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = formattedDate,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(10.dp))

            Text("Livrare: ${order.address}, ${order.city}")
            Text("Telefon: ${order.phone}")
            Text("Plată: ${order.paymentMethod}")

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Total: %.2f lei".format(order.total),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFB85C7A)
            )
        }
    }
}