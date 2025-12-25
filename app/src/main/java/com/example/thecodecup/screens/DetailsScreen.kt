package com.example.thecodecup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import com.example.thecodecup.ui.utils.getCoffeeImageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.Screen
import com.example.thecodecup.model.CartItem
import com.example.thecodecup.model.DataManager
import com.example.thecodecup.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavController, coffeeId: String?) {
    // Lấy thông tin món từ ID
    val coffee = DataManager.menu.find { it.id == coffeeId } ?: DataManager.menu.first()

    // State cho các lựa chọn (Size, Ice, Shot, Quantity)
    var quantity by remember { mutableIntStateOf(1) }
    var selectedSize by remember { mutableStateOf("M") }
    var selectedShot by remember { mutableStateOf("Single") }
    var selectedIce by remember { mutableStateOf("Full") }

    // Tính tổng tiền Dynamic
    val sizePrice = if (selectedSize == "L") 1.0 else 0.0
    val shotPrice = if (selectedShot == "Double") 0.5 else 0.0
    val totalPrice = (coffee.basePrice + sizePrice + shotPrice) * quantity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Back Navigation
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Cart.route) }) { // Cart Preview
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Ảnh Cafe
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = getCoffeeImageResource(coffee)),
                        contentDescription = coffee.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tên món và Số lượng
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(coffee.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) { Text("-", fontSize = 20.sp) }
                        Text("$quantity", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { quantity++ }) { Text("+", fontSize = 20.sp) }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Các tùy chọn (Shot, Size, Ice)
                OptionSection("Shot", listOf("Single", "Double"), selectedShot) { selectedShot = it }
                Spacer(modifier = Modifier.height(16.dp))
                OptionSection("Size", listOf("S", "M", "L"), selectedSize) { selectedSize = it }
                Spacer(modifier = Modifier.height(16.dp))
                OptionSection("Ice", listOf("No", "Less", "Full"), selectedIce) { selectedIce = it }
            }

            // Footer: Tổng tiền và nút Add to Cart
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Amount", fontSize = 18.sp, color = TextGray)
                    Text("$${String.format("%.2f", totalPrice)}", fontSize = 20.sp, fontWeight = FontWeight.Bold) // Dynamic Price
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Logic Add to Cart
                        DataManager.addToCart(
                            CartItem(coffee, selectedSize, selectedIce, selectedShot, quantity, totalPrice)
                        )
                        navController.navigate(Screen.Cart.route)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue)
                ) {
                    Text("Add to cart", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

// Component con để vẽ các nút chọn (Chip)
@Composable
fun OptionSection(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(title, modifier = Modifier.width(60.dp), fontWeight = FontWeight.Medium, color = TextGray)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { option ->
                val isSelected = option == selected
                Box(
                    modifier = Modifier
                        .border(1.dp, if (isSelected) CoffeeBrown else Color.LightGray, RoundedCornerShape(20.dp))
                        .background(if (isSelected) CoffeeBrown.copy(alpha = 0.1f) else Color.Transparent, RoundedCornerShape(20.dp))
                        .clickable { onSelect(option) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(option, color = if (isSelected) CoffeeBrown else TextGray)
                }
            }
        }
    }
}