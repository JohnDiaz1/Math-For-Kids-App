package com.towo.AnimalesApp.usecases.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

interface BaseActivityRouter {

    //Activity

    fun intent(activity: Context): Intent

    fun launch(activity: Context) = activity.startActivity(intent(activity))

    fun launchBundle(activity: Context, title: String) {

        val dato = intent(activity)
        dato.putExtra("nombre", title)
        activity.startActivity(dato)

    }

}

interface BaseFragmentRouter {

    //Fragment

    fun fragment() : Fragment

    fun add(manager: FragmentManager, containerId: Int, tag: String) = manager.beginTransaction().add(containerId, fragment(), tag).commitAllowingStateLoss()

    fun replace(manager: FragmentManager, containerId: Int) = manager.beginTransaction().replace(containerId, fragment()).commit()

    fun show(manager: FragmentManager) = manager.beginTransaction().show(fragment()).commitAllowingStateLoss()

    fun hide(manager: FragmentManager) = manager.beginTransaction().hide(fragment()).commitAllowingStateLoss()

    fun remove(manager: FragmentManager) = manager.beginTransaction().remove(fragment()).commitAllowingStateLoss()

}