package com.towo.AnimalesApp.utils.extensions

import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.towo.AnimalesApp.R
import com.towo.AnimalesApp.utils.FontSize
import com.towo.AnimalesApp.utils.FontType

fun TextView.font(size: FontSize, type: FontType? = null, color: Int = ContextCompat.getColor(context, R.color.white)) {

    var fontType = type

    if (type == null) {
        fontType = when (size) {
            FontSize.TITLE, FontSize.HEAD, FontSize.BUTTON, FontSize.CAPTION -> FontType.BOLD
            FontSize.SUBTITLE, FontSize.SUBHEAD -> FontType.LIGHT
            FontSize.BODY -> FontType.REGULAR
        }
    }

    setTextSize(TypedValue.COMPLEX_UNIT_SP, size.size.toFloat())
    setTypeface(Typeface.createFromAsset(context.assets, fontType!!.path), Typeface.NORMAL)
    setTextColor(color)
    includeFontPadding = false

}

fun TextView.center() {
    textAlignment = View.TEXT_ALIGNMENT_CENTER
}