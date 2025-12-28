package com.example.thecodecup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.model.DataManager

@Composable
fun PromoCodeDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var promoCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        title = { 
            Text(
                "Enter Promo Code",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Enter your promo code to get special discounts",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = promoCode,
                    onValueChange = { 
                        promoCode = it.uppercase()
                        errorMessage = null
                    },
                    label = { Text("Promo Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    enabled = !isProcessing,
                    placeholder = { Text("e.g., WELCOME2024") }
                )
                
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (promoCode.isBlank()) {
                        errorMessage = "Please enter a promo code"
                        return@Button
                    }
                    
                    isProcessing = true
                    val voucher = DataManager.applyPromoCode(promoCode)
                    if (voucher != null) {
                        errorMessage = null
                        isProcessing = false
                        onSuccess()
                    } else {
                        errorMessage = "Invalid or already used promo code"
                        isProcessing = false
                    }
                },
                enabled = !isProcessing && promoCode.isNotBlank()
            ) {
                Text(if (isProcessing) "Processing..." else "Apply")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isProcessing
            ) {
                Text("Cancel")
            }
        }
    )
}

