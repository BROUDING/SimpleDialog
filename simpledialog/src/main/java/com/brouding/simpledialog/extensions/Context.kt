package com.brouding.simpledialog.extensions

import android.content.Context
import android.util.TypedValue

fun Context.inDp(intDp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        intDp.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}