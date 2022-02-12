package com.towo.AnimalesApp.utils.extensions

import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.towo.AnimalesApp.R
import com.towo.AnimalesApp.utils.FontSize
import com.towo.AnimalesApp.utils.FontType
import com.towo.AnimalesApp.utils.UIConstants

fun Button.navigation(listener: View.OnClickListener) {

    setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.BODY.size.toFloat())
    setTextColor(ContextCompat.getColor(context, R.color.black))
    setTypeface(Typeface.createFromAsset(context.assets, FontType.LIGHT.path), Typeface.NORMAL)
    setOnClickListener(listener)
}

fun Button.enable(enable: Boolean, opacity: Boolean = false) {
    if (opacity) {
        alpha = if (enable) 1f else UIConstants.SHADOW_OPACITY
    }
    isEnabled = enable
}