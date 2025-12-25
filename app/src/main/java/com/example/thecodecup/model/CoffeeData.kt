package com.example.thecodecup.model

import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.*

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

// Trạng thái đơn hàng
enum class OrderStatus {
    ONGOING,
    COMPLETED
}

// Định nghĩa cấu trúc đơn hàng
data class Order(
    val id: String,
    val dateTime: String,
    val items: List<CartItem>,
    val totalPrice: Double,
    val status: OrderStatus,
    val address: String = "3 Addersion Court Chino Hills, HO56824, United State"
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

    // Danh sách đơn hàng
    val orders = mutableStateListOf<Order>()

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

    fun addOrder(order: Order) {
        orders.add(order)
    }

    fun getOngoingOrders(): List<Order> {
        return orders.filter { it.status == OrderStatus.ONGOING }
    }

    fun getCompletedOrders(): List<Order> {
        return orders.filter { it.status == OrderStatus.COMPLETED }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        val orderIndex = orders.indexOfFirst { it.id == orderId }
        if (orderIndex != -1) {
            val order = orders[orderIndex]
            orders[orderIndex] = order.copy(status = status)
        }
    }

    // Helper function to format date/time
    fun formatOrderDateTime(): String {
        val dateFormat = SimpleDateFormat("dd MMMM | hh:mm a", Locale.ENGLISH)
        return dateFormat.format(Date())
    }
}