package com.example.makeupstoreapp.ui.screens

import android.widget.Button
import android.widget.CheckBox
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDiscountScreen(
    onBack: () -> Unit,
    onDiscountSaved: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    var code by remember { mutableStateOf("") }
    var percent by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf("") }
    var attemptedSave by remember { mutableStateOf(false) }

    val percentNumber = percent.toIntOrNull()
    val codeError = attemptedSave && code.isBlank()
    val percentError = attemptedSave && (percentNumber == null || percentNumber !in 1..90)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adaugă cod reducere") },
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
                value = code,
                onValueChange = { code = it.uppercase().replace(" ","") },
                label = { Text("Cod reducere") },
                placeholder = { Text("Ex: MAKEUP20") },
                isError = codeError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (codeError) {
                Text(
                    text = "Codul este obligatoriu.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = percent,
                onValueChange = { percent = it.filter { c -> c.isDigit() }.take(2) },
                placeholder = { Text("Ex: 20") },
                isError = percentError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (percentError) {
                Text(
                    text = "Procentul trebuie să fie între 1 și 90.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(12.dp))

            Row {
                Checkbox(
                    checked = active,
                    onCheckedChange = { active = it }
                )

                Text(
                    text = "Cod activ",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    attemptedSave = true
                    message = ""

                    val percentValue = percent.toIntOrNull()

                    if (code.isBlank() || percentValue == null || percentValue !in 1..90) {
                        message = "Completează corect codul și procentul."
                        return@Button
                    }

                    val discount = mapOf(
                        "code" to code,
                        "percent" to percentValue,
                        "active" to active
                    )

                    db.collection("discountCodes")
                        .document(code)
                        .set(discount)
                        .addOnSuccessListener {
                            onDiscountSaved()
                        }
                        .addOnFailureListener {
                            message = "Codul de reducere nu a putut fi salvat."
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Salvează cod reducere")
            }

            if (message.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}