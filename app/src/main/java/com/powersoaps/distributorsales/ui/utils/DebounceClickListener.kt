package com.powersoaps.distributorsales.ui.utils

import android.os.SystemClock
import android.view.View
import java.util.*
import kotlin.math.abs

abstract class DebounceClickListener : View.OnClickListener {
    private val lastClickMap: MutableMap<View, Long>
    private val minIntervalMillis: Long = 1000L // Need to set the required value

    init {
        this.lastClickMap = WeakHashMap()
    }

    /**
     * Implement this in your subclass instead of onClick
     * @param view The view that was clicked
     */
    abstract fun onDebounceClick(view: View)

    override fun onClick(clickedView: View) {
        val previousClickTimestamp = lastClickMap[clickedView]
        val currentTimestamp = SystemClock.uptimeMillis()

        lastClickMap[clickedView] = currentTimestamp
        if (previousClickTimestamp == null || abs(currentTimestamp - previousClickTimestamp) > minIntervalMillis) {
            onDebounceClick(clickedView)
        }
    }
}