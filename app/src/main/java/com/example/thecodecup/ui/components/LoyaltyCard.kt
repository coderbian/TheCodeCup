package com.example.thecodecup.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.R
import androidx.compose.material3.Text

@Composable
fun LoyaltyCard(
    stamps: Int,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canReset = stamps >= 8

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(enabled = canReset) { onResetClick() }
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.Center) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Loyalty card", color = MaterialTheme.colorScheme.onSecondary, fontSize = 14.sp)
                Text(
                    "$stamps / 8",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(8) { index ->
                    val isActive = index < stamps
                    Image(
                        painter = painterResource(
                            id = if (isActive) R.drawable.loyalty_coffee_cup_active else R.drawable.loyalty_coffee_cup_deactive
                        ),
                        contentDescription = if (isActive) "Active stamp ${index + 1}" else "Inactive stamp ${index + 1}",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}


