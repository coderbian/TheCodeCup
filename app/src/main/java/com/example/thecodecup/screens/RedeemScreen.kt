package com.example.thecodecup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.model.DataManager
import com.example.thecodecup.model.RedeemableItem
import com.example.thecodecup.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemScreen(navController: NavController) {
    val redeemableItems = DataManager.redeemableItems
    val totalPoints = DataManager.totalPoints.value

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Redeem", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundPrimary,
                    titleContentColor = TextPrimaryDark
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(redeemableItems) { item ->
                RedeemableItemCard(
                    item = item,
                    totalPoints = totalPoints,
                    onRedeemClick = {
                        if (DataManager.redeemPoints(item)) {
                            // Show snackbar or navigate back
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RedeemableItemCard(
    item: RedeemableItem,
    totalPoints: Int,
    onRedeemClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Coffee Image Placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(CoffeeBrown, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Coffee,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Item Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Valid until ${item.validUntil}",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            // Redeem Button
            Button(
                onClick = onRedeemClick,
                enabled = totalPoints >= item.pointsRequired,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonPrimary,
                    disabledContainerColor = TextGray
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "${item.pointsRequired} pts",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

