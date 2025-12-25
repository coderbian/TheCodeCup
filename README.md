# The Code Cup

A Jetpack Compose Android app for a fictional coffee shop. Users can browse drinks, view details, customize options, add items to cart, checkout, track their orders, and earn rewards through a loyalty program.

## Tech stack
- Kotlin + Jetpack Compose + Material 3
- Navigation Compose
- Simple in-memory data manager for menu, cart, order history, and rewards system

## Project Structure

```
app/src/main/java/com/example/thecodecup/
├── MainActivity.kt          # Main entry point & NavHost setup
├── Screens.kt              # Navigation routes definition
├── model/
│   └── CoffeeData.kt       # Data models (Coffee, CartItem, Order, RewardHistory, RedeemableItem) & DataManager
├── screens/
│   ├── SplashScreen.kt
│   ├── HomeScreen.kt
│   ├── DetailsScreen.kt
│   ├── CartScreen.kt
│   ├── OrderSuccessScreen.kt
│   ├── MyOrdersScreen.kt
│   ├── RewardsScreen.kt
│   └── RedeemScreen.kt
└── ui/
    └── theme/
        ├── Color.kt        # Theme colors
        ├── Theme.kt        # Material3 theme configuration
        └── Type.kt         # Typography
```

## Screens

### 1. Splash Screen
- App logo and branding
- Auto-navigates to Home after 2 seconds

### 2. Home Screen
- **Header**: Greeting and user info
- **Loyalty Card**: Progress tracking (8 stamps system)
  - Displays current stamps from DataManager (reactive)
  - Clickable to reset when reaching 8 stamps
- **Coffee Grid**: Browse available coffee items
- **Bottom Navigation**: Home, Rewards, My Orders

### 3. Details Screen
- Coffee item details
- **Customization Options**:
  - Size selection (S/M/L)
  - Shot selection (Single/Double)
  - Ice level
  - Quantity selector
- Dynamic price calculation based on selections
- Add to cart functionality

### 4. Cart Screen
- List of cart items with details
- **Swipe-to-delete** gesture for item removal
- Total price calculation
- **Checkout**: Creates order and navigates to Order Success
  - Order is created with ONGOING status

### 5. Order Success Screen
- Confirmation message after successful checkout
- Success icon and description
- **Track My Order** button: Navigates to My Orders screen

### 6. My Orders Screen
- **Tab Navigation**: 
  - "On going": Active orders
  - "History": Completed orders
- **Order List Display**:
  - Date and time
  - Total price
  - Coffee items with icons
  - Delivery address
- **Order Status Transition**: Click on ongoing orders to mark as completed
  - Automatically increments loyalty stamps (+1 per completed order)
  - Automatically awards reward points (+12 points per completed order)
  - Adds entry to reward history
- Bottom navigation bar integration

### 7. Rewards Screen
- **Loyalty Card Section**: 
  - Displays current stamps (0-8) from DataManager
  - Clickable to reset stamps when reaching 8
- **My Points Section**: 
  - Shows total accumulated reward points
  - "Redeem drinks" button navigates to Redeem screen
- **History Rewards Section**: 
  - Lists all reward history entries
  - Shows coffee name, points earned (+12 Pts), and date/time
- Bottom navigation bar integration

### 8. Redeem Screen
- **Redeemable Items List**: 
  - Displays available items for redemption (Cafe Latte, Flat White, Cappuccino)
  - Each item shows: image, name, validity date, and points required (180 pts)
- **Redemption Logic**: 
  - Button enabled only when user has sufficient points
  - Deducts points from total when redeemed
  - Navigates back to Rewards screen after successful redemption

## Data Models

### Coffee
- `id`, `name`, `basePrice`, `description`

### CartItem
- `coffee`, `size`, `ice`, `shot`, `quantity`, `totalPrice`

### Order
- `id`, `dateTime`, `items`, `totalPrice`, `status` (ONGOING/COMPLETED), `address`

### RewardHistory
- `id`, `coffeeName`, `points`, `dateTime`

### RedeemableItem
- `id`, `name`, `pointsRequired`, `validUntil`

### DataManager (Singleton)
- Menu management
- Cart operations (add, remove, clear, get total)
- Order management (add, get ongoing/completed, update status)
- **Rewards System**:
  - Loyalty stamps tracking (0-8, auto-increment on order completion)
  - Total points tracking (auto-increment +12 points per completed order)
  - Reward history management
  - Points redemption (deduct points for redeemable items)
  - Auto-reset stamps when reaching 8 (clickable on card)

## Key Features

- ✅ Coffee browsing and customization
- ✅ Shopping cart with swipe-to-delete
- ✅ Order placement and confirmation
- ✅ Order history with status tracking
- ✅ **Loyalty Card System**:
  - 8-stamp progress tracking
  - Auto-increment on order completion
  - Click to reset when full
- ✅ **Rewards System**:
  - Points earned per completed order (+12 points)
  - Reward history tracking
  - Points redemption for drinks (180 points per item)
  - Redeemable items: Cafe Latte, Flat White, Cappuccino
- ✅ Bottom navigation for main screens (Home, Rewards, My Orders)
- ✅ Material 3 design following Figma specifications

## Build & run
1. Open the project in Android Studio (Giraffe+).
2. Connect a device/emulator running Android 8.0+.
3. Click **Run**. Compose preview works for UI iteration.

## Notes
- Light theme palette follows provided Figma (blue CTAs, dark card panels, coffee accent).
- No backend; data resets when the app restarts (in-memory storage).
- Navigation uses Jetpack Navigation Compose with type-safe routes.
