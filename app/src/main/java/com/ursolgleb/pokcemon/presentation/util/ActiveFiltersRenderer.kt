package com.ursolgleb.pokcemon.presentation.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ursolgleb.pockemon.R

fun renderActiveFilters(
	context: Context,
	chipGroup: ChipGroup,
	selectedType: String?,
	selectedTypes: List<String>,
	orderBy: String,
	onApplyFilters: (types: List<String>, order: String) -> Unit,
	onResetFilters: () -> Unit
) {
	chipGroup.removeAllViews()

	if (!selectedType.isNullOrBlank() && selectedTypes.isEmpty()) {
		val chip = Chip(context).apply {
			text = context.getString(R.string.type_label, selectedType)
			isCloseIconVisible = true
			setTextColor(Color.WHITE)
			chipBackgroundColor = ColorStateList.valueOf(colorForType(selectedType))
			setOnCloseIconClickListener {
				onApplyFilters(emptyList(), orderBy)
			}
		}
		chipGroup.addView(chip)
	}

	if (selectedTypes.isNotEmpty()) {
		selectedTypes.forEach { t ->
			val chip = Chip(context).apply {
				text = t.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
				isCloseIconVisible = true
				setTextColor(Color.WHITE)
				chipBackgroundColor = ColorStateList.valueOf(colorForType(t))
				setOnCloseIconClickListener {
					val rest = selectedTypes.filterNot { it.equals(t, true) }
					onApplyFilters(rest, orderBy)
				}
			}
			chipGroup.addView(chip)
		}
	}

	if (orderBy != "number") {
		val chip = Chip(context).apply {
			text = context.getString(R.string.order_label, orderLabel(context, orderBy))
			isCloseIconVisible = true
			setOnCloseIconClickListener {
				onApplyFilters(selectedTypes, "number")
			}
		}
		chipGroup.addView(chip)
	}

	if (chipGroup.childCount > 0) {
		val clearAllChip = Chip(context).apply {
			text = context.getString(R.string.clear_all)
			isCloseIconVisible = true
			setOnCloseIconClickListener { onResetFilters() }
		}
		chipGroup.addView(clearAllChip)
	}
}


