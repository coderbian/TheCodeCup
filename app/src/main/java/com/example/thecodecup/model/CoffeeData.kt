package com.example.thecodecup.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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

// Định nghĩa cấu trúc reward history entry
data class RewardHistory(
    val id: String,
    val coffeeName: String,
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

    // Loyalty stamps (0-8)
    var loyaltyStamps = mutableStateOf(0)

    // Total reward points
    var totalPoints = mutableStateOf(0)

    // Reward history
    val rewardHistory = mutableStateListOf<RewardHistory>()

    // Redeemable items
    val redeemableItems = listOf(
        RedeemableItem("1", "Cafe Latte", 180, "04.07.21"),
        RedeemableItem("2", "Flat White", 180, "04.07.21"),
        RedeemableItem("3", "Cappuccino", 180, "04.07.21")
    )

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
            val oldStatus = order.status
            orders[orderIndex] = order.copy(status = status)
            
            // If order is being completed, add rewards
            if (oldStatus == OrderStatus.ONGOING && status == OrderStatus.COMPLETED) {
                // Increment loyalty stamps
                incrementLoyaltyStamps()
                // Add reward points and history
                addRewardPoints(order)
            }
        }
    }

    // Helper function to format date/time
    fun formatOrderDateTime(): String {
        val dateFormat = SimpleDateFormat("dd MMMM | hh:mm a", Locale.ENGLISH)
        return dateFormat.format(Date())
    }

    // Increment loyalty stamps when order is completed
    fun incrementLoyaltyStamps() {
        if (loyaltyStamps.value < 8) {
            loyaltyStamps.value = loyaltyStamps.value + 1
        }
    }

    // Reset loyalty stamps when reaching 8
    fun resetLoyaltyStamps() {
        loyaltyStamps.value = 0
    }

    // Add reward points and history when order is completed
    fun addRewardPoints(order: Order) {
        // Award 12 points per order (as shown in Figma)
        val pointsEarned = 12
        totalPoints.value = totalPoints.value + pointsEarned

        // Add to reward history (use first coffee name from order)
        val coffeeName = order.items.firstOrNull()?.coffee?.name ?: "Order"
        val historyEntry = RewardHistory(
            id = UUID.randomUUID().toString(),
            coffeeName = coffeeName,
            points = pointsEarned,
            dateTime = order.dateTime
        )
        rewardHistory.add(historyEntry)
    }

    // Redeem points for an item
    fun redeemPoints(item: RedeemableItem): Boolean {
        if (totalPoints.value >= item.pointsRequired) {
            totalPoints.value = totalPoints.value - item.pointsRequired
            return true
        }
        return false
    }
}