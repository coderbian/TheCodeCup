package com.example.thecodecup.model

import androidx.compose.runtime.mutableStateListOf

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

// Singleton quản lý dữ liệu (Giả lập Database)
object DataManager {
    // Menu Cafe giả
    val menu = listOf(
        Coffee("1", "Americano", 3.0),
        Coffee("2", "Cappuccino", 3.5),
        Coffee("3", "Mocha", 4.0),
        Coffee("4", "Flat White", 3.5)
    )

    // Giỏ hàng (dùng mutableStateListOf để UI tự cập nhật khi list thay đổi)
    val cart = mutableStateListOf<CartItem>()

    fun addToCart(item: CartItem) {
        cart.add(item)
    }

    fun removeFromCart(item: CartItem) {
        cart.remove(item)
    }

    fun getCartTotal(): Double {
        return cart.sumOf { it.totalPrice }
    }

    fun clearCart() {
        cart.clear()
    }
}