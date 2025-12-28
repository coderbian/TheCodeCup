package com.example.thecodecup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thecodecup.model.AddressManager
import com.example.thecodecup.model.Province
import com.example.thecodecup.model.District
import com.example.thecodecup.model.Ward

/**
 * VN address picker with real API data from vnappmob.com
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressPickerScreen(navController: NavController) {
    // Load provinces on first launch
    LaunchedEffect(Unit) {
        AddressManager.loadProvinces()
    }

    // State from AddressManager
    val provinces by AddressManager.provinces
    val districts by AddressManager.districts
    val wards by AddressManager.wards
    
    val isLoadingProvinces by AddressManager.isLoadingProvinces
    val isLoadingDistricts by AddressManager.isLoadingDistricts
    val isLoadingWards by AddressManager.isLoadingWards
    
    val provincesError by AddressManager.provincesError
    val districtsError by AddressManager.districtsError
    val wardsError by AddressManager.wardsError

    var provinceExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var wardExpanded by remember { mutableStateOf(false) }

    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var selectedDistrict by remember { mutableStateOf<District?>(null) }
    var selectedWard by remember { mutableStateOf<Ward?>(null) }
    var detailAddress by remember { mutableStateOf("") }

    var showMissingDialog by remember { mutableStateOf(false) }

    // Load districts when province changes
    LaunchedEffect(selectedProvince) {
        selectedProvince?.let { province ->
            AddressManager.clearDistricts()
            AddressManager.clearWards()
            selectedDistrict = null
            selectedWard = null
            AddressManager.loadDistricts(province.id)
        }
    }

    // Load wards when district changes
    LaunchedEffect(selectedDistrict) {
        selectedDistrict?.let { district ->
            AddressManager.clearWards()
            selectedWard = null
            AddressManager.loadWards(district.id)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Choose address", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        // Show error state if provinces failed to load
        if (provincesError != null && provinces.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "Failed to load provinces",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = provincesError ?: "Unknown error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { AddressManager.retryLoadProvinces() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Retry")
                    }
                }
            }
            return@Scaffold
        }

        // Show loading state
        if (isLoadingProvinces && provinces.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading provinces...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Vietnam address", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)

                    ExposedDropdownMenuBox(
                        expanded = provinceExpanded,
                        onExpandedChange = { provinceExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedProvince?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Province/City") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = provinceExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = provinceExpanded,
                            onDismissRequest = { provinceExpanded = false }
                        ) {
                            provinces.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.name) },
                                    onClick = {
                                        selectedProvince = p
                                        provinceExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    Column {
                        ExposedDropdownMenuBox(
                            expanded = districtExpanded,
                            onExpandedChange = { 
                                if (selectedProvince != null && !isLoadingDistricts) {
                                    districtExpanded = it
                                }
                            }
                        ) {
                            OutlinedTextField(
                                value = selectedDistrict?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                enabled = selectedProvince != null && !isLoadingDistricts,
                                label = { 
                                    Text(
                                        if (isLoadingDistricts) "Loading districts..." 
                                        else "District"
                                    ) 
                                },
                                trailingIcon = {
                                    if (isLoadingDistricts) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded)
                                    }
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = districtExpanded,
                                onDismissRequest = { districtExpanded = false }
                            ) {
                                districts.forEach { d ->
                                    DropdownMenuItem(
                                        text = { Text(d.name) },
                                        onClick = {
                                            selectedDistrict = d
                                            districtExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                        
                        if (districtsError != null && districts.isEmpty() && selectedProvince != null) {
                            Text(
                                text = "Failed to load districts",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }

                    Column {
                        ExposedDropdownMenuBox(
                            expanded = wardExpanded,
                            onExpandedChange = { 
                                if (selectedDistrict != null && !isLoadingWards) {
                                    wardExpanded = it
                                }
                            }
                        ) {
                            OutlinedTextField(
                                value = selectedWard?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                enabled = selectedDistrict != null && !isLoadingWards,
                                label = { 
                                    Text(
                                        if (isLoadingWards) "Loading wards..." 
                                        else "Ward"
                                    ) 
                                },
                                trailingIcon = {
                                    if (isLoadingWards) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = wardExpanded)
                                    }
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = wardExpanded,
                                onDismissRequest = { wardExpanded = false }
                            ) {
                                wards.forEach { w ->
                                    DropdownMenuItem(
                                        text = { Text(w.name) },
                                        onClick = {
                                            selectedWard = w
                                            wardExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                        
                        if (wardsError != null && wards.isEmpty() && selectedDistrict != null) {
                            Text(
                                text = "Failed to load wards",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
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
                    if (selectedProvince == null || selectedDistrict == null || selectedWard == null || detailAddress.isBlank()) {
                        showMissingDialog = true
                    } else {
                        val full = "$detailAddress, ${selectedWard?.name}, ${selectedDistrict?.name}, ${selectedProvince?.name}"
                        navController.previousBackStackEntry?.savedStateHandle?.set("selected_address", full)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Use this address", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium)
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


