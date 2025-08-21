package com.ursolgleb.pokcemon.presentation.util

import android.graphics.Color

fun colorForType(type: String): Int {
    return when (type.lowercase()) {
        "normal" -> 0xFFA8A878.toInt()
        "fire" -> 0xFFEE8130.toInt()
        "water" -> 0xFF6390F0.toInt()
        "electric" -> 0xFFF7D02C.toInt()
        "grass" -> 0xFF7AC74C.toInt()
        "ice" -> 0xFF96D9D6.toInt()
        "fighting" -> 0xFFC22E28.toInt()
        "poison" -> 0xFFA33EA1.toInt()
        "ground" -> 0xFFE2BF65.toInt()
        "flying" -> 0xFFA98FF3.toInt()
        "psychic" -> 0xFFF95587.toInt()
        "bug" -> 0xFFA6B91A.toInt()
        "rock" -> 0xFFB6A136.toInt()
        "ghost" -> 0xFF735797.toInt()
        "dragon" -> 0xFF6F35FC.toInt()
        "dark" -> 0xFF705746.toInt()
        "steel" -> 0xFFB7B7CE.toInt()
        "fairy" -> 0xFFD685AD.toInt()
        else -> Color.parseColor("#607D8B")
    }
}


