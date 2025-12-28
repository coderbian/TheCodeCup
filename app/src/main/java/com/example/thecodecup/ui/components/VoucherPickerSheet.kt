package com.example.thecodecup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.model.DataManager
import com.example.thecodecup.model.Voucher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherPickerSheet(
    onVoucherSelected: (Voucher) -> Unit,
    onDismiss: () -> Unit,
    totalQuantity: Int = 0
) {
    val activeVouchers = DataManager.getActiveVouchers()
    
    // Filter vouchers that meet conditions
    val validVouchers = activeVouchers.filter { voucher ->
        voucher.minOrderQuantity == null || totalQuantity >= voucher.minOrderQuantity
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Select Voucher",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (activeVouchers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No vouchers available",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Redeem or enter promo codes to get vouchers",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Show valid vouchers first
                    items(validVouchers) { voucher ->
                        VoucherCard(
                            voucher = voucher,
                            onClick = {
                                onVoucherSelected(voucher)
                                onDismiss()
                            }
                        )
                    }
                    
                    // Show invalid vouchers (with conditions not met) at the bottom
                    val invalidVouchers = activeVouchers.filter { voucher ->
                        voucher.minOrderQuantity != null && totalQuantity < voucher.minOrderQuantity
                    }
                    
                    if (invalidVouchers.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Vouchers requiring more items:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(invalidVouchers) { voucher ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    VoucherCard(
                                        voucher = voucher,
                                        onClick = null // Disabled
                                    )
                                    Text(
                                        text = "⚠️ Cần mua tối thiểu ${voucher.minOrderQuantity} ly (hiện tại: $totalQuantity ly)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

