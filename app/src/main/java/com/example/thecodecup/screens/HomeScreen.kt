package com.example.thecodecup.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.thecodecup.R
import androidx.compose.runtime.Composable
import com.example.thecodecup.ui.utils.getCoffeeImageResourceByName
import androidx.compose.runtime.getValue
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
import com.example.thecodecup.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
    ) {
        // Main scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 120.dp) // Extra bottom padding for nav bar overlap
        ) {
            // 1. Header Component
            HeaderSection(navController)

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Loyalty Card View
            LoyaltyCardSection(navController)

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Coffee Grid Section with Border Container
            CoffeeGridSection(navController)
        }
        
        // Bottom Navigation Bar (overlaying)
        BottomNavBar(
            navController = navController,
            currentRoute = Screen.Home.route,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun HeaderSection(navController: NavController) {
    val profile by DataManager.userProfile
    val isDarkMode by DataManager.isDarkMode
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Good morning", color = TextSecondaryGray, fontSize = 14.sp)
            Text(profile.fullName, color = TextPrimaryDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Dark mode toggle button
            IconButton(onClick = { DataManager.toggleDarkMode() }) {
                Image(
                    painter = painterResource(id = R.drawable.moon_icon),
                    contentDescription = "Toggle Dark Mode",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(IconActive)
                )
            }
            // Shopping cart button
            IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                Image(
                    painter = painterResource(id = R.drawable.shopping_card_icon),
                    contentDescription = "Cart",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(IconActive)
                )
            }
            // Profile button
            IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                Image(
                    painter = painterResource(id = R.drawable.profile_icon),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(IconActive)
                )
            }
        }
    }
}

@Composable
fun LoyaltyCardSection(navController: NavController) {
    val loyaltyStamps by DataManager.loyaltyStamps
    
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDarkBlue),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(enabled = loyaltyStamps >= 8) {
                if (loyaltyStamps >= 8) {
                    DataManager.resetLoyaltyStamps()
                }
            }
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.Center) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Loyalty card", color = TextOnDarkSurface, fontSize = 14.sp)
                Text("$loyaltyStamps / 8", color = TextOnDarkSurface, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().background(CardWhite, RoundedCornerShape(12.dp)).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(8) { index ->
                    val isActive = index < loyaltyStamps
                    Image(
                        painter = painterResource(
                            id = if (isActive) R.drawable.loyalty_coffee_cup_active else R.drawable.loyalty_coffee_cup_deactive
                        ),
                        contentDescription = if (isActive) "Active stamp ${index + 1}" else "Inactive stamp ${index + 1}",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CoffeeGridSection(navController: NavController) {
    // Get all coffee items from DataManager
    val coffees = DataManager.menu

    // Dark blue container (like loyalty card)
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDarkBlue),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Title - WHITE text on dark background
            Text(
                "Choose your coffee",
                color = TextOnDarkSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scrollable grid - using LazyVerticalGrid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp) // Limit height to enable scrolling
            ) {
                items(coffees.size) { index ->
                    CoffeeCard(
                        coffeeName = coffees[index].name,
                        coffeeId = coffees[index].id,
                        onClick = {
                            navController.navigate(Screen.Details.createRoute(coffees[index].id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CoffeeCard(
    coffeeName: String,
    coffeeId: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite), // WHITE
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundPrimary), // Light gray for image background
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getCoffeeImageResourceByName(coffeeName)),
                    contentDescription = coffeeName,
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                coffeeName,
                color = TextPrimaryDark,  // Dark text on white card
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CardWhite,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
        // Home (Store icon)
        NavigationBarItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.store_icon),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(
                        if (currentRoute == Screen.Home.route) IconActive else IconInactive
                    )
                )
            },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = BackgroundSecondary,
                selectedIconColor = IconActive,
                unselectedIconColor = IconInactive
            )
        )
        // Rewards (Gift icon)
        NavigationBarItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.gift_icon),
                    contentDescription = "Rewards",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(
                        if (currentRoute == Screen.Rewards.route) IconActive else IconInactive
                    )
                )
            },
            selected = currentRoute == Screen.Rewards.route,
            onClick = {
                navController.navigate(Screen.Rewards.route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = BackgroundSecondary,
                selectedIconColor = IconActive,
                unselectedIconColor = IconInactive
            )
        )
        // My Orders (Bill icon)
        NavigationBarItem(
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.bill_icon),
                    contentDescription = "My Orders",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(
                        if (currentRoute == Screen.MyOrders.route) IconActive else IconInactive
                    )
                )
            },
            selected = currentRoute == Screen.MyOrders.route,
            onClick = {
                navController.navigate(Screen.MyOrders.route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedIconColor = IconActive,
                unselectedIconColor = IconInactive
            )
        )
    }
    }
}