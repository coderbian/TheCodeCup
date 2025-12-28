package com.example.thecodecup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thecodecup.model.DataManager
import com.example.thecodecup.ui.theme.TheCodeCupTheme
import com.example.thecodecup.screens.HomeScreen
import com.example.thecodecup.screens.SplashScreen
import com.example.thecodecup.screens.DetailsScreen
import com.example.thecodecup.screens.CartScreen
import com.example.thecodecup.screens.OrderSuccessScreen
import com.example.thecodecup.screens.MyOrdersScreen
import com.example.thecodecup.screens.RewardsScreen
import com.example.thecodecup.screens.RedeemScreen
import com.example.thecodecup.screens.ProfileScreen
import com.example.thecodecup.screens.SettingsScreen
import com.example.thecodecup.screens.CheckoutScreen
import com.example.thecodecup.screens.AddressPickerScreen
import com.example.thecodecup.screens.MyVouchersScreen
import com.example.thecodecup.screens.RedeemVoucherScreen
import com.example.thecodecup.utils.NotificationManager

class MainActivity : ComponentActivity() {
    // Permission launcher for notification permission (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled, notifications will work if granted
    }
    
    // State to track notification navigation
    private var notificationNavigationState = mutableStateOf<Pair<String, Int>?>(null)

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val navigateTo = intent.getStringExtra("navigate_to")
        val tabIndex = intent.getIntExtra("tab_index", -1)
        if (navigateTo != null && tabIndex >= 0) {
            notificationNavigationState.value = Pair(navigateTo, tabIndex)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Initialize NotificationManager
        NotificationManager.init(applicationContext)
        
        // Request notification permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        
        setContent {
            LaunchedEffect(Unit) {
                DataManager.init(applicationContext)
            }
            val isDarkMode by DataManager.isDarkMode
            
            TheCodeCupTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                
                // Check if we're coming from notification (on first launch)
                val fromNotification = intent.getBooleanExtra("from_notification", false)
                val navigateTo = intent.getStringExtra("navigate_to")
                val tabIndex = intent.getIntExtra("tab_index", -1)
                
                // Observe notification navigation state (for when app is already running)
                val notificationNav by notificationNavigationState

                // Handle navigation from notification
                LaunchedEffect(notificationNav) {
                    val (navTo, tabIdx) = notificationNav ?: return@LaunchedEffect
                    if (navTo == "my_orders" && tabIdx >= 0) {
                        // Wait a bit to ensure NavHost is ready
                        kotlinx.coroutines.delay(300)
                        // Navigate to MyOrders if not already there
                        if (navController.currentDestination?.route != Screen.MyOrders.route) {
                            navController.navigate(Screen.MyOrders.route) {
                                // Pop back stack to home if needed
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                        // Store tab index for MyOrdersScreen to use
                        navController.currentBackStackEntry?.savedStateHandle?.set("selected_tab", tabIdx)
                        // Clear the state after handling
                        notificationNavigationState.value = null
                    }
                }
                
                // Handle navigation from notification on first launch
                LaunchedEffect(fromNotification, navigateTo, tabIndex) {
                    if (fromNotification && navigateTo == "my_orders" && tabIndex >= 0) {
                        // Wait a bit to ensure NavHost is ready
                        kotlinx.coroutines.delay(300)
                        // Store tab index for MyOrdersScreen to use
                        navController.currentBackStackEntry?.savedStateHandle?.set("selected_tab", tabIndex)
                    }
                }

                NavHost(
                    navController = navController, 
                    startDestination = if (fromNotification && navigateTo == "my_orders") {
                        // Skip splash if coming from notification on first launch
                        Screen.MyOrders.route
                    } else {
                        Screen.Splash.route
                    }
                ) {
                    composable(Screen.Splash.route) { SplashScreen(navController) }
                    composable(Screen.Home.route) { HomeScreen(navController) }

                    // Route Details nhận ID
                    composable(Screen.Details.route) { backStackEntry ->
                        val coffeeId = backStackEntry.arguments?.getString("coffeeId")
                        DetailsScreen(navController, coffeeId)
                    }

                    // Route Cart
                    composable(Screen.Cart.route) { CartScreen(navController) }

                    // Route Checkout
                    composable(Screen.Checkout.route) { CheckoutScreen(navController) }

                    // Route Address Picker (placeholder data)
                    composable(Screen.AddressPicker.route) { AddressPickerScreen(navController) }

                    // Route Order Success
                    composable(Screen.OrderSuccess.route) { OrderSuccessScreen(navController) }

                    // Route My Orders
                    composable(Screen.MyOrders.route) { 
                        // Handle navigation from notification
                        LaunchedEffect(Unit) {
                            if (fromNotification && navigateTo == "my_orders" && tabIndex >= 0) {
                                // Store tab index for MyOrdersScreen to use
                                navController.currentBackStackEntry?.savedStateHandle?.set("selected_tab", tabIndex)
                            }
                        }
                        MyOrdersScreen(navController) 
                    }

                    // Route Rewards
                    composable(Screen.Rewards.route) { RewardsScreen(navController) }

                    // Route Redeem
                    composable(Screen.Redeem.route) { RedeemScreen(navController) }

                    // Route Profile
                    composable(Screen.Profile.route) { ProfileScreen(navController) }

                    // Route Settings
                    composable(Screen.Settings.route) { SettingsScreen(navController) }
                    
                    // Route My Vouchers
                    composable(Screen.MyVouchers.route) { MyVouchersScreen(navController) }
                    
                    // Route Redeem Voucher
                    composable(Screen.RedeemVoucher.route) { RedeemVoucherScreen(navController) }
                }
            }
        }
    }
}