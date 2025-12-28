package com.example.thecodecup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.thecodecup.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.Screen
import com.example.thecodecup.model.DataManager
import com.example.thecodecup.ui.components.BottomNavBar
import com.example.thecodecup.ui.components.CoffeeCard
import com.example.thecodecup.ui.components.LoyaltyCard
import com.example.thecodecup.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavBar(navController, currentRoute = Screen.Home.route) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 20.dp)
        ) {
            // 1. Header Component (fixed)
            HeaderSection(navController)

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Loyalty Card View (fixed)
            val loyaltyStamps by DataManager.loyaltyStamps
            LoyaltyCard(
                stamps = loyaltyStamps,
                onResetClick = { if (loyaltyStamps >= 8) DataManager.resetLoyaltyStamps() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Coffee Grid Section (container fixed, ONLY grid scrolls)
            var isSearchVisible by remember { mutableStateOf(false) }
            var searchQuery by remember { mutableStateOf("") }
            CoffeeGridSection(
                navController = navController,
                isSearchVisible = isSearchVisible,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearchClick = { 
                    isSearchVisible = !isSearchVisible
                    if (!isSearchVisible) {
                        searchQuery = "" // Clear search when closing
                    }
                },
                onSearchVisibilityChange = { 
                    isSearchVisible = it
                    if (!it) {
                        searchQuery = "" // Clear search when closing
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
fun HeaderSection(navController: NavController) {
    val profile by DataManager.userProfile
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Good morning", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Text(
                profile.fullName,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Shopping cart button
            IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                Image(
                    painter = painterResource(id = R.drawable.shopping_card_icon),
                    contentDescription = "Cart",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
            // Profile button
            IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                Image(
                    painter = painterResource(id = R.drawable.profile_icon),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun CoffeeGridSection(
    navController: NavController,
    isSearchVisible: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSearchVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Get all coffee items from DataManager
    val allCoffees = DataManager.menu
    
    // Filter coffees based on search query
    val coffees = remember(allCoffees, searchQuery) {
        if (searchQuery.isBlank()) {
            allCoffees
        } else {
            allCoffees.filter { coffee ->
                coffee.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Dark blue container (like loyalty card)
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // slightly smaller padding like teacher design
        ) {
            // Title and Search Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Choose your coffee",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                // Search icon button
                IconButton(onClick = onSearchClick) {
                    Image(
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = "Search",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
                    )
                }
            }
            
            // Search bar (shown when isSearchVisible is true)
            if (isSearchVisible) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search coffee...", color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                onSearchQueryChange("")
                            }) {
                                Text(
                                    "✕",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            // ONLY this grid scrolls
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(coffees) { coffee ->
                    CoffeeCard(
                        coffeeName = coffee.name,
                        onClick = {
                            navController.navigate(Screen.Details.createRoute(coffee.id))
                        }
                    )
                }
            }
        }
    }
}