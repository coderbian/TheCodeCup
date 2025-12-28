package com.example.thecodecup.model

// Định nghĩa cấu trúc reward history entry
data class RewardHistory(
    val id: String,
    val coffeeName: String,
    val quantity: Int,
    val points: Int,
    val dateTime: String
)

// Định nghĩa cấu trúc redeemable item
data class RedeemableItem(
    val id: String,
    val name: String,
    val pointsRequired: Int,
    val validUntil: String
)

