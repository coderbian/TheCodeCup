package com.example.thecodecup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.R
import com.example.thecodecup.Screen
import com.example.thecodecup.ui.theme.*

@Composable
fun OrderSuccessScreen(navController: NavController) {
    Scaffold(
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Take away icon
            Image(
                painter = painterResource(id = R.drawable.take_away),
                contentDescription = "Order Success",
                modifier = Modifier.size(177.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = "Order Success",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "Your order has been placed successfully. For more details, go to my orders.",
                fontSize = 16.sp,
                color = TextSecondaryGray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Track My Order button
            Button(
                onClick = {
                    navController.navigate(Screen.MyOrders.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Track My Order",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


