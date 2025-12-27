package com.example.thecodecup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.thecodecup.model.DataManager
import com.example.thecodecup.screens.HomeScreen
import com.example.thecodecup.ui.screens.SplashScreen
import com.example.thecodecup.ui.theme.TheCodeCupTheme
import com.example.thecodecup.ui.screens.DetailsScreen
import com.example.thecodecup.ui.screens.CartScreen
import com.example.thecodecup.screens.OrderSuccessScreen
import com.example.thecodecup.screens.MyOrdersScreen
import com.example.thecodecup.screens.RewardsScreen
import com.example.thecodecup.screens.RedeemScreen
import com.example.thecodecup.screens.ProfileScreen
import com.example.thecodecup.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by DataManager.isDarkMode
            
            TheCodeCupTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Screen.Splash.route) {
                    composable(Screen.Splash.route) { SplashScreen(navController) }
                    composable(Screen.Home.route) { HomeScreen(navController) }

                    // Route Details nhận ID
                    composable(Screen.Details.route) { backStackEntry ->
                        val coffeeId = backStackEntry.arguments?.getString("coffeeId")
                        DetailsScreen(navController, coffeeId)
                    }

                    // Route Cart
                    composable(Screen.Cart.route) { CartScreen(navController) }

                    // Route Order Success
                    composable(Screen.OrderSuccess.route) { OrderSuccessScreen(navController) }

                    // Route My Orders
                    composable(Screen.MyOrders.route) { MyOrdersScreen(navController) }

                    // Route Rewards
                    composable(Screen.Rewards.route) { RewardsScreen(navController) }

                    // Route Redeem
                    composable(Screen.Redeem.route) { RedeemScreen(navController) }

                    // Route Profile
                    composable(Screen.Profile.route) { ProfileScreen(navController) }

                    // Route Settings
                    composable(Screen.Settings.route) { SettingsScreen(navController) }
                }
            }
        }
    }
}