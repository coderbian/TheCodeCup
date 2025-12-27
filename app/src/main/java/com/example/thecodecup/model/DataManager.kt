package com.example.thecodecup.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.thecodecup.data.AppDataStore
import com.example.thecodecup.data.PersistedAppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

// Singleton quản lý dữ liệu (Giả lập Database)
object DataManager {
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var store: AppDataStore? = null
    private var hasInitialized = false
    private val orderSimulationJobs = mutableMapOf<String, Job>()

    /**
     * In-app delivery events (orderId). MyOrdersScreen can collect this and show a Snackbar.
     */
    val orderDeliveredEvents = MutableSharedFlow<String>(extraBufferCapacity = 16)

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
        RedeemableItem("1", "Americano", 180, "05.10.25"),
        RedeemableItem("2", "Cappuccino", 180, "05.10.25"),
        RedeemableItem("3", "Mocha", 180, "05.10.25"),
        RedeemableItem("4", "Flat White", 180, "05.10.25"),
        RedeemableItem("5", "Espresso", 180, "05.10.25"),
        RedeemableItem("6", "Latte", 180, "05.10.25"),
        RedeemableItem("7", "Macchiato", 180, "05.10.25"),
        RedeemableItem("8", "Affogato", 180, "05.10.25")
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
        // Merge same item (same coffee + options) instead of creating duplicate rows
        val existingIndex = cart.indexOfFirst {
            it.coffee.id == item.coffee.id &&
                it.size == item.size &&
                it.ice == item.ice &&
                it.shot == item.shot
        }
        if (existingIndex != -1) {
            val existing = cart[existingIndex]
            cart[existingIndex] = existing.copy(
                quantity = existing.quantity + item.quantity,
                totalPrice = existing.totalPrice + item.totalPrice
            )
        } else {
            cart.add(item)
        }
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
        startOrderSimulationIfNeeded(order.id)
    }

    fun getOngoingOrders(): List<Order> {
        return orders.filter { it.status == OrderStatus.ONGOING || it.status == OrderStatus.DELIVERED }
    }

    fun getCompletedOrders(): List<Order> {
        return orders.filter { it.status == OrderStatus.COMPLETED }
    }

    fun getWaitingPickupOrders(): List<Order> {
        return orders.filter { it.status == OrderStatus.WAITING_PICKUP }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        val orderIndex = orders.indexOfFirst { it.id == orderId }
        if (orderIndex != -1) {
            val order = orders[orderIndex]
            val oldStatus = order.status
            orders[orderIndex] = order.copy(status = status)
            
            // If order is being completed, add rewards
            if (oldStatus == OrderStatus.DELIVERED && status == OrderStatus.COMPLETED) {
                // Increment loyalty stamps
                incrementLoyaltyStamps()
                // Add reward points and history
                addRewardPoints(order)
            }
            persistAsync()
        }
    }

    /**
     * Only allow confirmation when the order has reached DELIVERED.
     */
    fun confirmDelivered(orderId: String): Boolean {
        val orderIndex = orders.indexOfFirst { it.id == orderId }
        if (orderIndex == -1) return false
        val order = orders[orderIndex]
        if (order.status != OrderStatus.DELIVERED) return false
        updateOrderStatus(orderId, OrderStatus.COMPLETED)
        // cleanup any timers
        orderSimulationJobs.remove(orderId)?.cancel()
        return true
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

    private fun startOrderSimulationIfNeeded(orderId: String) {
        val existing = orderSimulationJobs[orderId]
        if (existing?.isActive == true) return

        val order = orders.firstOrNull { it.id == orderId } ?: return
        if (order.status != OrderStatus.WAITING_PICKUP) return

        orderSimulationJobs[orderId]?.cancel()
        orderSimulationJobs[orderId] = ioScope.launch {
            // After 2s -> ONGOING
            delay(2000)
            withContext(Dispatchers.Main) {
                val idx = orders.indexOfFirst { it.id == orderId }
                if (idx != -1 && orders[idx].status == OrderStatus.WAITING_PICKUP) {
                    orders[idx] = orders[idx].copy(status = OrderStatus.ONGOING)
                    persistAsync()
                }
            }

            // After 3s more -> DELIVERED (ready to confirm)
            delay(3000)
            withContext(Dispatchers.Main) {
                val idx = orders.indexOfFirst { it.id == orderId }
                if (idx != -1 && orders[idx].status == OrderStatus.ONGOING) {
                    orders[idx] = orders[idx].copy(status = OrderStatus.DELIVERED)
                    persistAsync()
                    orderDeliveredEvents.tryEmit(orderId)
                }
            }
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

