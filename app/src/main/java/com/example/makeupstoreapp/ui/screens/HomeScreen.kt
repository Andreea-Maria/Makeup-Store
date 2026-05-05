package com.example.makeupstoreapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.makeupstoreapp.data.model.Product
import com.example.makeupstoreapp.viewmodel.CartViewModel
import com.example.makeupstoreapp.viewmodel.ProductViewModel
import com.example.makeupstoreapp.viewmodel.FavoritesViewModel
import com.example.makeupstoreapp.viewmodel.ThemeViewModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.Star
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarDuration

val primaryColor = Color(0xFFFFC1CC)
val backgroundColor = Color(0xFFFFF5F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showFavorites by remember { mutableStateOf(false) }
    var showOrders by remember { mutableStateOf(false) }
    var showCheckout by remember { mutableStateOf(false) }
    var showOrderConfirmation by remember { mutableStateOf(false) }
    var showAddProduct by remember { mutableStateOf(false) }
    var isAdmin by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }

    val productViewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.quantity }
    val products by productViewModel.products.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()


    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid

        userId?.let {
            db.collection("users")
                .document(it)
                .get()
                .addOnSuccessListener { doc ->
                    isAdmin = doc.getString("role") == "admin"
                }
        }
    }

    LaunchedEffect(products) {
        if (products.isNotEmpty()) {
            favoritesViewModel.loadFavorites(products)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text("Makeup Store") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout", color = primaryColor)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedProduct = null
                        selectedTab = 0
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedProduct = null
                        selectedTab = 1
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge {
                                        Text(cartCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Coș")
                        }
                    },
                    label = { Text("Coș") }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedProduct = null
                        selectedTab = 2
                    },
                    icon = { Icon(Icons.Default.CameraAlt, contentDescription = "QR") },
                    label = { Text("QR") }
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedProduct = null
                        selectedTab = 3
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") }
                )

                if (isAdmin) {
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = {
                            selectedProduct = null
                            selectedTab = 4
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Admin") },
                        label = { Text("Admin") }
                    )
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> {
                    if (productToEdit != null) {
                        EditProductScreen(
                            product = productToEdit!!,
                            onBack = { productToEdit = null },
                            onProductUpdated = {
                                productToEdit = null
                                selectedProduct = null
                            }
                        )
                    } else if (selectedProduct == null) {
                        HomeContent(
                            productViewModel = productViewModel,
                            favoritesViewModel = favoritesViewModel,
                            onProductClick = { product ->
                                selectedProduct = product
                            },
                            onShowMessage = { message ->
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    } else {
                        ProductDetailsScreen(
                            product = selectedProduct!!,
                            variants = products.filter {
                                it.productGroupId == selectedProduct!!.productGroupId
                            },
                            favoritesViewModel = favoritesViewModel,
                            onVariantSelected = { product ->
                                selectedProduct = product
                            },
                            onBack = {
                                selectedProduct = null
                            },
                            onAddToCart = { product ->
                                cartViewModel.addToCart(product)
                                selectedProduct = null
                            },
                            isAdmin = isAdmin,
                            onEditProduct = { product ->
                                productToEdit = product
                            },
                            onShowMessage = { message ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }
                }

                1 -> {
                    when {
                        showOrderConfirmation -> {
                            OrderConfirmationScreen(
                                onGoToOrders = {
                                    showOrderConfirmation = false
                                    showOrders = true
                                    selectedTab = 3
                                },
                                onGoToHome = {
                                    showOrderConfirmation = false
                                    selectedTab = 0
                                }
                            )
                        }

                        showCheckout -> {
                            CheckoutScreen(
                                cartViewModel = cartViewModel,
                                onBack = { showCheckout = false },
                                onOrderPlaced = {
                                    showCheckout = false
                                    showOrderConfirmation = true
                                }
                            )
                        }

                        else -> {
                            CartScreen(
                                cartViewModel = cartViewModel,
                                onCheckoutClick = {
                                    showCheckout = true
                                }
                            )
                        }
                    }
                }
                2 -> QRScreen(cartViewModel)
                3 -> {
                    when {
                        showFavorites -> FavoritesScreen(
                            favoritesViewModel = favoritesViewModel,
                            onProductClick = { product ->
                                selectedProduct = product
                                showFavorites = false
                                selectedTab = 0
                            },
                            onBack = { showFavorites = false }
                        )

                        showOrders -> OrdersHistoryScreen(
                            onBack = { showOrders = false }
                        )

                        else -> ProfileScreen(
                            themeViewModel = themeViewModel,
                            onFavoritesClick = { showFavorites = true },
                            onOrdersClick = { showOrders = true }
                        )
                    }
                }
                4 -> {
                    if (showAddProduct) {
                        AddProductScreen(
                            onBack = { showAddProduct = false },
                            onProductSaved = { showAddProduct = false }
                        )
                    } else {
                        AdminScreen(
                            onAddProductClick = { showAddProduct = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    productViewModel: ProductViewModel,
    favoritesViewModel: FavoritesViewModel,
    onProductClick: (Product) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Toate") }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }

    val filteredProducts = products.filter { product ->
        val matchesSearch =
            product.name.contains(searchText, ignoreCase = true) ||
                    product.brand.contains(searchText, ignoreCase = true) ||
                    product.category.contains(searchText, ignoreCase = true) ||
                    product.subcategory.contains(searchText, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Toate" -> true
            "Oferte speciale" -> product.isOffer
            "Cele mai populare" -> product.isPopular
            else -> product.category == selectedFilter || product.subcategory == selectedFilter || product.brand == selectedFilter
        }

        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.Menu, contentDescription = "Categorii")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Toate") },
                        onClick = {
                            selectedFilter = "Toate"
                            showMenu = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Oferte speciale") },
                        onClick = {
                            selectedFilter = "Oferte speciale"
                            showMenu = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Cele mai populare") },
                        onClick = {
                            selectedFilter = "Cele mai populare"
                            showMenu = false
                        }
                    )

                    Text(
                        "Branduri",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold
                    )

                    val brands = products.map { it.brand }.filter { it.isNotBlank() }.distinct()
                    brands.forEach { brand ->
                        DropdownMenuItem(
                            text = { Text("   $brand") },
                            onClick = {
                                selectedFilter = brand
                                showMenu = false
                            }
                        )
                    }

                    CategoryMenuGroup("Față", listOf("Fond de ten", "Pudră", "Concealer", "Contouring", "Iluminatoare", "Skincare")) {
                        selectedFilter = it
                        showMenu = false
                    }

                    CategoryMenuGroup("Ochi", listOf("Mascara", "Eyeliner", "Fard", "Gene")) {
                        selectedFilter = it
                        showMenu = false
                    }

                    CategoryMenuGroup("Buze", listOf("Ruj mat", "Ruj lucios", "Gloss", "Creion de buze")) {
                        selectedFilter = it
                        showMenu = false
                    }

                    CategoryMenuGroup("Păr", listOf("Șampon", "Mască / balsam", "Leave-in", "Accesorii", "Produse de îngrijire")) {
                        selectedFilter = it
                        showMenu = false
                    }

                    DropdownMenuItem(
                        text = { Text("Parfumuri") },
                        onClick = {
                            selectedFilter = "Parfumuri"
                            showMenu = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Caută produse...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Filtru: $selectedFilter",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            filteredProducts.isEmpty() -> {
                Text("Nu am găsit produse.")
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            favoritesViewModel = favoritesViewModel,
                            onShowMessage = onShowMessage
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryMenuGroup(
    title: String,
    subcategories: List<String>,
    onSelect: (String) -> Unit
) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        fontWeight = FontWeight.Bold
    )

    subcategories.forEach { subcategory ->
        DropdownMenuItem(
            text = { Text("   $subcategory") },
            onClick = { onSelect(subcategory) }
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    favoritesViewModel: FavoritesViewModel,
    onShowMessage: (String) -> Unit
) {
    val favoriteProducts by favoritesViewModel.favoriteProducts.collectAsState()
    val isFavorite = favoriteProducts.any { it.id == product.id }

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        color = parseColor(product.color).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )

                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
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

                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp)
                        .background(
                            color = parseColor(product.color),
                            shape = CircleShape
                        )
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )

            Text(
                text = product.category,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(4.dp))

            RatingBar(rating = product.rating)

            Spacer(Modifier.height(6.dp))

            Text(
                text = "%.2f lei".format(product.price),
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFFB85C7A)
            )
        }
    }
}

@Composable
fun RatingBar(
    rating: Double,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            val icon = when {
                rating >= i -> Icons.Filled.Star
                rating >= i - 0.5 -> Icons.Filled.StarHalf
                else -> Icons.Outlined.Star
            }

            Icon(
                imageVector = icon,
                contentDescription = "Rating",
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(Modifier.width(4.dp))

        Text(
            text = "%.1f".format(rating),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = Color.Black
    )

    Spacer(Modifier.height(10.dp))
}

@Composable
fun HorizontalProducts(
    products: List<Product>,
    favoritesViewModel: FavoritesViewModel,
    onClick: (Product) -> Unit,
    onShowMessage: (String) -> Unit
) {
    if (products.isEmpty()) {
        Text(
            text = "Nu există produse în această secțiune.",
            color = Color.Gray
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                Box(modifier = Modifier.width(165.dp)) {
                    ProductCard(
                        product = product,
                        onClick = { onClick(product) },
                        favoritesViewModel = favoritesViewModel,
                        onShowMessage = onShowMessage
                    )
                }
            }
        }
    }
}

@Composable
fun BrandRow(
    products: List<Product>,
    onBrandClick: (String) -> Unit
) {
    val brands = products
        .map { it.brand }
        .filter { it.isNotBlank() }
        .distinct()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(brands) { brand ->
            AssistChip(
                onClick = { onBrandClick(brand) },
                label = { Text(brand) }
            )
        }
    }
}

fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.LightGray
    }
}