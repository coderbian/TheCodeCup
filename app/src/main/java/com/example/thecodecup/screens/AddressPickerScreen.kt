package com.example.thecodecup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.ui.theme.BackgroundPrimary
import com.example.thecodecup.ui.theme.ButtonPrimary
import com.example.thecodecup.ui.theme.TextPrimaryDark
import com.example.thecodecup.ui.theme.TextSecondaryGray

/**
 * Placeholder VN address picker (static data).
 * Later you can replace the data source with a network API without changing the UI contract.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressPickerScreen(navController: NavController) {
    // Static sample data
    val data = remember {
        mapOf(
            "Ho Chi Minh" to mapOf(
                "District 1" to listOf("Ben Nghe", "Ben Thanh"),
                "Binh Thanh" to listOf("Ward 1", "Ward 2")
            ),
            "Ha Noi" to mapOf(
                "Ba Dinh" to listOf("Phuc Xa", "Truc Bach"),
                "Cau Giay" to listOf("Dich Vong", "Nghia Tan")
            ),
            "Da Nang" to mapOf(
                "Hai Chau" to listOf("Hai Chau 1", "Hai Chau 2"),
                "Son Tra" to listOf("An Hai Bac", "Phuoc My")
            )
        )
    }

    var provinceExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var wardExpanded by remember { mutableStateOf(false) }

    var province by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var ward by remember { mutableStateOf("") }
    var detailAddress by remember { mutableStateOf("") }

    val districts = remember(province) { data[province]?.keys?.toList().orEmpty() }
    val wards = remember(province, district) { data[province]?.get(district).orEmpty() }

    var showMissingDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            TopAppBar(
                title = { Text("Choose address", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Vietnam address (placeholder)", color = TextSecondaryGray, fontSize = 13.sp)

                    Column {
                        OutlinedTextField(
                            value = province,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Province/City") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondaryGray)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { provinceExpanded = true }
                        )
                        DropdownMenu(
                            expanded = provinceExpanded,
                            onDismissRequest = { provinceExpanded = false }
                        ) {
                            data.keys.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p) },
                                    onClick = {
                                        province = p
                                        district = ""
                                        ward = ""
                                        provinceExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Column {
                        OutlinedTextField(
                            value = district,
                            onValueChange = {},
                            readOnly = true,
                            enabled = province.isNotBlank(),
                            label = { Text("District") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondaryGray)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = province.isNotBlank()) { districtExpanded = true }
                        )
                        DropdownMenu(
                            expanded = districtExpanded,
                            onDismissRequest = { districtExpanded = false }
                        ) {
                            districts.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d) },
                                    onClick = {
                                        district = d
                                        ward = ""
                                        districtExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Column {
                        OutlinedTextField(
                            value = ward,
                            onValueChange = {},
                            readOnly = true,
                            enabled = district.isNotBlank(),
                            label = { Text("Ward") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondaryGray)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = district.isNotBlank()) { wardExpanded = true }
                        )
                        DropdownMenu(
                            expanded = wardExpanded,
                            onDismissRequest = { wardExpanded = false }
                        ) {
                            wards.forEach { w ->
                                DropdownMenuItem(
                                    text = { Text(w) },
                                    onClick = {
                                        ward = w
                                        wardExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = detailAddress,
                        onValueChange = { detailAddress = it },
                        label = { Text("Street / Detail") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    if (province.isBlank() || district.isBlank() || ward.isBlank() || detailAddress.isBlank()) {
                        showMissingDialog = true
                    } else {
                        val full = "$detailAddress, $ward, $district, $province"
                        navController.previousBackStackEntry?.savedStateHandle?.set("selected_address", full)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = ButtonPrimary)
            ) {
                Text("Use this address", color = Color.White, fontWeight = FontWeight.Medium)
            }
        }

        if (showMissingDialog) {
            AlertDialog(
                onDismissRequest = { showMissingDialog = false },
                title = { Text("Missing information") },
                text = { Text("Please select province, district, ward and enter detail address.") },
                confirmButton = {
                    Button(onClick = { showMissingDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}


