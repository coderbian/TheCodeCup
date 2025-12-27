package com.example.thecodecup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.thecodecup.ui.utils.getCoffeeImageResource
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.thecodecup.ui.components.BottomNavBar
import com.example.thecodecup.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Waiting", "On going", "History")

    // Watch DataManager.orders to react to changes
    val allOrders = DataManager.orders

    val orders = remember(selectedTabIndex, allOrders) {
        when (selectedTabIndex) {
            0 -> DataManager.getWaitingPickupOrders()
            1 -> DataManager.getOngoingOrders() // includes DELIVERED (ready to confirm)
            else -> DataManager.getCompletedOrders()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        DataManager.orderDeliveredEvents.collectLatest {
            snackbarHostState.showSnackbar(
                message = "Your order has arrived. Please confirm receipt in On going.",
                withDismissAction = true
            )
        }
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            TopAppBar(
                title = { Text("My Order", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundPrimary,
                    titleContentColor = TextPrimaryDark
                )
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = Screen.MyOrders.route) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = BackgroundPrimary,
                contentColor = ButtonPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontSize = 13.sp) }
                    )
                }
            }

            // Order List
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (selectedTabIndex) {
                            0 -> "No waiting pickup orders"
                            1 -> "No ongoing orders"
                            else -> "No order history"
                        },
                        color = TextSecondaryGray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        val isHistory = selectedTabIndex == 2
                        OrderItemCard(
                            order = order,
                            isHistory = isHistory,
                            onConfirmReceived = {
                                // Only allowed when DELIVERED
                                DataManager.confirmDelivered(order.id)
                            },
                            showConfirmButton = selectedTabIndex == 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(
    order: Order,
    isHistory: Boolean,
    showConfirmButton: Boolean,
    onConfirmReceived: () -> Unit
) {
    val titleColor = if (isHistory) TextPrimaryDark.copy(alpha = 0.70f) else TextPrimaryDark
    val secondaryColor = if (isHistory) TextSecondaryGray.copy(alpha = 0.70f) else TextSecondaryGray

    Card(
        colors = CardDefaults.cardColors(containerColor = CardLightGray),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Date and Time with Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.dateTime,
                    color = secondaryColor,
                    fontSize = 12.sp
                )
                Text(
                    text = "$${String.format("%.2f", order.totalPrice)}",
                    color = titleColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Small status UI (for Waiting/On going)
            if (!isHistory) {
                val (statusText, statusColor) = when (order.status) {
                    OrderStatus.WAITING_PICKUP -> "Waiting pickup (≈2s)" to TextSecondaryGray
                    OrderStatus.ONGOING -> "Delivering (≈3s)" to CoffeeAccent
                    OrderStatus.DELIVERED -> "Delivered - please confirm" to ButtonPrimary
                    OrderStatus.COMPLETED -> "Completed" to TextSecondaryGray
                }
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Progress indicator
                val progress = when (order.status) {
                    OrderStatus.WAITING_PICKUP -> 0.33f
                    OrderStatus.ONGOING -> 0.66f
                    OrderStatus.DELIVERED -> 1f
                    OrderStatus.COMPLETED -> 1f
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (order.status == OrderStatus.DELIVERED) ButtonPrimary else CoffeeAccent,
                    trackColor = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Coffee items
            order.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = getCoffeeImageResource(item.coffee)),
                        contentDescription = item.coffee.name,
                        modifier = Modifier.size(18.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${item.coffee.name} x${item.quantity}",
                        color = titleColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = secondaryColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.shippingAddress,
                    color = secondaryColor,
                    fontSize = 12.sp
                )
            }

            // Confirm received (only show in On going tab)
            if (showConfirmButton) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onConfirmReceived,
                    enabled = order.status == OrderStatus.DELIVERED,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonPrimary,
                        disabledContainerColor = ButtonPrimary.copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text = if (order.status == OrderStatus.DELIVERED) "Confirm received" else "Waiting for delivery…",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

