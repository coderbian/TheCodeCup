# The Code Cup

The Code Cup is an Android (Jetpack Compose) coffee-shop app built for an academic midterm.  
It supports browsing drinks, customizing options, adding to cart, checkout with shipping + payment,
order tracking with simulated delivery steps, and a rewards/loyalty program.

This repo has grown significantly since the initial version: data is now persisted across app restarts,
the UI is componentized, and dark/light theme switching is applied across the app using Material 3.

---

## Features

### Coffee browsing & details
- 8 drinks in menu: Americano, Cappuccino, Mocha, Flat White, Espresso, Latte, Macchiato, Affogato
- **Search functionality**: Search icon in header to filter drinks by name
- Details customization:
  - Size: S / M / L
  - Shot: Single / Double
  - Hot / Cold select
  - Ice level icons (ice_1/2/3)
  - Quantity selector
- Dynamic price calculation based on selections

### Cart
- Add-to-cart from Details
- **Auto-merge duplicated items**:
  - If you add the same drink with the same options (size/shot/ice), it increases quantity instead of creating another row
- Swipe-to-delete gesture
- Total price calculation

### Checkout (Shipping + Payment + Vouchers) ✨
- Shipping info:
  - Receiver name + phone
  - Shipping address
  - "Change" opens Address Picker (real VN address API)
- Payment method:
  - Cash
  - Bank transfer
  - Card
- **Voucher/Coupon System**:
  - Select voucher from available active vouchers
  - Enter promo code to redeem new vouchers
  - Apply percentage-based discounts to order total
  - Conditional vouchers (e.g., minimum order quantity required)
  - Automatic discount calculation and display
  - Voucher validation before order placement

### Address Picker (Real API Integration) ✨
- **Live data from VNAppMob Province API v2**
- Cascading dropdowns: Province → District → Ward + detail street
- Features:
  - Real-time data fetching from `vapi.vnappmob.com`
  - 63 provinces/cities in Vietnam
  - Dynamic district loading based on selected province
  - Dynamic ward loading based on selected district
  - Material Design 3 `ExposedDropdownMenuBox` with smooth animations
  - Loading indicators while fetching data
  - Error handling with retry functionality
  - Network security config for API compatibility

### Orders (3 stages) ✨
- Tabs: Waiting / On going / History
- Order simulation (foreground-only for simplicity):
  - Waiting pickup → after 5s → On going
  - On going → after 10s → Delivered (ready to confirm)
- **Real-time UI updates**: Screen automatically refreshes when order status changes
- **Android System Notifications**:
  - System notification appears when order is delivered (if notifications enabled)
  - Clicking notification navigates directly to "On going" tab
  - Requires notification permission on Android 13+ (API 33+)
- **Order Confirmation**:
  - Confirmation dialog before confirming receipt
  - Success snackbar notification after confirmation
  - User can only confirm receipt when status is Delivered
- History styling:
  - Completed orders are displayed with lighter/muted text

### Rewards & loyalty
- Loyalty stamps:
  - 8 stamps system
  - Stamps increase when user confirms receipt (delivered → completed)
- Reward points:
  - Points calculated based on quantity of drinks in the order
- Reward history:
  - Shows: drink name + quantity, points earned, date/time
- Redeem:
  - **Unified Redeem Screen**: Toggle between "Drinks" and "Vouchers"
  - Redeem drinks: Uses real drink images, buttons disabled if not enough points
  - Redeem vouchers: Exchange points for discount vouchers (10%, 20%, 50% off)
  - Vouchers have expiry dates and validity periods

### Profile
- Editable user profile:
  - Full name, phone, email, address
- Used to pre-fill Checkout shipping fields (default address)
- Quick access to "My Vouchers" screen

### Voucher/Coupon System ✨
- **My Vouchers Screen**:
  - View all active vouchers with details (code, discount %, expiry date)
  - Enter promo codes to redeem new vouchers
  - Navigate to redeem vouchers with points
  - Empty state message when no vouchers available
- **Voucher Types**:
  - Percentage-based discounts (10%, 20%, 50%, etc.)
  - Conditional vouchers with minimum order quantity requirements
  - Single-use vouchers (removed after checkout)
- **Voucher Sources**:
  - **Redeemed**: Exchanged with reward points
  - **Admin Gift**: Default vouchers given on first app install
  - **Promo Code**: Entered via promo code dialog
- **Default Vouchers** (on first install):
  - Welcome 50% Off (30 days validity)
  - Group Order 20% Off (requires ≥3 items, 60 days validity)
  - First Order 15% Off (90 days validity)
- **Voucher Management**:
  - Automatic expiry date checking
  - Status tracking (ACTIVE, USED, EXPIRED)
  - Conditional application (validates minimum order quantity)
  - Voucher picker at checkout filters applicable vouchers
- **Redeem Voucher Screen**:
  - Unified interface for redeeming drinks and vouchers
  - Toggle between "Drinks" and "Vouchers" tabs
  - Shows points required and validity period
  - Disabled state when insufficient points

### Settings
- Dark/Light toggle (custom UI switch + icon changes)
  - Uses `moon.xml` and `sun.xml` icons (theme-dependent)
- Notifications toggle (in-app flag)
  - Controls whether Android system notifications are shown for delivered orders
  - Requires notification permission to be effective
- Clear all data (manual reset)
  - Confirm dialog required
  - Clears: cart, orders, rewards, profile, settings persisted state
  - Default vouchers are automatically re-added after clearing

### Theme / Dark mode
- Global Material 3 theme with Light + Dark schemes
- Refactored screens/components to use:
  - `MaterialTheme.colorScheme.background/surface/onSurface/...`
  - avoids hard-coded `Color.White` style values

## Tech Stack

- Kotlin
- Jetpack Compose (Material 3)
- Navigation Compose
- Networking:
  - HttpURLConnection (native Android)
  - Gson (JSON parsing)
  - Coroutines (async API calls)
- Data persistence:
  - DataStore Preferences
  - Gson (JSON serialization)

## Screens & Navigation

Routes are defined in:
- `app/src/main/java/com/example/thecodecup/Screens.kt`

NavHost setup in:
- `app/src/main/java/com/example/thecodecup/MainActivity.kt`

Main screens: Splash, Home, Details, Cart, Checkout, Address Picker, Order Success, My Orders, Rewards, Redeem Voucher, My Vouchers, Profile, Settings.

## API Integration (Vietnamese Address)

### VNAppMob Province API v2
- **Endpoint:** `https://vapi.vnappmob.com/api/v2/province/`
- **Documentation:** [vnappmob.com](https://vapi.vnappmob.com/province.v2.html)

### Implementation Details
- **HTTP Client:** Native `HttpURLConnection` (no external dependencies)
- **JSON Parsing:** Gson
- **State Management:** Singleton `AddressManager` (follows `DataManager` pattern)
- **Async:** Kotlin Coroutines with `Dispatchers.IO`

### API Endpoints Used
1. `GET /api/v2/province/` - Fetch all 63 provinces/cities
2. `GET /api/v2/province/district/{province_id}` - Fetch districts by province
3. `GET /api/v2/province/ward/{district_id}` - Fetch wards by district

### Features
- Automatic redirect handling (HTTP 308 → HTTPS)
- Network security configuration for cleartext traffic compatibility
- Loading states with progress indicators
- Error handling with retry functionality
- Cached data to avoid redundant API calls
- Logging for debugging (`ProvinceApi` and `AddressManager` tags)

### Files
- `app/src/main/java/com/example/thecodecup/api/ProvinceApi.kt` - API client
- `app/src/main/java/com/example/thecodecup/model/AddressModels.kt` - Data models
- `app/src/main/java/com/example/thecodecup/model/AddressManager.kt` - State manager
- `app/src/main/res/xml/network_security_config.xml` - Network config

## Data Persistence (DataStore)

Why DataStore:
- Very lightweight for midterm requirements
- No DB schema/migrations needed
- Works well with a single persisted snapshot pattern

Implementation:
- `PersistedAppState` (single state object)
- Stored as JSON string in DataStore Preferences
- `DataManager.persistAsync()` saves after important mutations

Key behavior:
- App restart keeps all data
- Data is only cleared manually through Settings → Clear all data
- Default vouchers are automatically added on first app install
- Vouchers are persisted and restored across app restarts

Files:
- `app/src/main/java/com/example/thecodecup/data/PersistedAppState.kt`
- `app/src/main/java/com/example/thecodecup/data/AppDataStore.kt`

## Rewards & Loyalty (Rules)

- Loyalty stamps:
  - increment when user confirms receipt for an order
- Reward points:
  - calculated using the total quantity of items in the order
- Reward history:
  - one entry per coffee type in the order (with its quantity)

## Orders: 3-Stage Simulation + Confirmation

Statuses:
- WAITING_PICKUP
- ONGOING
- DELIVERED
- COMPLETED (History)

Timing (foreground-only):
- +5s: WAITING_PICKUP → ONGOING
- +10s: ONGOING → DELIVERED

User action:
- Only DELIVERED orders can be confirmed (DELIVERED → COMPLETED)
- Confirmation dialog appears before confirming
- Success snackbar shown after successful confirmation

Notifications:
- **Android System Notification**: Shown when order reaches DELIVERED (if notifications enabled)
- Notification opens app and navigates to "On going" tab
- Requires `POST_NOTIFICATIONS` permission on Android 13+ (API 33+)
- Permission is requested automatically on app launch

UI Auto-refresh:
- Screen automatically updates when order status changes
- No need to navigate away and back to see updates
- Progress indicators and status text update in real-time

## Assets (Icons/Images)

Coffee images (PNG): `americano`, `cappuccino`, `mocha`, `flat_white`, `expresso`, `latte`, `macchiato`, `affogato`.

UI icons (VectorDrawable XML):
- Bottom nav: `store_icon.xml`, `gift_icon.xml`, `bill_icon.xml`, `settings_icon.xml`, `profile_icon.xml`
- Theme: `moon.xml`, `sun.xml`
- Details: `cup_size.xml`, `hot_icon.xml`, `cold_icon.xml`, `ice_1.xml`, `ice_2.xml`, `ice_3.xml`
- Search: `search_icon.xml` (for drink search functionality)

Splash background:
- `landing.jpeg`

Order success image:
- `take_away.png`
  - Note: PNG tinting is limited; use `ColorFilter.tint` for simple overlay tint or convert to VectorDrawable if needed

## Project Structure

```
app/src/main/java/com/example/thecodecup/
├── MainActivity.kt
├── Screens.kt
├── api/
│   └── ProvinceApi.kt          # VN address API client
├── data/
│   ├── AppDataStore.kt
│   └── PersistedAppState.kt
├── model/
│   ├── AddressModels.kt        # Province, District, Ward models
│   ├── AddressManager.kt       # Address state management
│   ├── Coffee.kt
│   ├── DataManager.kt
│   ├── Order.kt
│   ├── Reward.kt
│   ├── User.kt
│   └── Voucher.kt              # Voucher models (Voucher, RedeemableVoucher, PromoCodeTemplate)
├── screens/
│   ├── AddressPickerScreen.kt
│   ├── CartScreen.kt
│   ├── CheckoutScreen.kt
│   ├── DetailsScreen.kt
│   ├── HomeScreen.kt
│   ├── MyOrdersScreen.kt
│   ├── MyVouchersScreen.kt     # Voucher management screen
│   ├── OrderSuccessScreen.kt
│   ├── ProfileScreen.kt
│   ├── RedeemScreen.kt         # Deprecated (redirects to RedeemVoucherScreen)
│   ├── RedeemVoucherScreen.kt  # Unified redeem screen (drinks + vouchers)
│   ├── RewardsScreen.kt
│   ├── SettingsScreen.kt
│   └── SplashScreen.kt
├── ui/
│   ├── components/
│   │   ├── BottomNavBar.kt
│   │   ├── CoffeeCard.kt
│   │   ├── LoyaltyCard.kt
│   │   ├── PromoCodeDialog.kt   # Promo code input dialog
│   │   ├── VoucherCard.kt       # Voucher display card
│   │   └── VoucherPickerSheet.kt # Bottom sheet for voucher selection at checkout
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── utils/
│       └── ImageUtils.kt
└── utils/
    └── NotificationManager.kt   # Android system notification manager

app/src/main/res/xml/
└── network_security_config.xml  # Network security for API
```

## Build & Run

Requirements:
- Android Studio (Giraffe+ recommended)
- Android device/emulator (minSdk 24)
- **Internet connection** (for address picker API)

Steps:
1. Open project in Android Studio
2. Sync Gradle
3. Run on emulator/device

Permissions:
- `INTERNET` - Required for fetching Vietnamese address data from API
- `POST_NOTIFICATIONS` - Required for Android system notifications (Android 13+, API 33+)
  - Permission is requested automatically on app launch
  - Notifications are only shown if permission is granted and enabled in settings

## Troubleshooting

### 1) Kotlin daemon / compile daemon errors
- Try: **Invalidate Caches / Restart**
- Ensure Gradle sync finished successfully

### 2) Dark mode doesn't change some screens
- Screen is likely still using hard-coded colors
- Fix by using `MaterialTheme.colorScheme.*` instead of constants like `Color.White`

### 3) Icons don't tint
- VectorDrawable XML can be tinted easily
- PNG tint is limited; use `ColorFilter.tint` or convert to vector

### 4) Address Picker shows "Failed to load provinces"
- Check internet connection
- Verify emulator/device has network access
- API endpoint: `https://vapi.vnappmob.com/api/v2/province/`
- Check Logcat for detailed error messages (filter by `ProvinceApi` or `AddressManager`)

### 5) HTTP 308 or Cleartext traffic errors
- Already handled via `network_security_config.xml`
- Ensures compatibility with API redirects

### 6) Notifications not showing
- Check if notification permission is granted (Android 13+)
- Verify notifications are enabled in Settings
- Ensure app is not in battery optimization mode
- Check notification channel is created (done automatically on app launch)

### 7) Screen not refreshing when order status changes
- Screen uses `derivedStateOf` to observe order status changes
- If issues persist, try navigating away and back to the screen
- Ensure app is in foreground (simulation only works when app is active)

## Roadmap (Optional / Future)

- Persist timers more accurately (background scheduling / WorkManager)
- Migrate persistence from JSON snapshot → Room for orders/cart tables
- Add search/filter + favorites
- Improve redemption UX (snackbar feedback, redeemed history)
- Add address caching to reduce API calls
- Implement offline mode with cached addresses

## API Credits

- **Vietnamese Address Data:** [VNAppMob Province API v2](https://vapi.vnappmob.com/province.v2.html)
  - Free, open API for Vietnamese administrative divisions
  - Provides real-time data for 63 provinces, districts, and wards

## Credits

- Built with Kotlin + Jetpack Compose (Material 3)
- Design guided by teacher/Figma requirements (midterm project)
- Vietnamese address data powered by VNAppMob
