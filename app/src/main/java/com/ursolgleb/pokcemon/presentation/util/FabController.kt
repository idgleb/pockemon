package com.ursolgleb.pokcemon.presentation.util

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabController(
	private val fab: FloatingActionButton
) {
	fun updateVisibilityByScroll(layoutManager: GridLayoutManager) {
		val shouldShow = layoutManager.findFirstVisibleItemPosition() > UiConstants.FAB_FIRST_VISIBLE_THRESHOLD
		if (shouldShow && fab.visibility != View.VISIBLE) {
			fab.alpha = 0f
			fab.scaleX = 0.9f
			fab.scaleY = 0.9f
			fab.visibility = View.VISIBLE
			fab.animate()
				.alpha(1f)
				.scaleX(1f)
				.scaleY(1f)
				.setDuration(UiConstants.ANIM_SHORT_MS)
				.start()
		} else if (!shouldShow && fab.visibility == View.VISIBLE) {
			hide()
		}
	}

	fun hide() {
		if (fab.visibility == View.VISIBLE) {
			fab.animate()
				.alpha(0f)
				.scaleX(0.9f)
				.scaleY(0.9f)
				.setDuration(UiConstants.ANIM_SHORT_MS)
				.withEndAction { fab.visibility = View.GONE }
				.start()
		}
	}
}


