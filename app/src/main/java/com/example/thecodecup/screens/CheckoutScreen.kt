package com.example.thecodecup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.Surface
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
import com.example.thecodecup.model.Voucher
import com.example.thecodecup.ui.components.PromoCodeDialog
import com.example.thecodecup.ui.components.VoucherCard
import com.example.thecodecup.ui.components.VoucherPickerSheet
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
    
    // Voucher state
    var selectedVoucher by remember { mutableStateOf<Voucher?>(null) }
    var showVoucherPicker by remember { mutableStateOf(false) }
    var showPromoDialog by remember { mutableStateOf(false) }
    var voucherError by remember { mutableStateOf<String?>(null) }
    
    // Calculate total quantity in cart
    val totalQuantity = cartItems.sumOf { it.quantity }
    
    // Check if selected voucher is valid
    val isVoucherValid = remember(selectedVoucher, totalQuantity) {
        if (selectedVoucher == null) true
        else {
            val voucher = selectedVoucher!!
            // Check min order quantity condition
            if (voucher.minOrderQuantity != null && totalQuantity < voucher.minOrderQuantity) {
                false
            } else {
                true
            }
        }
    }
    
    // Calculate discount (only if voucher is valid)
    val discount = if (selectedVoucher != null && isVoucherValid) {
        (totalAmount * selectedVoucher!!.discountPercent / 100.0)
    } else 0.0
    
    val finalTotal = totalAmount - discount

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Shipping info
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Shipping",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        )
                    }

                    OutlinedTextField(
                        value = receiverName,
                        onValueChange = { receiverName = it },
                        label = { Text("Receiver name", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                    )
                    OutlinedTextField(
                        value = receiverPhone,
                        onValueChange = { receiverPhone = it },
                        label = { Text("Phone", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "Address",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        }
                        Button(
                            onClick = { navController.navigate(Screen.AddressPicker.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Change", color = MaterialTheme.colorScheme.onPrimary, fontSize = 13.sp)
                        }
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Shipping address", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        minLines = 2
                    )
                }
            }

            // Payment method
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Payment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Payment",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        )
                    }

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

            // Voucher section
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Voucher",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        )
                    }

                    if (selectedVoucher == null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showVoucherPicker = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("Select Voucher", fontSize = 13.sp)
                            }
                            Button(
                                onClick = { showPromoDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("Enter Code", fontSize = 13.sp)
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            VoucherCard(
                                voucher = selectedVoucher!!,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            // Show warning if voucher condition not met
                            if (!isVoucherValid && selectedVoucher!!.minOrderQuantity != null) {
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "⚠️ Cần mua tối thiểu ${selectedVoucher!!.minOrderQuantity} ly để sử dụng voucher này. Hiện tại: $totalQuantity ly",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                            
                            Button(
                                onClick = { selectedVoucher = null },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Remove Voucher",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Place order
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Text(
                            "$${String.format("%.2f", totalAmount)}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    }
                    
                    // Discount (if voucher applied and valid)
                    if (selectedVoucher != null && isVoucherValid) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Discount (${selectedVoucher!!.discountPercent}%)",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 13.sp
                            )
                            Text(
                                "-$${String.format("%.2f", discount)}",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else if (selectedVoucher != null && !isVoucherValid) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Discount (${selectedVoucher!!.discountPercent}%)",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 13.sp
                            )
                            Text(
                                "Not applicable",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total", color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "$${String.format("%.2f", finalTotal)}",
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
                                totalPrice = finalTotal,
                                status = OrderStatus.WAITING_PICKUP,
                                receiverName = receiverName.trim(),
                                receiverPhone = receiverPhone.trim(),
                                shippingAddress = address.trim(),
                                paymentMethod = paymentMethod
                            )
                            DataManager.addOrder(order)
                            DataManager.clearCart()
                            
                            // Use voucher if applied and valid
                            if (selectedVoucher != null && isVoucherValid) {
                                DataManager.useVoucher(selectedVoucher!!.id)
                            }
                            
                            navController.navigate(Screen.OrderSuccess.route) {
                                popUpTo(Screen.Cart.route) { inclusive = true }
                            }
                        },
                        enabled = receiverName.isNotBlank() && receiverPhone.isNotBlank() && address.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Place order", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    }
                }
            }
        }
    }
    
    // Voucher picker bottom sheet
    if (showVoucherPicker) {
        VoucherPickerSheet(
            onVoucherSelected = { voucher ->
                selectedVoucher = voucher
            },
            onDismiss = { showVoucherPicker = false },
            totalQuantity = totalQuantity
        )
    }
    
    // Promo code dialog
    if (showPromoDialog) {
        PromoCodeDialog(
            onDismiss = { showPromoDialog = false },
            onSuccess = { 
                showPromoDialog = false
                // Optionally auto-select the newly added voucher
                val latestVoucher = DataManager.getActiveVouchers().lastOrNull()
                if (latestVoucher != null) {
                    selectedVoucher = latestVoucher
                }
            }
        )
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
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
        RadioButton(selected = selected, onClick = onSelect)
    }
}


