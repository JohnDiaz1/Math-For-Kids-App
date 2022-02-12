package com.towo.AnimalesApp.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.towo.AnimalesApp.R


fun AppCompatActivity.addClose() {

    supportActionBar?.title = ""
    supportActionBar?.elevation = 0f

    val closeIcon = (ContextCompat.getDrawable(this, R.drawable.adicion) as BitmapDrawable).bitmap
    val resizedCloseIcon: Drawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(closeIcon, 48, 48, false))
    //resizedCloseIcon.setTint(ContextCompat.getColor(this, R.color.white))
    supportActionBar?.setHomeAsUpIndicator(resizedCloseIcon)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

}

fun AppCompatActivity.hideSoftInput() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}