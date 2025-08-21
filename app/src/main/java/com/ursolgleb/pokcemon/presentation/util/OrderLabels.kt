package com.ursolgleb.pokcemon.presentation.util

import android.content.Context
import com.ursolgleb.pockemon.R

fun orderLabel(context: Context, value: String): String = when (value) {
	"name" -> context.getString(R.string.order_name)
	"hp" -> context.getString(R.string.order_hp)
	"attack" -> context.getString(R.string.order_attack)
	"defense" -> context.getString(R.string.order_defense)
	else -> context.getString(R.string.order_number)
}


