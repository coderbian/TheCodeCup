package com.example.thecodecup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.Screen
import com.example.thecodecup.model.DataManager
import com.example.thecodecup.model.Order
import com.example.thecodecup.model.OrderStatus
import com.example.thecodecup.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val cartItems = DataManager.cart
    val totalAmount = DataManager.getCartTotal() // Total Price Display

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            // Danh sách món (Cart Item Rendering )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = cartItems, key = { it.hashCode() }) { item ->
                    SwipeToDeleteItem(item = item, onDelete = { DataManager.removeFromCart(item) })
                }
            }

            // Footer thanh toán
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Price", fontSize = 18.sp, color = TextGray)
                    Text("$${String.format("%.2f", totalAmount)}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (cartItems.isNotEmpty()) {
                            // Create order from cart items
                            val order = Order(
                                id = UUID.randomUUID().toString(),
                                dateTime = DataManager.formatOrderDateTime(),
                                items = cartItems.toList(),
                                totalPrice = totalAmount,
                                status = OrderStatus.ONGOING
                            )
                            // Add order to DataManager
                            DataManager.addOrder(order)
                            // Clear cart after checkout
                            DataManager.clearCart()
                            // Navigate to Order Success screen
                            navController.navigate(Screen.OrderSuccess.route) {
                                // Clear back stack to prevent going back to cart
                                popUpTo(Screen.Cart.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                    enabled = cartItems.isNotEmpty()
                ) {
                    Text("Checkout", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

// Xử lý Vuốt để xóa (Gesture-Based Item Removal )
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteItem(item: com.example.thecodecup.model.CartItem, onDelete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }
        }
    ) {
        // UI của 1 item trong giỏ hàng
        Card(
            colors = CardDefaults.cardColors(containerColor = LightCards),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                // Ảnh nhỏ
                Box(
                    modifier = Modifier.size(60.dp).background(Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Coffee, contentDescription = null, tint = CoffeeBrown)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.coffee.name, color = TextWhite, fontWeight = FontWeight.Bold)
                    Text(
                        "${item.shot} | ${item.size} | Ice: ${item.ice}",
                        color = TextGray, fontSize = 12.sp
                    )
                    Text("x${item.quantity}", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                Text("$${String.format("%.2f", item.totalPrice)}", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}