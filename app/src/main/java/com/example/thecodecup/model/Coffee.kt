package com.example.thecodecup.model

// Định nghĩa cấu trúc 1 món Cafe
data class Coffee(
    val id: String,
    val name: String,
    val basePrice: Double,
    val description: String = "Single | Iced | Medium | Full Ice"
)

// Định nghĩa cấu trúc 1 món trong Giỏ hàng
data class CartItem(
    val coffee: Coffee,
    val size: String, // S, M, L
    val ice: String,  // 1, 2, 3 (viên đá)
    val shot: String, // Single, Double
    val quantity: Int,
    val totalPrice: Double
)

