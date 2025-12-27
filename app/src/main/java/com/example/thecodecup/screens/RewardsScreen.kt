package com.example.thecodecup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import com.example.thecodecup.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.Screen
import com.example.thecodecup.model.DataManager
import com.example.thecodecup.model.RewardHistory
import com.example.thecodecup.ui.components.BottomNavBar
import com.example.thecodecup.ui.components.LoyaltyCard
import com.example.thecodecup.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(navController: NavController) {
    val loyaltyStamps = DataManager.loyaltyStamps.value
    val totalPoints = DataManager.totalPoints.value
    val rewardHistory = DataManager.rewardHistory

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Rewards", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = Screen.Rewards.route) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Loyalty Card Section
            LoyaltyCard(
                stamps = loyaltyStamps,
                onResetClick = { if (loyaltyStamps >= 8) DataManager.resetLoyaltyStamps() }
            )

            // My Points Section
            MyPointsSection(
                totalPoints = totalPoints,
                onRedeemClick = {
                    navController.navigate(Screen.Redeem.route)
                }
            )

            // History Rewards Section
            Text(
                text = "History Rewards",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (rewardHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reward history yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(rewardHistory.reversed()) { history ->
                        RewardHistoryItem(history = history)
                    }
                }
            }
        }
    }
}

@Composable
fun MyPointsSection(totalPoints: Int, onRedeemClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("My Points:", color = MaterialTheme.colorScheme.onSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = totalPoints.toString(),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onRedeemClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Redeem drinks", color = MaterialTheme.colorScheme.onTertiary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun RewardHistoryItem(history: RewardHistory) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${history.coffeeName} x${history.quantity}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "+ ${history.points} Pts",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = history.dateTime,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

