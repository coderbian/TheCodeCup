package com.example.thecodecup.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thecodecup.ui.utils.getCoffeeImageResourceByName
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.widthIn

@Composable
fun CoffeeCard(
    coffeeName: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Keep a tight inset so the gray image container can be almost full-width
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // Fixed height makes all coffee previews feel consistent
                    .height(92.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getCoffeeImageResourceByName(coffeeName)),
                    contentDescription = coffeeName,
                    // Fill the container with consistent inner padding to normalize perceived size
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                coffeeName,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )
        }
    }
}


