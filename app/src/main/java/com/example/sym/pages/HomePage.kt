package com.example.sym.pages

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sym.others.CardList
import com.example.sym.vms.AuthState
import com.example.sym.vms.AuthViewModel
import com.example.sym.vms.ProductViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    navController: NavController,
    productViewModel: ProductViewModel
) {
    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val productList = productViewModel.products.observeAsState()
    var showProfile by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) } // State to control exit dialog visibility
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Handle back button press to show exit dialog
    BackHandler {
        showExitDialog = true
    }

    LaunchedEffect(key1 = authState) {
        when(authState){
            is AuthState.UnAuthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    // Lighter maroon color scheme
    val customColorScheme = lightColorScheme(
        primary = Color(0xFFC41E3A),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFE5E8),
        onPrimaryContainer = Color(0xFF8B0000),
        secondary = Color(0xFFD4545C),
        onSecondary = Color(0xFFFFFFFF),
        surface = Color(0xFFFCFCFC),
        background = Color(0xFFF8F8F8),
        error = Color(0xFFB00020)
    )

    if (showProfile) {
        BasicAlertDialog(
            onDismissRequest = { showProfile = false },
            properties = DialogProperties(dismissOnClickOutside = true),
            content = {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        // Title with close button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Profile",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            IconButton(onClick = { showProfile = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Profile Details
                        Text(text = "User Name: Shreekant", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Email: ${currentUser?.email}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        )
    }

    // Exit confirmation dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Exit App") },
            text = { Text("Are you sure you want to exit?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        navController.popBackStack() // Exit the app or go back
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    MaterialTheme(colorScheme = customColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Store,
                                contentDescription = "Store Icon",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Kirana",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        IconButton(onClick = { navController.navigate("allTransactions") }) {
                            Icon(Icons.Filled.Receipt, "Transactions")
                        }
                        IconButton(onClick = { showProfile = true }) {
                            Icon(Icons.Filled.Person, "Profile")
                        }
                        IconButton(onClick = { authViewModel.signout() }) {
                            Icon(Icons.Filled.ExitToApp, "Sign Out")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    tonalElevation = 4.dp
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, "Home") },
                        label = { Text("Home") },
                        selected = false,
                        onClick = { /* Do nothing for home button */ },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.ShoppingCart, "Cart") },
                        label = { Text("Cart") },
                        selected = false,
                        onClick = { navController.navigate("showcart") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Collections, "Collection") },
                        label = { Text("Collection") },
                        selected = false,
                        onClick = { navController.navigate("todayCollection") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (productList.value == null) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Loading products...",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    CardList(productList, productViewModel)
                }
            }
        }
    }
}
