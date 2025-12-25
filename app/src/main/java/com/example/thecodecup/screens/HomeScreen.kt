package com.example.thecodecup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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
    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = { BottomNavBar(navController, currentRoute = Screen.Home.route) } //  Bottom Navigation Bar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            // 1. Header Component
            HeaderSection(navController)

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Loyalty Card View
            LoyaltyCardSection(navController)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Choose your coffee", color = TextSecondary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // 3. Coffee List View
            CoffeeGridSection(navController)
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
            Text("Good morning", color = TextSecondary, fontSize = 14.sp)
            Text(profile.fullName, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Row {
            IconButton(onClick = { navController.navigate(Screen.Cart.route) }) { 
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = TextPrimary) 
            }
            IconButton(onClick = { navController.navigate(Screen.Profile.route) }) { 
                Icon(Icons.Default.Person, contentDescription = null, tint = TextPrimary) 
            }
        }
    }
}

@Composable
fun LoyaltyCardSection(navController: NavController) {
    val loyaltyStamps by DataManager.loyaltyStamps
    
    Card(
        colors = CardDefaults.cardColors(containerColor = LightCards),
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
                Text("Loyalty card", color = Color.White, fontSize = 14.sp)
                Text("$loyaltyStamps / 8", color = Color.White, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(12.dp),
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
    val coffees = listOf("Americano", "Cappuccino", "Mocha", "Flat White")

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(coffees.size) { index ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightCards),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.aspectRatio(1f),
                onClick = {
                    navController.navigate(Screen.Details.createRoute("${index + 1}"))
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = getCoffeeImageResourceByName(coffees[index])),
                            contentDescription = coffees[index],
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        coffees[index], 
                        color = Color.White, 
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String? = null) {
    NavigationBar(containerColor = SurfaceLight) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = SurfaceLight, 
                selectedIconColor = CoffeeBrown, 
                unselectedIconColor = TextSecondary
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CardGiftcard, null) }, // Rewards icon
            selected = currentRoute == Screen.Rewards.route,
            onClick = {
                navController.navigate(Screen.Rewards.route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = SurfaceLight,
                selectedIconColor = CoffeeBrown,
                unselectedIconColor = TextSecondary
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Receipt, null) }, // Orders icon
            selected = currentRoute == Screen.MyOrders.route,
            onClick = {
                navController.navigate(Screen.MyOrders.route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = SurfaceLight,
                selectedIconColor = CoffeeBrown,
                unselectedIconColor = TextSecondary
            )
        )
    }
}