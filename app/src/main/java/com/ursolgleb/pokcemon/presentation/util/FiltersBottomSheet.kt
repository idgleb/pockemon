package com.ursolgleb.pokcemon.presentation.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.ursolgleb.pockemon.R

class FiltersBottomSheet(
	private val context: Context,
	private val currentOrder: String,
	private val preselectedTypes: List<String>,
	private val onApply: (selectedTypes: List<String>, order: String) -> Unit,
	private val onReset: () -> Unit
) {

	fun show() {
		val dialog = BottomSheetDialog(context)
		val view = LayoutInflater.from(context).inflate(R.layout.bottomsheet_filters, null)
		dialog.setContentView(view)

		val orderGroup = view.findViewById<ChipGroup>(R.id.chipGroupOrder)
		val typesGroup = view.findViewById<ChipGroup>(R.id.chipGroupTypes)
		val btnApply = view.findViewById<android.widget.Button>(R.id.btnApply)
		val btnReset = view.findViewById<android.widget.Button>(R.id.btnReset)

		val types = listOf("normal","fire","water","electric","grass","ice","fighting","poison","ground","flying","psychic","bug","rock","ghost","dragon","dark","steel","fairy")
		typesGroup.isSingleSelection = false
		typesGroup.isSelectionRequired = false
		types.forEach { t ->
			val chip = Chip(context).apply {
				text = t
				isCheckable = true
				checkedIcon = null
				val checkedColor = colorForType(t)
				val uncheckedColor = UiConstants.FILTER_UNCHECKED_BG_COLOR
				val bgStates = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))
				val bgColors = intArrayOf(checkedColor, uncheckedColor)
				chipBackgroundColor = android.content.res.ColorStateList(bgStates, bgColors)
				val txtColors = intArrayOf(android.graphics.Color.WHITE, android.graphics.Color.WHITE)
				setTextColor(android.content.res.ColorStateList(bgStates, txtColors))
			}
			typesGroup.addView(chip)
		}

		when (currentOrder) {
			"name" -> orderGroup.check(R.id.chipOrderName)
			"hp" -> orderGroup.check(R.id.chipOrderHp)
			"attack" -> orderGroup.check(R.id.chipOrderAttack)
			"defense" -> orderGroup.check(R.id.chipOrderDefense)
			else -> orderGroup.check(R.id.chipOrderNumber)
		}
		if (preselectedTypes.isNotEmpty()) {
			for (i in 0 until typesGroup.childCount) {
				val chip = typesGroup.getChildAt(i) as? Chip
				if (chip != null && preselectedTypes.any { it.equals(chip.text.toString(), ignoreCase = true) }) {
					chip.isChecked = true
				}
			}
		}

		btnApply.setOnClickListener {
			val selectedTypes = typesGroup.checkedChipIds.mapNotNull { id ->
				typesGroup.findViewById<Chip>(id)?.text?.toString()
			}
			val order = when (orderGroup.checkedChipId) {
				R.id.chipOrderName -> "name"
				R.id.chipOrderHp -> "hp"
				R.id.chipOrderAttack -> "attack"
				R.id.chipOrderDefense -> "defense"
				else -> "number"
			}
			onApply(selectedTypes, order)
			dialog.dismiss()
		}

		btnReset.setOnClickListener {
			onReset()
			dialog.dismiss()
		}

		dialog.show()
	}
}


