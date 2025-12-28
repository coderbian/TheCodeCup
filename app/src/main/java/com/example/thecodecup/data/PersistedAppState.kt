package com.example.thecodecup.data

import com.example.thecodecup.model.CartItem
import com.example.thecodecup.model.Order
import com.example.thecodecup.model.RewardHistory
import com.example.thecodecup.model.UserProfile
import com.example.thecodecup.model.Voucher
import com.example.thecodecup.model.VoucherSource
import com.example.thecodecup.model.VoucherStatus
import com.example.thecodecup.model.VoucherType
import java.text.SimpleDateFormat
import java.util.*

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
    val myVouchers: List<Voucher> = getDefaultVouchers(),
    val userProfile: UserProfile = UserProfile(
        fullName = "Trần Minh Hiếu Học",
        phoneNumber = "+84348567062",
        email = "tranminhhieuhoc@gmail.com",
        address = "Thành phố Hồ Chí Minh, Quận 5, Phường 4, 227 Nguyễn Văn Cừ"
    ),
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true
)

/**
 * Default vouchers that users get when they first install the app
 */
private fun getDefaultVouchers(): List<Voucher> {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    // Voucher giảm 50% - hết hạn sau 30 ngày
    calendar.add(Calendar.DAY_OF_YEAR, 30)
    val expiry50 = dateFormat.format(calendar.time)
    calendar.time = Date() // Reset
    
    // Voucher nhóm >= 3 ly giảm 20% - hết hạn sau 60 ngày
    calendar.add(Calendar.DAY_OF_YEAR, 60)
    val expiry20 = dateFormat.format(calendar.time)
    calendar.time = Date() // Reset
    
    // Voucher chào mừng 15% - hết hạn sau 90 ngày
    calendar.add(Calendar.DAY_OF_YEAR, 90)
    val expiry15 = dateFormat.format(calendar.time)
    
    return listOf(
        Voucher(
            id = "default_voucher_1",
            code = "WELCOME50",
            name = "Welcome 50% Off",
            description = "Giảm 50% cho đơn hàng đầu tiên",
            type = VoucherType.PERCENTAGE,
            discountPercent = 50,
            expiryDate = expiry50,
            source = VoucherSource.ADMIN_GIFT,
            status = VoucherStatus.ACTIVE,
            minOrderQuantity = null
        ),
        Voucher(
            id = "default_voucher_2",
            code = "GROUP20",
            name = "Group Order 20% Off",
            description = "Giảm 20% khi mua từ 3 ly trở lên",
            type = VoucherType.PERCENTAGE,
            discountPercent = 20,
            expiryDate = expiry20,
            source = VoucherSource.ADMIN_GIFT,
            status = VoucherStatus.ACTIVE,
            minOrderQuantity = 3
        ),
        Voucher(
            id = "default_voucher_3",
            code = "FIRST15",
            name = "First Order 15% Off",
            description = "Giảm 15% cho đơn hàng đầu tiên",
            type = VoucherType.PERCENTAGE,
            discountPercent = 15,
            expiryDate = expiry15,
            source = VoucherSource.ADMIN_GIFT,
            status = VoucherStatus.ACTIVE,
            minOrderQuantity = null
        )
    )
}


