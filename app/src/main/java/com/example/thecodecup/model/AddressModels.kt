package com.example.thecodecup.model

import com.google.gson.annotations.SerializedName

/**
 * Data models for Vietnamese address hierarchy from VNAppMob API v2
 */

data class Province(
    @SerializedName("province_id")
    val id: String,
    @SerializedName("province_name")
    val name: String,
    @SerializedName("province_type")
    val type: String
)

data class District(
    @SerializedName("district_id")
    val id: String,
    @SerializedName("district_name")
    val name: String
)

data class Ward(
    @SerializedName("ward_id")
    val id: String,
    @SerializedName("ward_name")
    val name: String
)

/**
 * API response wrappers
 */
data class ProvinceResponse(
    val results: List<Province>
)

data class DistrictResponse(
    val results: List<District>
)

data class WardResponse(
    val results: List<Ward>
)

