package com.example.thecodecup.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.thecodecup.Screen

/**
 * Deprecated: This screen redirects to RedeemVoucherScreen
 * Kept for backward compatibility with existing navigation routes
 */
@Composable
fun RedeemScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        navController.navigate(Screen.RedeemVoucher.route) {
            popUpTo(Screen.Redeem.route) { inclusive = true }
        }
    }
}

