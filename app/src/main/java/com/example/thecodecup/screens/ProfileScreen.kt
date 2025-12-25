package com.example.thecodecup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun ProfileScreen(navController: NavController) {
    val profile by DataManager.userProfile

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute = Screen.Profile.route) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Full name
            ProfileFieldItem(
                icon = Icons.Default.Person,
                label = "Full name",
                value = profile.fullName,
                onSave = { newValue ->
                    DataManager.updateUserProfile(
                        profile.copy(fullName = newValue)
                    )
                }
            )

            // Phone number
            ProfileFieldItem(
                icon = Icons.Default.Phone,
                label = "Phone number",
                value = profile.phoneNumber,
                onSave = { newValue ->
                    DataManager.updateUserProfile(
                        profile.copy(phoneNumber = newValue)
                    )
                }
            )

            // Email
            ProfileFieldItem(
                icon = Icons.Default.Email,
                label = "Email",
                value = profile.email,
                onSave = { newValue ->
                    DataManager.updateUserProfile(
                        profile.copy(email = newValue)
                    )
                }
            )

            // Address
            ProfileFieldItem(
                icon = Icons.Default.LocationOn,
                label = "Address",
                value = profile.address,
                onSave = { newValue ->
                    DataManager.updateUserProfile(
                        profile.copy(address = newValue)
                    )
                },
                isMultiline = true
            )
        }
    }
}

@Composable
fun ProfileFieldItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onSave: (String) -> Unit,
    isMultiline: Boolean = false
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedValue by remember { mutableStateOf(value) }

    // Update editedValue when value changes externally
    LaunchedEffect(value) {
        if (!isEditing) {
            editedValue = value
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    color = TextGray,
                    fontSize = 14.sp
                )
                if (isEditing) {
                    if (isMultiline) {
                        OutlinedTextField(
                            value = editedValue,
                            onValueChange = { editedValue = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ButtonBlue,
                                unfocusedBorderColor = TextGray.copy(alpha = 0.5f)
                            ),
                            maxLines = 3
                        )
                    } else {
                        OutlinedTextField(
                            value = editedValue,
                            onValueChange = { editedValue = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ButtonBlue,
                                unfocusedBorderColor = TextGray.copy(alpha = 0.5f)
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(onClick = {
                            editedValue = value
                            isEditing = false
                        }) {
                            Text("Cancel", color = TextGray)
                        }
                        TextButton(onClick = {
                            onSave(editedValue)
                            isEditing = false
                        }) {
                            Text("Save", color = ButtonBlue, fontWeight = FontWeight.Medium)
                        }
                    }
                } else {
                    Text(
                        text = value,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Edit icon
            if (!isEditing) {
                IconButton(onClick = {
                    editedValue = value
                    isEditing = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

