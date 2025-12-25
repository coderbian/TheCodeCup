# The Code Cup

A Jetpack Compose Android app for a fictional coffee shop. Users can browse drinks, view details, customize options, add items to cart, checkout, and track their orders.

## Tech stack
- Kotlin + Jetpack Compose + Material 3
- Navigation Compose
- Simple in-memory data manager for menu, cart, and order history

## Project Structure

```
app/src/main/java/com/example/thecodecup/
├── MainActivity.kt          # Main entry point & NavHost setup
├── Screens.kt              # Navigation routes definition
├── model/
│   └── CoffeeData.kt       # Data models (Coffee, CartItem, Order) & DataManager
├── screens/
│   ├── SplashScreen.kt
│   ├── HomeScreen.kt
│   ├── DetailsScreen.kt
│   ├── CartScreen.kt
│   ├── OrderSuccessScreen.kt
│   └── MyOrdersScreen.kt
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
- **Coffee Grid**: Browse available coffee items
- **Bottom Navigation**: Home, Rewards (placeholder), My Orders

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
- Bottom navigation bar integration

## Data Models

### Coffee
- `id`, `name`, `basePrice`, `description`

### CartItem
- `coffee`, `size`, `ice`, `shot`, `quantity`, `totalPrice`

### Order
- `id`, `dateTime`, `items`, `totalPrice`, `status` (ONGOING/COMPLETED), `address`

### DataManager (Singleton)
- Menu management
- Cart operations (add, remove, clear, get total)
- Order management (add, get ongoing/completed, update status)

## Key Features

- ✅ Coffee browsing and customization
- ✅ Shopping cart with swipe-to-delete
- ✅ Order placement and confirmation
- ✅ Order history with status tracking
- ✅ Loyalty card system
- ✅ Bottom navigation for main screens
- ✅ Material 3 design following Figma specifications

## Build & run
1. Open the project in Android Studio (Giraffe+).
2. Connect a device/emulator running Android 8.0+.
3. Click **Run**. Compose preview works for UI iteration.

## Notes
- Light theme palette follows provided Figma (blue CTAs, dark card panels, coffee accent).
- No backend; data resets when the app restarts (in-memory storage).
- Navigation uses Jetpack Navigation Compose with type-safe routes.
