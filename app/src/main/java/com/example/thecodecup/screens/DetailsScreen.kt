package com.example.thecodecup.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.R
import com.example.thecodecup.Screen
import com.example.thecodecup.model.CartItem
import com.example.thecodecup.model.DataManager
import com.example.thecodecup.ui.theme.*
import com.example.thecodecup.ui.utils.getCoffeeImageResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(navController: NavController, coffeeId: String?) {
    // Lấy thông tin món từ ID
    val coffee = DataManager.menu.find { it.id == coffeeId } ?: DataManager.menu.first()

    // State cho các lựa chọn
    var quantity by remember { mutableIntStateOf(1) }
    var selectedSize by remember { mutableStateOf("M") }
    var selectedShot by remember { mutableStateOf("Single") }
    var selectedSelect by remember { mutableStateOf("Hot") } // Hot or Cold
    var selectedIce by remember { mutableStateOf("Full") }

    // Tính tổng tiền Dynamic
    val sizePrice = if (selectedSize == "L") 1.0 else 0.0
    val shotPrice = if (selectedShot == "Double") 0.5 else 0.0
    val totalPrice = (coffee.basePrice + sizePrice + shotPrice) * quantity

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Details", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = TextPrimaryDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Ảnh Cafe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(CardLightGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getCoffeeImageResource(coffee)),
                    contentDescription = coffee.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Tên món và Số lượng
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = coffee.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        QuantitySelector(quantity = quantity, onIncrease = { quantity++ }, onDecrease = { if (quantity > 1) quantity-- })
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                    // Shot
                    OptionRow(
                        title = "Shot",
                        options = listOf("Single", "Double"),
                        selected = selectedShot,
                        onSelect = { selectedShot = it },
                        isTextOnly = true
                    )

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                    // Select (Hot/Cold)
                    OptionRow(
                        title = "Select",
                        options = listOf("Hot", "Cold"),
                        selected = selectedSelect,
                        onSelect = { selectedSelect = it },
                        isTextOnly = false
                    )

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                    // Size
                    SizeOptionRow(
                        selected = selectedSize,
                        onSelect = { selectedSize = it }
                    )

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                    // Ice
                    IceOptionRow(
                        selected = selectedIce,
                        onSelect = { selectedIce = it }
                    )
                }

                // Footer: Button Add to Cart
                Button(
                    onClick = {
                        DataManager.addToCart(
                            CartItem(coffee, selectedSize, selectedSelect, selectedShot, quantity, totalPrice)
                        )
                        navController.navigate(Screen.Cart.route)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to cart", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

// Quantity Selector
@Composable
fun QuantitySelector(quantity: Int, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        color = Color.White
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onDecrease, modifier = Modifier.size(24.dp)) {
                Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            }
            Text("$quantity", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            IconButton(onClick = onIncrease, modifier = Modifier.size(24.dp)) {
                Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            }
        }
    }
}

// Option Row for text-only options (Shot)
@Composable
fun OptionRow(title: String, options: List<String>, selected: String, onSelect: (String) -> Unit, isTextOnly: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = option == selected
                if (isTextOnly) {
                    // Text-only button (Shot)
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        border = if (isSelected) BorderStroke(1.dp, TextPrimaryDark) else BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        color = if (isSelected) Color.Black else Color.White,
                        modifier = Modifier.clickable { onSelect(option) }
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                            color = if (isSelected) Color.White else TextSecondaryGray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // Icon button (Select - Hot/Cold)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = if (isSelected) BorderStroke(2.dp, TextPrimaryDark) else BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        color = Color.White,
                        modifier = Modifier
                            .size(56.dp)
                            .clickable { onSelect(option) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(
                                    id = if (option == "Hot") R.drawable.hot_icon else R.drawable.cold_icon
                                ),
                                contentDescription = option,
                                modifier = Modifier.size(28.dp),
                                colorFilter = ColorFilter.tint(if (isSelected) Color.Black else Color(0xFFD8D8D8))
                            )
                        }
                    }
                }
            }
        }
    }
}

// Size Option Row with cup icons
@Composable
fun SizeOptionRow(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Size", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("S", "M", "L").forEach { size ->
                val isSelected = size == selected
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) BorderStroke(2.dp, TextPrimaryDark) else BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    color = Color.White,
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onSelect(size) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.cup_size),
                            contentDescription = size,
                            modifier = Modifier.size(if (size == "S") 20.dp else if (size == "M") 28.dp else 36.dp),
                            colorFilter = ColorFilter.tint(if (isSelected) Color.Black else Color(0xFFD8D8D8))
                        )
                    }
                }
            }
        }
    }
}

// Ice Option Row
@Composable
fun IceOptionRow(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Ice", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val iceOptions = listOf(
                "No" to R.drawable.ice_1,
                "Less" to R.drawable.ice_2,
                "Full" to R.drawable.ice_3
            )
            
            iceOptions.forEach { (ice, iconRes) ->
                val isSelected = ice == selected
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) BorderStroke(2.dp, TextPrimaryDark) else BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    color = Color.White,
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onSelect(ice) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = ice,
                            modifier = Modifier.size(28.dp),
                            colorFilter = ColorFilter.tint(if (isSelected) Color.Black else Color(0xFFD8D8D8))
                        )
                    }
                }
            }
        }
    }
}