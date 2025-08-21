package com.ursolgleb.pokcemon.presentation.util

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.splashscreen.SplashScreenViewProvider
import com.ursolgleb.pockemon.R

fun animateSplashExit(
	activity: Activity,
	splashView: SplashScreenViewProvider,
	titleText: String = activity.getString(R.string.splash_title),
	titleColor: Int = UiConstants.SPLASH_TITLE_COLOR,
	durationMs: Long = UiConstants.SPLASH_DURATION_MS
) {
	val root = activity.window.decorView as ViewGroup
	val topOffset = (activity.resources.displayMetrics.heightPixels * 0.25f).toInt()
	val title = TextView(activity).apply {
		text = titleText
		setTextColor(titleColor)
		textSize = 48f
		typeface = Typeface.DEFAULT_BOLD
		alpha = 0f
		translationY = -30f
	}
	val lp = FrameLayout.LayoutParams(
		FrameLayout.LayoutParams.WRAP_CONTENT,
		FrameLayout.LayoutParams.WRAP_CONTENT
	).apply {
		gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
		topMargin = topOffset
	}
	root.addView(title, lp)

	title.animate()
		.alpha(1f)
		.translationY(0f)
		.setDuration(durationMs)
		.start()

	splashView.iconView.animate()
		.scaleX(0.85f)
		.scaleY(0.85f)
		.setDuration(durationMs)
		.withEndAction {
			splashView.remove()
			title.animate().alpha(0f).setDuration(200).withEndAction { root.removeView(title) }.start()
		}
		.start()
}


