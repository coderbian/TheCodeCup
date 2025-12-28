package com.example.thecodecup.model

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.thecodecup.api.ProvinceApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Singleton manager for Vietnamese address data
 * Follows the same pattern as DataManager
 */
object AddressManager {
    private const val TAG = "AddressManager"
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Cached data
    val provinces = mutableStateOf<List<Province>>(emptyList())
    val districts = mutableStateOf<List<District>>(emptyList())
    val wards = mutableStateOf<List<Ward>>(emptyList())

    // Loading states
    val isLoadingProvinces = mutableStateOf(false)
    val isLoadingDistricts = mutableStateOf(false)
    val isLoadingWards = mutableStateOf(false)

    // Error states
    val provincesError = mutableStateOf<String?>(null)
    val districtsError = mutableStateOf<String?>(null)
    val wardsError = mutableStateOf<String?>(null)

    /**
     * Load all provinces from API
     */
    fun loadProvinces() {
        if (provinces.value.isNotEmpty()) {
            // Already loaded, skip
            Log.d(TAG, "Provinces already loaded, skipping")
            return
        }

        ioScope.launch {
            Log.d(TAG, "Loading provinces...")
            isLoadingProvinces.value = true
            provincesError.value = null

            val result = ProvinceApi.getProvinces()
            result.fold(
                onSuccess = { response ->
                    Log.d(TAG, "Provinces loaded successfully: ${response.results.size} items")
                    provinces.value = response.results
                    isLoadingProvinces.value = false
                },
                onFailure = { error ->
                    val errorMsg = error.message ?: "Failed to load provinces"
                    Log.e(TAG, "Error loading provinces: $errorMsg", error)
                    provincesError.value = errorMsg
                    isLoadingProvinces.value = false
                }
            )
        }
    }

    /**
     * Load districts for a specific province
     * @param provinceId The ID of the province
     */
    fun loadDistricts(provinceId: String) {
        ioScope.launch {
            isLoadingDistricts.value = true
            districtsError.value = null

            val result = ProvinceApi.getDistricts(provinceId)
            result.fold(
                onSuccess = { response ->
                    districts.value = response.results
                    isLoadingDistricts.value = false
                },
                onFailure = { error ->
                    districtsError.value = error.message ?: "Failed to load districts"
                    isLoadingDistricts.value = false
                }
            )
        }
    }

    /**
     * Load wards for a specific district
     * @param districtId The ID of the district
     */
    fun loadWards(districtId: String) {
        ioScope.launch {
            isLoadingWards.value = true
            wardsError.value = null

            val result = ProvinceApi.getWards(districtId)
            result.fold(
                onSuccess = { response ->
                    wards.value = response.results
                    isLoadingWards.value = false
                },
                onFailure = { error ->
                    wardsError.value = error.message ?: "Failed to load wards"
                    isLoadingWards.value = false
                }
            )
        }
    }

    /**
     * Clear districts and wards when province changes
     */
    fun clearDistricts() {
        districts.value = emptyList()
        districtsError.value = null
    }

    /**
     * Clear wards when district changes
     */
    fun clearWards() {
        wards.value = emptyList()
        wardsError.value = null
    }

    /**
     * Retry loading provinces
     */
    fun retryLoadProvinces() {
        provinces.value = emptyList()
        loadProvinces()
    }
}

