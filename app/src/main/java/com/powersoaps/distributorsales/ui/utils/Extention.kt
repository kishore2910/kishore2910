package com.powersoaps.distributorsales.ui.utils

import android.view.View

fun View.setOnDebounceListener(onClick: (View) -> Unit) {
    val debounceOnClickListener = object : DebounceClickListener() {
        override fun onDebounceClick(view: View) {
            onClick(view)
        }
    }
    setOnClickListener(debounceOnClickListener)
}
