package com.example.makeupstoreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.makeupstoreapp.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    product: Product,
    onBack: () -> Unit,
    onProductUpdated: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf(product.name) }
    var brand by remember { mutableStateOf(product.brand) }
    var category by remember { mutableStateOf(product.category) }
    var subcategory by remember { mutableStateOf(product.subcategory) }
    var productGroupId by remember { mutableStateOf(product.productGroupId) }
    var shadeName by remember { mutableStateOf(product.shadeName) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var stock by remember { mutableStateOf(product.stock.toString()) }
    var color by remember { mutableStateOf(product.color) }
    var rating by remember { mutableStateOf(product.rating.toString()) }
    var imageUrl by remember { mutableStateOf(product.imageUrl) }
    var description by remember { mutableStateOf(product.description) }
    var isPopular by remember { mutableStateOf(product.isPopular) }
    var isOffer by remember { mutableStateOf(product.isOffer) }
    var message by remember { mutableStateOf("") }
    var attemptedSave by remember { mutableStateOf(false) }

    val subcategories = when (category) {
        "Față" -> listOf("Fond de ten", "Pudră", "Concealer", "Contouring", "Iluminatoare", "Skincare")
        "Ochi" -> listOf("Mascara", "Eyeliner", "Fard", "Gene")
        "Buze" -> listOf("Ruj mat", "Ruj lucios", "Gloss", "Creion de buze")
        "Păr" -> listOf("Șampon", "Mască / balsam", "Leave-in", "Accesorii", "Produse de îngrijire")
        "Parfumuri" -> listOf("Parfumuri")
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editează produs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Înapoi")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nume produs") },
                isError = attemptedSave && name.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Brand") },
                isError = attemptedSave && brand.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            AdminDropdown(
                label = "Categorie",
                selectedValue = category,
                values = listOf("Față", "Ochi", "Buze", "Păr", "Parfumuri"),
                onValueSelected = {
                    category = it
                    subcategory = when (it) {
                        "Față" -> "Fond de ten"
                        "Ochi" -> "Mascara"
                        "Buze" -> "Ruj mat"
                        "Păr" -> "Șampon"
                        "Parfumuri" -> "Parfumuri"
                        else -> ""
                    }
                }
            )

            Spacer(Modifier.height(10.dp))

            AdminDropdown(
                label = "Subcategorie",
                selectedValue = subcategory,
                values = subcategories,
                onValueSelected = { subcategory = it }
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = productGroupId,
                onValueChange = { productGroupId = it },
                label = { Text("Product Group ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = shadeName,
                onValueChange = { shadeName = it },
                label = { Text("Nume nuanță") },
                isError = attemptedSave && shadeName.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Preț") },
                isError = attemptedSave && price.toDoubleOrNull() == null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stoc") },
                isError = attemptedSave && stock.toIntOrNull() == null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Culoare HEX") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text("Rating") },
                isError = attemptedSave && rating.toDoubleOrNull() == null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descriere") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isPopular,
                    onCheckedChange = { isPopular = it }
                )
                Text("Popular")

                Spacer(Modifier.width(16.dp))

                Checkbox(
                    checked = isOffer,
                    onCheckedChange = { isOffer = it }
                )
                Text("Ofertă")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    attemptedSave = true
                    message = ""

                    if (
                        name.isBlank() ||
                        brand.isBlank() ||
                        shadeName.isBlank() ||
                        price.toDoubleOrNull() == null ||
                        stock.toIntOrNull() == null ||
                        rating.toDoubleOrNull() == null
                    ) {
                        message = "Completează corect câmpurile obligatorii."
                        return@Button
                    }

                    val finalGroupId = if (productGroupId.isBlank()) {
                        name.lowercase()
                            .replace(" ", "-")
                            .replace(Regex("[^a-z0-9-]"), "")
                    } else {
                        productGroupId
                    }

                    val updatedProduct = mapOf(
                        "name" to name,
                        "brand" to brand,
                        "category" to category,
                        "subcategory" to subcategory,
                        "productGroupId" to finalGroupId,
                        "shadeName" to shadeName,
                        "price" to (price.toDoubleOrNull() ?: 0.0),
                        "description" to description,
                        "imageUrl" to imageUrl,
                        "color" to color,
                        "isPopular" to isPopular,
                        "isOffer" to isOffer,
                        "rating" to (rating.toDoubleOrNull() ?: 0.0),
                        "stock" to (stock.toIntOrNull() ?: 0)
                    )

                    db.collection("products")
                        .document(product.id)
                        .update(updatedProduct)
                        .addOnSuccessListener {
                            onProductUpdated()
                        }
                        .addOnFailureListener {
                            message = "Produsul nu a putut fi actualizat."
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Salvează modificările")
            }

            if (message.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}