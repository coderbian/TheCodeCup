package com.example.thecodecup.ui.utils

import com.example.thecodecup.R
import com.example.thecodecup.model.Coffee

/**
 * Maps coffee name to drawable resource ID
 */
fun getCoffeeImageResource(coffee: Coffee): Int {
    return when (coffee.name.lowercase()) {
        "americano" -> R.drawable.americano
        "cappuccino" -> R.drawable.cappuccino
        "mocha" -> R.drawable.mocha
        "flat white" -> R.drawable.flat_white
        "espresso" -> R.drawable.expresso         // Has actual image (note: expresso.png)
        "latte" -> R.drawable.latte               // Has actual image
        "macchiato" -> R.drawable.macchiato       // Has actual image
        "affogato" -> R.drawable.affogato         // Has actual image
        else -> R.drawable.americano              // Default fallback
    }
}

/**
 * Maps coffee name string to drawable resource ID
 */
fun getCoffeeImageResourceByName(name: String): Int {
    return when (name.lowercase()) {
        "americano" -> R.drawable.americano
        "cappuccino" -> R.drawable.cappuccino
        "mocha" -> R.drawable.mocha
        "flat white" -> R.drawable.flat_white
        "espresso" -> R.drawable.expresso         // Has actual image (note: expresso.png)
        "latte" -> R.drawable.latte               // Has actual image
        "macchiato" -> R.drawable.macchiato       // Has actual image
        "affogato" -> R.drawable.affogato         // Has actual image
        else -> R.drawable.americano              // Default fallback
    }
}

