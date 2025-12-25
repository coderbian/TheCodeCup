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
        else -> R.drawable.americano // Default fallback
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
        else -> R.drawable.americano // Default fallback
    }
}

