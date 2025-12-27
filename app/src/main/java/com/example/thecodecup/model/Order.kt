package com.example.thecodecup.model

// Trạng thái đơn hàng
enum class OrderStatus {
    WAITING_PICKUP,
    ONGOING,
    DELIVERED,
    COMPLETED
}

enum class PaymentMethod {
    CASH,
    BANK_TRANSFER,
    CARD
}

// Định nghĩa cấu trúc đơn hàng
data class Order(
    val id: String,
    val dateTime: String,
    val items: List<CartItem>,
    val totalPrice: Double,
    val status: OrderStatus,
    val receiverName: String = "",
    val receiverPhone: String = "",
    val shippingAddress: String = "227 Nguyen Van Cu Street, Cho Quan Ward, Ho Chi Minh City",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH
)

