package com.example.thecodecup.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thecodecup.R
import com.example.thecodecup.Screen
import com.example.thecodecup.ui.theme.CardWhite
import com.example.thecodecup.ui.theme.IconActive
import com.example.thecodecup.ui.theme.IconInactive

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String? = null,
    modifier: Modifier = Modifier,
    surfaceColor: Color = CardWhite,
    shadowElevation: Dp = 8.dp,
    shape: Shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = surfaceColor,
        shape = shape,
        shadowElevation = shadowElevation
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // Home
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.store_icon),
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(
                            if (currentRoute == Screen.Home.route) IconActive else IconInactive
                        )
                    )
                },
                selected = currentRoute == Screen.Home.route,
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = IconActive,
                    unselectedIconColor = IconInactive
                )
            )

            // Rewards
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.gift_icon),
                        contentDescription = "Rewards",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(
                            if (currentRoute == Screen.Rewards.route) IconActive else IconInactive
                        )
                    )
                },
                selected = currentRoute == Screen.Rewards.route,
                onClick = {
                    navController.navigate(Screen.Rewards.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = IconActive,
                    unselectedIconColor = IconInactive
                )
            )

            // My Orders
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.bill_icon),
                        contentDescription = "My Orders",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(
                            if (currentRoute == Screen.MyOrders.route) IconActive else IconInactive
                        )
                    )
                },
                selected = currentRoute == Screen.MyOrders.route,
                onClick = {
                    navController.navigate(Screen.MyOrders.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = IconActive,
                    unselectedIconColor = IconInactive
                )
            )

            // Settings
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.settings_icon),
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(
                            if (currentRoute == Screen.Settings.route) IconActive else IconInactive
                        )
                    )
                },
                selected = currentRoute == Screen.Settings.route,
                onClick = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = IconActive,
                    unselectedIconColor = IconInactive
                )
            )
        }
    }
}


