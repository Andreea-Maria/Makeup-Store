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
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBack: () -> Unit,
    onProductSaved: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Față") }
    var subcategory by remember { mutableStateOf("Fond de ten") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("#FFFFFF") }
    var rating by remember { mutableStateOf("4.5") }
    var isPopular by remember { mutableStateOf(false) }
    var isOffer by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var productGroupId by remember { mutableStateOf("") }
    var shadeName by remember { mutableStateOf("") }
    var attemptedSave by remember { mutableStateOf(false) }

    val subcategories = when (category) {
        "Față" -> listOf("Fond de ten", "Pudră", "Concealer", "Contouring", "Iluminatoare", "Skincare")
        "Ochi" -> listOf("Mascara", "Eyeliner", "Fard", "Gene")
        "Buze" -> listOf("Ruj mat", "Ruj lucios", "Gloss", "Creion de buze")
        "Păr" -> listOf("Șampon", "Mască / balsam", "Leave-in", "Accesorii", "Produse de îngrijire")
        "Parfumuri" -> listOf("Parfumuri")
        else -> emptyList()
    }

    LaunchedEffect(category) {
        subcategory = subcategories.firstOrNull() ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adaugă produs") },
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

            if (attemptedSave && name.isBlank()) {
                Text("Numele este obligatoriu", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Brand") },
                isError = attemptedSave && brand.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            if (attemptedSave && brand.isBlank()) {
                Text(
                    "Nuanța este obligatorie",
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(Modifier.height(10.dp))

            AdminDropdown(
                label = "Categorie",
                selectedValue = category,
                values = listOf("Față", "Ochi", "Buze", "Păr", "Parfumuri"),
                onValueSelected = { category = it }
            )

            Spacer(Modifier.height(10.dp))

            AdminDropdown(
                label = "Subcategorie",
                selectedValue = subcategory,
                values = subcategories,
                onValueSelected = { subcategory = it }
            )

            Spacer(Modifier.height(10.dp))

            val priceError = price.isBlank() || price.toDoubleOrNull() == null

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Preț") },
                isError = priceError && attemptedSave,
                modifier = Modifier.fillMaxWidth()
            )

            if (priceError && attemptedSave) {
                Text("Preț invalid", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(10.dp))

            val stockError = stock.isBlank() || stock.toIntOrNull() == null

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stoc") },
                isError = stockError && attemptedSave,
                modifier = Modifier.fillMaxWidth()
            )

            if (attemptedSave && stock.isBlank()) {
                Text(
                    "Nuanța este obligatorie",
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Culoare HEX pentru nuanță") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text("Rating") },
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

            OutlinedTextField(
                value = productGroupId,
                onValueChange = { productGroupId = it },
                label = { Text("Product Group ID (opțional)") },
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

            if (attemptedSave && shadeName.isBlank()) {
                Text(
                    "Nuanța este obligatorie",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isPopular, onCheckedChange = { isPopular = it })
                Text("Popular")

                Spacer(Modifier.width(16.dp))

                Checkbox(checked = isOffer, onCheckedChange = { isOffer = it })
                Text("Ofertă")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    attemptedSave = true

                    if (
                        name.isBlank() ||
                        brand.isBlank() ||
                        price.isBlank() ||
                        stock.isBlank() ||
                        shadeName.isBlank()
                    ) {
                        message = "Completează numele, brandul, prețul, stocul și nuanța."
                        return@Button
                    }

                    val finalGroupId = if (productGroupId.isBlank()) {
                        name.lowercase()
                            .replace(" ", "-")
                            .replace(Regex("[^a-z0-9-]"), "")
                    } else {
                        productGroupId
                    }

                    val product = hashMapOf(
                        "name" to name,
                        "brand" to brand,
                        "category" to category,
                        "subcategory" to subcategory,
                        "price" to (price.toDoubleOrNull() ?: 0.0),
                        "description" to description,
                        "imageUrl" to imageUrl,
                        "color" to color,
                        "isPopular" to isPopular,
                        "isOffer" to isOffer,
                        "rating" to (rating.toDoubleOrNull() ?: 0.0),
                        "stock" to (stock.toIntOrNull() ?: 0),
                        "productGroupId" to finalGroupId,
                        "shadeName" to shadeName
                    )

                    db.collection("products")
                        .add(product)
                        .addOnSuccessListener {
                            onProductSaved()
                        }
                        .addOnFailureListener {
                            message = "Produsul nu a putut fi salvat."
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Salvează produs")
            }

            if (message.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDropdown(
    label: String,
    selectedValue: String,
    values: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            values.forEach { value ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        onValueSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}