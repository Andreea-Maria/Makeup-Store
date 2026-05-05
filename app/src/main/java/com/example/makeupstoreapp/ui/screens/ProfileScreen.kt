package com.example.makeupstoreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.makeupstoreapp.viewmodel.ThemeViewModel
import androidx.compose.material.icons.filled.DarkMode

@Composable
fun ProfileScreen(
    themeViewModel: ThemeViewModel,
    onFavoritesClick: () -> Unit,
    onOrdersClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    var name by remember { mutableStateOf("Utilizator") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    name = document.getString("name") ?: "Utilizator"
                    email = document.getString("email") ?: currentUser.email.orEmpty()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .background(primaryColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profil",
                modifier = Modifier.size(48.dp),
                tint = Color.Black
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Email,
                contentDescription = "Email",
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )

            Spacer(Modifier.width(6.dp))

            Text(email,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        Spacer(Modifier.height(30.dp))

        ProfileOptionCard(
            icon = Icons.Default.Favorite,
            title = "Produse favorite",
            subtitle = "Vezi produsele salvate",
            onClick = onFavoritesClick
        )

        ProfileOptionCard(
            icon = Icons.Default.History,
            title = "Istoric comenzi",
            subtitle = "Vezi comenzile plasate",
            onClick = onOrdersClick
        )

        ProfileOptionCard(
            icon = Icons.Default.DarkMode,
            title = "Dark mode",
            subtitle = if (isDarkMode) "Activat" else "Dezactivat",
            onClick = {
                themeViewModel.toggleDarkMode()
            }
        )
    }
}

@Composable
fun ProfileOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFFB85C7A),
                modifier = Modifier.size(30.dp)
            )

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Deschide",
                tint = Color.Gray
            )
        }
    }
}