package com.example.thecodecup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.thecodecup.model.PaymentMethod
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController) {
    val cartItems = DataManager.cart
    val totalAmount = DataManager.getCartTotal()
    val profile by DataManager.userProfile

    // Receive address from AddressPicker via SavedStateHandle
    val selectedAddressFlow =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("selected_address", "")
    val selectedAddress = selectedAddressFlow?.collectAsState(initial = "")?.value.orEmpty()

    var receiverName by remember { mutableStateOf(profile.fullName) }
    var receiverPhone by remember { mutableStateOf(profile.phoneNumber) }
    var address by remember { mutableStateOf(profile.address) }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }

    LaunchedEffect(selectedAddress) {
        if (selectedAddress.isNotBlank()) {
            address = selectedAddress
            // clear one-shot value
            navController.currentBackStackEntry?.savedStateHandle?.set("selected_address", "")
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Your cart is empty", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shipping info
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Shipping", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)

                    OutlinedTextField(
                        value = receiverName,
                        onValueChange = { receiverName = it },
                        label = { Text("Receiver name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = receiverPhone,
                        onValueChange = { receiverPhone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(0.dp))
                            Text("Address", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = { navController.navigate(Screen.AddressPicker.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Change", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Shipping address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Payment method
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Payment", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)

                    PaymentOption(
                        title = "Cash",
                        selected = paymentMethod == PaymentMethod.CASH,
                        onSelect = { paymentMethod = PaymentMethod.CASH }
                    )
                    PaymentOption(
                        title = "Bank transfer",
                        selected = paymentMethod == PaymentMethod.BANK_TRANSFER,
                        onSelect = { paymentMethod = PaymentMethod.BANK_TRANSFER }
                    )
                    PaymentOption(
                        title = "Card",
                        selected = paymentMethod == PaymentMethod.CARD,
                        onSelect = { paymentMethod = PaymentMethod.CARD }
                    )
                }
            }

            // Place order
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Text(
                            "$${String.format("%.2f", totalAmount)}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Button(
                        onClick = {
                            val order = Order(
                                id = UUID.randomUUID().toString(),
                                dateTime = DataManager.formatOrderDateTime(),
                                items = cartItems.toList(),
                                totalPrice = totalAmount,
                                status = OrderStatus.WAITING_PICKUP,
                                receiverName = receiverName.trim(),
                                receiverPhone = receiverPhone.trim(),
                                shippingAddress = address.trim(),
                                paymentMethod = paymentMethod
                            )
                            DataManager.addOrder(order)
                            DataManager.clearCart()
                            navController.navigate(Screen.OrderSuccess.route) {
                                popUpTo(Screen.Cart.route) { inclusive = true }
                            }
                        },
                        enabled = receiverName.isNotBlank() && receiverPhone.isNotBlank() && address.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Place order", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentOption(
    title: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
        RadioButton(selected = selected, onClick = onSelect)
    }
}


