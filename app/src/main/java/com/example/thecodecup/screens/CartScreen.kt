package com.example.thecodecup.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.thecodecup.ui.utils.getCoffeeImageResource
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Delete
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
import com.example.thecodecup.model.CartItem
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
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("My Cart", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // Danh sách món (Cart Item Rendering)
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                items(items = cartItems, key = { it.hashCode() }) { item ->
                    SwipeToDeleteItem(item = item, onDelete = { DataManager.removeFromCart(item) })
                }
            }

            // Footer thanh toán
            Column(
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Price", fontSize = 16.sp, color = TextSecondary)
                    Text("$${String.format("%.2f", totalAmount)}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(20.dp))
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                    enabled = cartItems.isNotEmpty(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Checkout", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// Swipe to Delete Cart Item
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteItem(item: CartItem, onDelete: () -> Unit) {
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
                    .background(Color(0xFFFFF0F0), RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) {
        // Cart Item Card
        Card(
            colors = CardDefaults.cardColors(containerColor = BackgroundLight),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Coffee image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = getCoffeeImageResource(item.coffee)),
                        contentDescription = item.coffee.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Coffee details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.coffee.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "${item.shot} | ${item.ice} | ${item.size} | full ice",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "x ${item.quantity}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "$${String.format("%.2f", item.totalPrice)}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}