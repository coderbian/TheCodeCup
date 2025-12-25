package com.example.thecodecup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.*
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
import com.example.thecodecup.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(navController: NavController) {
    val loyaltyStamps = DataManager.loyaltyStamps.value
    val totalPoints = DataManager.totalPoints.value
    val rewardHistory = DataManager.rewardHistory

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Rewards", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = Screen.Rewards.route) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Loyalty Card Section
            LoyaltyCardSection(
                stamps = loyaltyStamps,
                onResetClick = {
                    if (loyaltyStamps >= 8) {
                        DataManager.resetLoyaltyStamps()
                    }
                }
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
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            if (rewardHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reward history yet",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
fun LoyaltyCardSection(stamps: Int, onResetClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightCards),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(enabled = stamps >= 8, onClick = onResetClick)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Loyalty card", color = TextGray, fontSize = 14.sp)
                Text("$stamps / 8", color = TextGray, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(8) { index ->
                    Icon(
                        imageVector = Icons.Default.LocalCafe,
                        contentDescription = null,
                        tint = if (index < stamps) CoffeeBrown else Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MyPointsSection(totalPoints: Int, onRedeemClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightCards),
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
                Text("My Points:", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = totalPoints.toString(),
                    color = TextWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onRedeemClick,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Redeem drinks", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun RewardHistoryItem(history: com.example.thecodecup.model.RewardHistory) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = history.coffeeName,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "+ ${history.points} Pts",
                color = CoffeeBrown,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = history.dateTime,
                color = TextGray,
                fontSize = 12.sp
            )
        }
    }
}

