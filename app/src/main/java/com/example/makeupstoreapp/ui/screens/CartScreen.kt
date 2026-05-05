package com.example.makeupstoreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.makeupstoreapp.viewmodel.CartViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onCheckoutClick: () -> Unit
) {

    var message by remember { mutableStateOf("") }

    val cartItems by cartViewModel.cartItems.collectAsState()
    val total = cartViewModel.getTotalPrice()
    val discountPercent by cartViewModel.discountPercent.collectAsState()
    val discountedTotal = cartViewModel.getDiscountedTotal()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Coșul meu", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Coșul tău este gol momentan")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(cartItems) { item ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        parseColor(item.product.color).copy(alpha = 0.25f),
                                        RoundedCornerShape(14.dp)
                                    )
                            )

                            Spacer(Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    item.product.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    "${item.product.price} lei",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                Text(
                                    "Cantitate: ${item.quantity}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        cartViewModel.decreaseQuantity(item.product)
                                    }
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Scade")
                                }

                                Text("${item.quantity}")

                                IconButton(
                                    onClick = {
                                        if (item.quantity < item.product.stock) {
                                            cartViewModel.addToCart(item.product)
                                        } else {
                                            message = "Ai atins limita stocului"
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Crește",
                                        tint = if (item.quantity < item.product.stock) {
                                            MaterialTheme.colorScheme.onSurface
                                        } else {
                                            Color.Gray
                                        }
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        cartViewModel.removeFromCart(item.product)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Șterge",
                                        tint = Color(0xFFB85C7A)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (discountPercent > 0) {
                Text("Reducere aplicată: $discountPercent%")
                Text("Total inițial: %.2f lei".format(total))
                Text(
                    text = "Total final: %.2f lei".format(discountedTotal),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFB85C7A)
                )
            } else {
                Text(
                    text = "Total: %.2f lei".format(total),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFB85C7A)
                )
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onCheckoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.Black
                )
            ) {
                Text("Finalizează comanda")
            }

            if (message.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}