package com.ursolgleb.pokcemon.presentation.util

import android.widget.EditText
import androidx.appcompat.widget.SearchView

fun SearchView.applyHintAndColors(hintText: String, hintColor: Int, textColor: Int) {
	queryHint = hintText
	(this.findViewById(androidx.appcompat.R.id.search_src_text) as? EditText)?.let { et ->
		et.hint = hintText
		et.setHintTextColor(hintColor)
		et.setTextColor(textColor)
	}
}


