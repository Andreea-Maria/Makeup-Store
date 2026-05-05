package com.example.makeupstoreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.makeupstoreapp.viewmodel.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Ramburs") }
    var message by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf(TextFieldValue("")) }
    var cvv by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val total = cartViewModel.getDiscountedTotal()
    val cartItems by cartViewModel.cartItems.collectAsState()

    LaunchedEffect(userId) {
        userId?.let {
            db.collection("users")
                .document(it)
                .get()
                .addOnSuccessListener { doc ->
                    name = doc.getString("name") ?: ""
                    phone = doc.getString("phone") ?: ""
                    address = doc.getString("address") ?: ""
                    city = doc.getString("city") ?: ""
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizare comandă") },
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
                label = { Text("Nume complet") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Telefon") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Adresă livrare") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Oraș") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            Text("Metodă de plată", style = MaterialTheme.typography.titleMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = paymentMethod == "Ramburs",
                    onClick = { paymentMethod = "Ramburs" }
                )
                Text("Ramburs")

                Spacer(Modifier.width(20.dp))

                RadioButton(
                    selected = paymentMethod == "Card",
                    onClick = { paymentMethod = "Card" }
                )
                Text("Card")
            }

            if (paymentMethod == "Card") {
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = {
                        val digits = it.filter { c -> c.isDigit() }.take(16)
                        cardNumber = digits.chunked(4).joinToString(" ")
                    },
                    label = { Text("Număr card") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = cardHolder,
                    onValueChange = { cardHolder = it },
                    label = { Text("Nume titular card") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { newValue ->
                            val digits = newValue.text.filter { it.isDigit() }.take(4)

                            val formatted = when (digits.length) {
                                0 -> ""
                                1 -> digits
                                2 -> digits
                                3 -> digits.substring(0, 2) + "/" + digits.substring(2)
                                else -> digits.substring(0, 2) + "/" + digits.substring(2, 4)
                            }

                            expiryDate = TextFieldValue(
                                text = formatted,
                                selection = TextRange(formatted.length)
                            )
                        },
                        label = { Text("MM/YY") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it.filter { c -> c.isDigit() }.take(3) },
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Total de plată: %.2f lei".format(total),
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFB85C7A)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    message = ""
                    if (name.isBlank() || phone.isBlank() || address.isBlank() || city.isBlank()) {
                        message = "Completează toate câmpurile."
                    } else if (
                        paymentMethod == "Card" &&
                        (cardNumber.length < 16 || cardHolder.isBlank() || expiryDate.text.isBlank() || cvv.length < 3)
                    ) {
                        message = "Completează corect datele cardului."
                    } else if (
                        paymentMethod == "Card" &&
                        isCardExpired(expiryDate.text)
                    ) {
                        message = "Cardul este expirat."
                    } else {
                        val auth = FirebaseAuth.getInstance()
                        val db = FirebaseFirestore.getInstance()

                        val userId = auth.currentUser?.uid ?: return@Button

                        val orderData = hashMapOf(
                            "userId" to userId,
                            "name" to name,
                            "phone" to phone,
                            "address" to address,
                            "city" to city,
                            "paymentStatus" to if (paymentMethod == "Card") "Plată simulată" else "Ramburs",
                            "total" to total,
                            "timestamp" to System.currentTimeMillis()
                        )

                        val userData: Map<String, Any> = mapOf(
                            "address" to address,
                            "city" to city,
                            "phone" to phone,
                            "name" to name
                        )

                        db.collection("orders")
                            .add(orderData)
                            .addOnSuccessListener {

                                cartItems.forEach { item ->
                                    val productRef = db.collection("products").document(item.product.id)

                                    db.runTransaction { transaction ->
                                        val snapshot = transaction.get(productRef)
                                        val currentStock = snapshot.getLong("stock") ?: 0
                                        val newStock = (currentStock - item.quantity).coerceAtLeast(0)

                                        transaction.update(productRef, "stock", newStock)
                                    }
                                }

                                db.collection("users")
                                    .document(userId)
                                    .update(userData)

                                cartViewModel.clearCart()
                                onOrderPlaced()
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.Black
                )
            ) {
                Text("Plasează comanda")
            }

            if (message.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(message, color = Color(0xFFB00020))
            }
        }
    }
}

fun isCardExpired(expiryDate: String): Boolean {
    if (!expiryDate.matches(Regex("\\d{2}/\\d{2}"))) return true

    val parts = expiryDate.split("/")
    val month = parts[0].toIntOrNull() ?: return true
    val year = parts[1].toIntOrNull() ?: return true

    if (month !in 1..12) return true

    val calendar = java.util.Calendar.getInstance()
    val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
    val currentYear = calendar.get(java.util.Calendar.YEAR) % 100

    return year < currentYear || (year == currentYear && month < currentMonth)
}