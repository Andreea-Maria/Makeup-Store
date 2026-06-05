package com.example.makeupstoreapp.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.makeupstoreapp.data.model.Product
import com.example.makeupstoreapp.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: Product,
    variants: List<Product>,
    favoritesViewModel: FavoritesViewModel,
    onVariantSelected: (Product) -> Unit,
    onBack: () -> Unit,
    onAddToCart: (Product) -> Unit,
    isAdmin: Boolean,
    onEditProduct: (Product) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val favoriteProducts by favoritesViewModel.favoriteProducts.collectAsState()
    val isFavorite = favoriteProducts.any { it.id == product.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalii produs") },
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
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )

                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clickable {
                                favoritesViewModel.toggleFavorite(product)

                                onShowMessage(
                                    if (isFavorite) {
                                        "Produs eliminat din favorite"
                                    } else {
                                        "Produs adăugat la favorite"
                                    }
                                )
                            }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${product.brand} • ${product.category}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            val usesQuantity =
                product.category == "Parfumuri" ||
                        product.category == "Păr"

            val hideVariants =
                product.subcategory == "Accesorii" ||
                        product.subcategory == "Gene"

            if (product.shadeName.isNotEmpty() && !hideVariants) {
                Spacer(Modifier.height(14.dp))

                Text(
                    text = if (usesQuantity) {
                        "Cantitate: ${product.shadeName}"
                    } else {
                        "Nuanță: ${product.shadeName}"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(18.dp))

            if (variants.isNotEmpty() && !hideVariants) {
                Text(
                    text = if (
                        product.category == "Parfumuri" ||
                        product.category == "Păr"
                    ) {
                        "Alege cantitatea"
                    } else {
                        "Alege nuanța"
                    }
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    variants.forEach { variant ->
                        if (usesQuantity) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = if (variant.id == product.id) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                border = ButtonDefaults.outlinedButtonBorder,
                                modifier = Modifier.clickable {
                                    onVariantSelected(variant)
                                }
                            ) {
                                Text(
                                    text = variant.shadeName,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(parseColor(variant.color))
                                    .border(
                                        width = if (variant.id == product.id) 3.dp else 1.dp,
                                        color = if (variant.id == product.id) {
                                            MaterialTheme.colorScheme.onBackground
                                        } else {
                                            Color.LightGray
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        onVariantSelected(variant)
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Detalii produs",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "%.2f lei".format(product.price),
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFB85C7A)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (product.stock > 0) {
                    "Stoc disponibil: ${product.stock}"
                } else {
                    "Indisponibil"
                },
                color = if (product.stock > 0) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    Color(0xFFB00020)
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    onAddToCart(product)
                    onShowMessage("Produs adăugat în coș")
                },
                enabled = product.stock > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (product.stock > 0) "Adaugă în coș" else "Indisponibil")
            }

            if (isAdmin) {
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        onEditProduct(product)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Editează produs")
                }
            }
        }
    }
}