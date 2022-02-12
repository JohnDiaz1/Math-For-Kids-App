package com.towo.AnimalesApp.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.towo.AnimalesApp.R
import com.towo.AnimalesApp.utils.UIConstants
import com.towo.AnimalesApp.utils.Util

fun ActionBar.titleName(titleA: String){

    setDisplayShowHomeEnabled(true)
    setDisplayHomeAsUpEnabled(false)
    setHomeButtonEnabled(false)

   /* val drawable = ContextCompat.getDrawable(context, R.drawable.adicion)
    val bitmap = (drawable as BitmapDrawable).bitmap
    val height = Util.dpToPixel(context, UIConstants.LOGO_HEIGHT).toInt()
    val width = (height * bitmap.width) / bitmap.height
    val resizedDrawable = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(bitmap, width, height, true))

    setIcon(resizedDrawable)*/
    title = "titleA"

}