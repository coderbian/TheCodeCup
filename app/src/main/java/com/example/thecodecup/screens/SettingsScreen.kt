package com.example.thecodecup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.R
import com.example.thecodecup.Screen
import com.example.thecodecup.model.DataManager
import com.example.thecodecup.ui.components.BottomNavBar
import com.example.thecodecup.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val isDarkMode by DataManager.isDarkMode
    val profile by DataManager.userProfile
    val notificationsEnabled by DataManager.notificationsEnabled
    val context = LocalContext.current
    var showClearDataDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = Screen.Settings.route) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Profile Link
                    SettingsItem(
                        icon = R.drawable.profile_icon,
                        title = "Profile",
                        subtitle = profile.fullName,
                        onClick = { navController.navigate(Screen.Profile.route) }
                    )
                }
            }
            
            // Appearance Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Appearance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Dark Mode Toggle with Custom Switch
                    DarkModeToggleItem(
                        isDarkMode = isDarkMode,
                        onToggle = { DataManager.toggleDarkMode() }
                    )
                }
            }
            
            // Preferences Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Preferences",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Notifications
                    SettingsItemWithSwitch(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Receive order updates",
                        isChecked = notificationsEnabled,
                        onCheckedChange = { DataManager.setNotificationsEnabled(it) }
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)
                    
                    // Location
                    SettingsItem(
                        icon = null,
                        iconVector = Icons.Default.LocationOn,
                        title = "Location",
                        subtitle = "Ho Chi Minh City",
                        onClick = { /* TODO: Implement */ }
                    )
                }
            }

            // Danger Zone
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Data",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showClearDataDialog = true }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = ErrorRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    "Clear all data",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ErrorRed
                                )
                                Text(
                                    "Remove cart, orders, rewards, profile and settings",
                                    fontSize = 13.sp,
                                    color = TextSecondaryGray
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextSecondaryGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        if (showClearDataDialog) {
            AlertDialog(
                onDismissRequest = { showClearDataDialog = false },
                title = { Text("Clear all data?") },
                text = { Text("This will delete all local data. You can’t undo this action.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            DataManager.clearAllData(context)
                            showClearDataDialog = false
                        }
                    ) {
                        Text("Clear", color = ErrorRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDataDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: Int? = null,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Icon
            if (icon != null) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(IconActive)
                )
            } else if (iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = IconActive
                )
            }
            
            // Text
            Column {
                Text(
                    title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimaryDark
                )
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = TextSecondaryGray
                )
            }
        }
        
        // Arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondaryGray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SettingsItemWithSwitch(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = IconActive
            )
            
            // Text
            Column {
                Text(
                    title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimaryDark
                )
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = TextSecondaryGray
                )
            }
        }
        
        // Switch
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ButtonPrimary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = TextSecondaryGray
            )
        )
    }
}

@Composable
fun DarkModeToggleItem(
    isDarkMode: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Moon/Sun Icon
            Image(
                painter = painterResource(id = R.drawable.moon_icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(IconActive)
            )
            
            // Text
            Column {
                Text(
                    "Dark Mode",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimaryDark
                )
                Text(
                    if (isDarkMode) "Dark theme enabled" else "Light theme enabled",
                    fontSize = 13.sp,
                    color = TextSecondaryGray
                )
            }
        }
        
        // Custom Toggle Switch (like the image)
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDarkMode) Color.Black else Color(0xFFFF5722))
                .clickable(onClick = onToggle)
                .padding(4.dp),
            contentAlignment = if (isDarkMode) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            // Toggle Circle
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            )
        }
    }
}

