package com.example.thecodecup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.Screen
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
            HeaderSection()

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Loyalty Card View
            LoyaltyCardSection()

            Spacer(modifier = Modifier.height(24.dp))

            Text("Choose your coffee", color = TextGray, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // 3. Coffee List View
            CoffeeGridSection(navController)
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Good morning", color = TextGray, fontSize = 14.sp)
            Text("Anderson", color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Row {
            IconButton(onClick = {}) { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = TextWhite) }
            IconButton(onClick = {}) { Icon(Icons.Default.Person, contentDescription = null, tint = TextWhite) }
        }
    }
}

@Composable
fun LoyaltyCardSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightCards),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(100.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Loyalty card", color = TextGray)
                Text("4 / 8", color = TextGray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(8) { index ->
                    Icon(
                        imageVector = Icons.Default.LocalCafe,
                        contentDescription = null,
                        // 4 ly đầu màu nâu, 4 ly sau màu xám
                        tint = if (index < 4) CoffeeBrown else Color.LightGray,
                        modifier = Modifier.size(20.dp)
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(coffees.size) { index ->
            Card(
                colors = CardDefaults.cardColors(containerColor = LightCards),
                shape = RoundedCornerShape(16.dp),
                //  Navigation Intent: On-click listener
                onClick = {
                    // Giả sử id lấy theo index (sau này bạn map ID thật vào)
                    navController.navigate(Screen.Details.createRoute("${index + 1}"))
                }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        // Placeholder Image
                        Icon(Icons.Default.Coffee, contentDescription = null, tint = CoffeeBrown, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(coffees[index], color = TextWhite, fontWeight = FontWeight.Medium)
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
            colors = NavigationBarItemDefaults.colors(indicatorColor = SurfaceLight, selectedIconColor = CoffeeBrown, unselectedIconColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CardGiftcard, null) }, // Rewards icon placeholder
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray)
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
                unselectedIconColor = TextGray
            )
        )
    }
}