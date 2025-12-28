package com.example.thecodecup.model

// Voucher types
enum class VoucherType {
    PERCENTAGE,      // Giảm theo %
    FIXED_AMOUNT     // Reserved for future (not used in v1)
}

enum class VoucherSource {
    REDEEMED,        // Đổi từ points
    ADMIN_GIFT,      // Tặng từ admin
    PROMO_CODE       // Nhập mã
}

enum class VoucherStatus {
    ACTIVE,          // Có thể sử dụng
    USED,            // Đã sử dụng
    EXPIRED          // Hết hạn
}

// Voucher data class
data class Voucher(
    val id: String,
    val code: String,                    // Mã voucher (VD: "COFFEE10", "WELCOME20")
    val name: String,                    // Tên voucher
    val description: String,             // Mô tả
    val type: VoucherType,              // Loại voucher
    val discountPercent: Int,           // % giảm (10, 20, 50, v.v.)
    val expiryDate: String,             // Ngày hết hạn (DD/MM/YYYY)
    val source: VoucherSource,          // Nguồn gốc voucher
    val status: VoucherStatus = VoucherStatus.ACTIVE,
    val usedDate: String? = null,        // Ngày sử dụng
    val minOrderQuantity: Int? = null   // Điều kiện: số lượng tối thiểu (VD: >= 3 ly)
)

// Redeemable voucher (voucher có thể đổi bằng points)
data class RedeemableVoucher(
    val id: String,
    val code: String,
    val name: String,
    val description: String,
    val discountPercent: Int,
    val pointsRequired: Int,            // Số points cần để đổi
    val validDays: Int                  // Số ngày có hiệu lực
)

// Promo code template (mã do admin tạo sẵn)
data class PromoCodeTemplate(
    val code: String,                    // Mã khuyến mãi
    val name: String,
    val description: String,
    val discountPercent: Int,
    val expiryDate: String,
    val usageLimit: Int = 1              // Giới hạn số lần dùng (mỗi user)
)

