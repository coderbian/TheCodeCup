package com.example.thecodecup.model

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

