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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(navController: NavController) {
    // Check if we should navigate to a specific tab (from notification)
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    
    // Handle tab selection from notification
    LaunchedEffect(Unit) {
        val tabFromNotification = savedStateHandle?.get<Int>("selected_tab")
        if (tabFromNotification != null && tabFromNotification >= 0 && tabFromNotification < 3) {
            selectedTabIndex = tabFromNotification
            savedStateHandle.remove<Int>("selected_tab")
        }
    }
    
    val tabs = listOf("Waiting", "On going", "History")

    // Watch DataManager.orders to react to changes
    val allOrders = DataManager.orders
    
    // Create a snapshot key that changes when any order status changes
    // This ensures recomposition when order status updates
    val ordersSnapshotKey = remember {
        derivedStateOf {
            // Create a key based on order IDs and their statuses
            allOrders.joinToString(",") { "${it.id}:${it.status}" }
        }
    }
    
    // Recalculate orders whenever snapshot key changes or tab changes
    val orders = remember(selectedTabIndex, ordersSnapshotKey.value) {
        when (selectedTabIndex) {
            0 -> DataManager.getWaitingPickupOrders()
            1 -> DataManager.getOngoingOrders() // includes DELIVERED (ready to confirm)
            else -> DataManager.getCompletedOrders()
        }
    }

    // Confirmation dialog state
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedOrderId by remember { mutableStateOf<String?>(null) }
    
    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("My Order", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = Screen.MyOrders.route) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                // Show confirmation dialog
                                selectedOrderId = order.id
                                showConfirmDialog = true
                            },
                            showConfirmButton = selectedTabIndex == 1
                        )
                    }
                }
            }
        }
    }
    
    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { 
                showConfirmDialog = false
                selectedOrderId = null
            },
            title = { 
                Text(
                    "Confirm Receipt",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text("Are you sure you have received this order?") 
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedOrderId?.let { orderId ->
                            if (DataManager.confirmDelivered(orderId)) {
                                showConfirmDialog = false
                                selectedOrderId = null
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Order confirmed successfully!",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Failed to confirm order. Please try again.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showConfirmDialog = false
                    selectedOrderId = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun OrderItemCard(
    order: Order,
    isHistory: Boolean,
    showConfirmButton: Boolean,
    onConfirmReceived: () -> Unit
) {
    val titleColor = if (isHistory) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val secondaryColor = if (isHistory) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                    OrderStatus.WAITING_PICKUP -> "Waiting pickup (≈5s)" to MaterialTheme.colorScheme.onSurfaceVariant
                    OrderStatus.ONGOING -> "Delivering (≈10s)" to MaterialTheme.colorScheme.tertiary
                    OrderStatus.DELIVERED -> "Delivered - please confirm" to MaterialTheme.colorScheme.primary
                    OrderStatus.COMPLETED -> "Completed" to MaterialTheme.colorScheme.onSurfaceVariant
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
                    color = if (order.status == OrderStatus.DELIVERED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surface
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
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text = if (order.status == OrderStatus.DELIVERED) "Confirm received" else "Waiting for delivery…",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

