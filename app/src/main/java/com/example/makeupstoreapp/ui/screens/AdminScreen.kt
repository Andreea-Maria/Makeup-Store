package com.example.makeupstoreapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.makeupstoreapp.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun AdminScreen(
    onAddProductClick: () -> Unit,
    onAddDiscountClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("products")
            .addSnapshotListener { result, _ ->
                if (result != null) {
                    products = result.documents.map { doc ->
                        Product(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            brand = doc.getString("brand") ?: "",
                            category = doc.getString("category") ?: "",
                            subcategory = doc.getString("subcategory") ?: "",
                            productGroupId = doc.getString("productGroupId") ?: "",
                            shadeName = doc.getString("shadeName") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            description = doc.getString("description") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            color = doc.getString("color") ?: "#FFFFFF",
                            isPopular = doc.getBoolean("isPopular") ?: false,
                            isOffer = doc.getBoolean("isOffer") ?: false,
                            rating = doc.getDouble("rating") ?: 0.0,
                            stock = doc.getLong("stock")?.toInt() ?: 0
                        )
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onAddProductClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adaugă produs nou")
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = onAddDiscountClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Adaugă cod reducere")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            items(products) { product ->
                ProductAdminItem(product, db)
            }
        }
    }
}

@Composable
fun ProductAdminItem(
    product: Product,
    db: FirebaseFirestore
) {
    var stockText by remember { mutableStateOf(product.stock.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(product.name, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            Text("Stoc actual: ${product.stock}")

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = stockText,
                onValueChange = { stockText = it },
                label = { Text("Modifică stoc") },
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val newStock = stockText.toIntOrNull() ?: 0

                    db.collection("products")
                        .document(product.id)
                        .update("stock", newStock)
                }
            ) {
                Text("Salvează")
            }
        }
    }
}

