package com.example.thecodecup

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Home : Screen("home_screen")
    object Details : Screen("details_screen/{coffeeId}") { // Nhận ID món
        fun createRoute(coffeeId: String) = "details_screen/$coffeeId"
    }
    object Cart : Screen("cart_screen")
    object Checkout : Screen("checkout_screen")
    object AddressPicker : Screen("address_picker_screen")
    object OrderSuccess : Screen("order_success_screen")
    object MyOrders : Screen("my_orders_screen")
    object Rewards : Screen("rewards_screen")
    object Redeem : Screen("redeem_screen")
    object Profile : Screen("profile_screen")
    object Settings : Screen("settings_screen")
}