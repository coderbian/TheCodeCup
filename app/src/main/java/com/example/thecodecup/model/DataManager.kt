package com.example.thecodecup.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.thecodecup.data.AppDataStore
import com.example.thecodecup.data.PersistedAppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

// Singleton quản lý dữ liệu (Giả lập Database)
object DataManager {
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var store: AppDataStore? = null
    private var hasInitialized = false

    private val defaultProfile = UserProfile(
        fullName = "Hieu-Hoc Tran Minh",
        phoneNumber = "+84348567062",
        email = "tranminhhieuhoc@gmail.com",
        address = "227 Nguyen Van Cu Street, Cho Quan Ward, Ho Chi Minh City"
    )

    // Menu Cafe giả
    val menu = listOf(
        Coffee("1", "Americano", 3.0),
        Coffee("2", "Cappuccino", 3.5),
        Coffee("3", "Mocha", 4.0),
        Coffee("4", "Flat White", 3.5),
        Coffee("5", "Espresso", 2.5),
        Coffee("6", "Latte", 4.0),
        Coffee("7", "Macchiato", 3.5),
        Coffee("8", "Affogato", 4.5)
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
        RedeemableItem("1", "Cafe Latte", 180, "04.07.26"),
        RedeemableItem("2", "Flat White", 180, "04.07.26"),
        RedeemableItem("3", "Cappuccino", 180, "04.07.26")
    )

    // User profile
    var userProfile = mutableStateOf(
        defaultProfile
    )

    // Dark mode state
    var isDarkMode = mutableStateOf(false)

    // Settings: notifications
    var notificationsEnabled = mutableStateOf(true)

    fun init(context: Context) {
        if (hasInitialized) return
        hasInitialized = true
        store = AppDataStore(context.applicationContext)
        ioScope.launch {
            val loaded = store?.loadState() ?: return@launch
            withContext(Dispatchers.Main) {
                applyLoadedState(loaded)
            }
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        userProfile.value = profile
        persistAsync()
    }

    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
        persistAsync()
    }

    fun addToCart(item: CartItem) {
        cart.add(item)
        persistAsync()
    }

    fun removeFromCart(item: CartItem) {
        cart.remove(item)
        persistAsync()
    }

    fun getCartTotal(): Double {
        return cart.sumOf { it.totalPrice }
    }

    fun clearCart() {
        cart.clear()
        persistAsync()
    }

    fun addOrder(order: Order) {
        orders.add(order)
        persistAsync()
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
            persistAsync()
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
        persistAsync()
    }

    // Add reward points and history when order is completed
    fun addRewardPoints(order: Order) {
        // Calculate total quantity of all items in the order
        val totalQuantity = order.items.sumOf { it.quantity }
        
        // Award 12 points per item (12 * total quantity)
        val pointsEarned = 12 * totalQuantity
        totalPoints.value = totalPoints.value + pointsEarned

        // Add to reward history - create one entry per unique coffee type
        order.items.forEach { item ->
            val historyEntry = RewardHistory(
                id = UUID.randomUUID().toString(),
                coffeeName = item.coffee.name,
                quantity = item.quantity,
                points = 12 * item.quantity, // Points for this specific item
                dateTime = order.dateTime
            )
            rewardHistory.add(historyEntry)
        }
        persistAsync()
    }

    // Redeem points for an item
    fun redeemPoints(item: RedeemableItem): Boolean {
        if (totalPoints.value >= item.pointsRequired) {
            totalPoints.value = totalPoints.value - item.pointsRequired
            persistAsync()
            return true
        }
        return false
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        notificationsEnabled.value = enabled
        persistAsync()
    }

    fun clearAllData(context: Context) {
        if (store == null) {
            store = AppDataStore(context.applicationContext)
        }
        // Clear in-memory state first for immediate UI update
        cart.clear()
        orders.clear()
        loyaltyStamps.value = 0
        totalPoints.value = 0
        rewardHistory.clear()
        userProfile.value = defaultProfile
        isDarkMode.value = false
        notificationsEnabled.value = true

        ioScope.launch {
            store?.clearAll()
        }
    }

    private fun applyLoadedState(state: PersistedAppState) {
        cart.clear()
        cart.addAll(state.cart)
        orders.clear()
        orders.addAll(state.orders)
        loyaltyStamps.value = state.loyaltyStamps
        totalPoints.value = state.totalPoints
        rewardHistory.clear()
        rewardHistory.addAll(state.rewardHistory)
        userProfile.value = state.userProfile
        isDarkMode.value = state.isDarkMode
        notificationsEnabled.value = state.notificationsEnabled
    }

    private fun persistAsync() {
        val currentStore = store ?: return
        val snapshot = PersistedAppState(
            cart = cart.toList(),
            orders = orders.toList(),
            loyaltyStamps = loyaltyStamps.value,
            totalPoints = totalPoints.value,
            rewardHistory = rewardHistory.toList(),
            userProfile = userProfile.value,
            isDarkMode = isDarkMode.value,
            notificationsEnabled = notificationsEnabled.value
        )
        ioScope.launch {
            currentStore.saveState(snapshot)
        }
    }
}

