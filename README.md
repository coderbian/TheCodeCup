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

### Checkout (Shipping + Payment)
- Shipping info:
  - Receiver name + phone
  - Shipping address
  - “Change” opens Address Picker (placeholder VN list)
- Payment method:
  - Cash
  - Bank transfer
  - Card

### Address Picker (placeholder)
- Province → District → Ward + detail street
- Uses static sample data now; designed so it can be replaced by a network API later

### Orders (3 stages)
- Tabs: Waiting / On going / History
- Order simulation (foreground-only for simplicity):
  - Waiting pickup → after 2s → On going
  - On going → after 3s → Delivered (ready to confirm)
- In-app notification:
  - Snackbar appears when an order becomes “Delivered”
- Confirmation gating:
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
  - Redeem list uses real drink images
  - Buttons disabled if not enough points

### Profile
- Editable user profile:
  - Full name, phone, email, address
- Used to pre-fill Checkout shipping fields (default address)

### Settings
- Dark/Light toggle (custom UI switch + icon changes)
  - Uses `moon.xml` and `sun.xml` icons (theme-dependent)
- Notifications toggle (in-app flag)
- Clear all data (manual reset)
  - Confirm dialog required
  - Clears: cart, orders, rewards, profile, settings persisted state

### Theme / Dark mode
- Global Material 3 theme with Light + Dark schemes
- Refactored screens/components to use:
  - `MaterialTheme.colorScheme.background/surface/onSurface/...`
  - avoids hard-coded `Color.White` style values

## Tech Stack

- Kotlin
- Jetpack Compose (Material 3)
- Navigation Compose
- Data persistence:
  - DataStore Preferences
  - Gson (JSON serialization)

## Screens & Navigation

Routes are defined in:
- `app/src/main/java/com/example/thecodecup/Screens.kt`

NavHost setup in:
- `app/src/main/java/com/example/thecodecup/MainActivity.kt`

Main screens: Splash, Home, Details, Cart, Checkout, Address Picker, Order Success, My Orders, Rewards, Redeem, Profile, Settings.

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
- +2s: WAITING_PICKUP → ONGOING
- +3s: ONGOING → DELIVERED

User action:
- Only DELIVERED orders can be confirmed (DELIVERED → COMPLETED)

In-app notification:
- Snackbar shown when order reaches DELIVERED

## Assets (Icons/Images)

Coffee images (PNG): `americano`, `cappuccino`, `mocha`, `flat_white`, `expresso`, `latte`, `macchiato`, `affogato`.

UI icons (VectorDrawable XML):
- Bottom nav: `store_icon.xml`, `gift_icon.xml`, `bill_icon.xml`, `settings_icon.xml`, `profile_icon.xml`
- Theme: `moon.xml`, `sun.xml`
- Details: `cup_size.xml`, `hot_icon.xml`, `cold_icon.xml`, `ice_1.xml`, `ice_2.xml`, `ice_3.xml`

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
├── data/
│   ├── AppDataStore.kt
│   └── PersistedAppState.kt
├── model/
│   ├── Coffee.kt
│   ├── DataManager.kt
│   ├── Order.kt
│   ├── Reward.kt
│   └── User.kt
├── screens/
│   ├── AddressPickerScreen.kt
│   ├── CartScreen.kt
│   ├── CheckoutScreen.kt
│   ├── DetailsScreen.kt
│   ├── HomeScreen.kt
│   ├── MyOrdersScreen.kt
│   ├── OrderSuccessScreen.kt
│   ├── ProfileScreen.kt
│   ├── RedeemScreen.kt
│   ├── RewardsScreen.kt
│   ├── SettingsScreen.kt
│   └── SplashScreen.kt
└── ui/
    ├── components/
    │   ├── BottomNavBar.kt
    │   ├── CoffeeCard.kt
    │   └── LoyaltyCard.kt
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    └── utils/
        └── ImageUtils.kt
```

## Build & Run

Requirements:
- Android Studio (Giraffe+ recommended)
- Android device/emulator (minSdk 24)

Steps:
1. Open project in Android Studio
2. Sync Gradle
3. Run on emulator/device

## Troubleshooting

### 1) Kotlin daemon / compile daemon errors
- Try: **Invalidate Caches / Restart**
- Ensure Gradle sync finished successfully

### 2) Dark mode doesn’t change some screens
- Screen is likely still using hard-coded colors
- Fix by using `MaterialTheme.colorScheme.*` instead of constants like `Color.White`

### 3) Icons don’t tint
- VectorDrawable XML can be tinted easily
- PNG tint is limited; use `ColorFilter.tint` or convert to vector

## Roadmap (Optional / Future)

- Replace placeholder address data with a Vietnam provinces API
- Persist timers more accurately (background scheduling / WorkManager)
- Migrate persistence from JSON snapshot → Room for orders/cart tables
- Add search/filter + favorites
- Improve redemption UX (snackbar feedback, redeemed history)

## Credits

- Built with Kotlin + Jetpack Compose (Material 3)
- Design guided by teacher/Figma requirements (midterm project)
