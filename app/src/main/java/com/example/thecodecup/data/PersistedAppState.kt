package com.example.thecodecup.data

import com.example.thecodecup.model.CartItem
import com.example.thecodecup.model.Order
import com.example.thecodecup.model.RewardHistory
import com.example.thecodecup.model.UserProfile

/**
 * Single persisted snapshot of app state.
 * Stored as JSON in DataStore Preferences for midterm-friendly persistence.
 */
data class PersistedAppState(
    val cart: List<CartItem> = emptyList(),
    val orders: List<Order> = emptyList(),
    val loyaltyStamps: Int = 0,
    val totalPoints: Int = 0,
    val rewardHistory: List<RewardHistory> = emptyList(),
    val userProfile: UserProfile = UserProfile(
        fullName = "Hieu-Hoc Tran Minh",
        phoneNumber = "+84348567062",
        email = "tranminhhieuhoc@gmail.com",
        address = "Thành phố Hồ Chí Minh, Quận 5, Phường 4, 227 Nguyễn Văn Cừ"
    ),
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true
)


