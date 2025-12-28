package com.example.thecodecup.api

import android.util.Log
import com.example.thecodecup.model.DistrictResponse
import com.example.thecodecup.model.ProvinceResponse
import com.example.thecodecup.model.WardResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * API service for fetching Vietnamese province, district, and ward data
 * from VNAppMob API v2 using HttpURLConnection
 */
object ProvinceApi {
    // Add trailing slash to avoid 308 redirect to HTTP
    private const val BASE_URL = "https://vapi.vnappmob.com/api/v2/province/"
    private const val TAG = "ProvinceApi"
    private val gson = Gson()

    /**
     * Fetch all provinces in Vietnam
     */
    suspend fun getProvinces(): Result<ProvinceResponse> = withContext(Dispatchers.IO) {
        try {
            val url = URL(BASE_URL)
            val response = performGetRequest(url)
            val provinceResponse = gson.fromJson(response, ProvinceResponse::class.java)
            Result.success(provinceResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch districts for a specific province
     * @param provinceId The ID of the province
     */
    suspend fun getDistricts(provinceId: String): Result<DistrictResponse> = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BASE_URL}district/$provinceId")
            val response = performGetRequest(url)
            val districtResponse = gson.fromJson(response, DistrictResponse::class.java)
            Result.success(districtResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch wards for a specific district
     * @param districtId The ID of the district
     */
    suspend fun getWards(districtId: String): Result<WardResponse> = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BASE_URL}ward/$districtId")
            val response = performGetRequest(url)
            val wardResponse = gson.fromJson(response, WardResponse::class.java)
            Result.success(wardResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Perform HTTP GET request and return response as string
     */
    private fun performGetRequest(url: URL): String {
        Log.d(TAG, "Requesting URL: $url")
        var connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("User-Agent", "TheCodeCup-Android")
            connection.instanceFollowRedirects = true
            
            // Handle redirects manually for cross-protocol redirects (HTTP -> HTTPS)
            var responseCode = connection.responseCode
            Log.d(TAG, "Response code: $responseCode")
            
            // Check for redirect codes (301, 302, 303, 307, 308)
            if (responseCode in 301..308) {
                val newUrl = connection.getHeaderField("Location")
                Log.d(TAG, "Redirecting to: $newUrl")
                connection.disconnect()
                
                if (newUrl != null) {
                    connection = URL(newUrl).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 15000
                    connection.readTimeout = 15000
                    connection.setRequestProperty("Accept", "application/json")
                    connection.setRequestProperty("User-Agent", "TheCodeCup-Android")
                    responseCode = connection.responseCode
                    Log.d(TAG, "New response code: $responseCode")
                }
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                val errorMsg = "HTTP error code: $responseCode"
                Log.e(TAG, errorMsg)
                throw Exception(errorMsg)
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            Log.d(TAG, "Response received: ${response.length} characters")
            return response.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error performing request: ${e.message}", e)
            throw e
        } finally {
            connection.disconnect()
        }
    }
}

