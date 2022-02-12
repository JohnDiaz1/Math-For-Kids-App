package com.towo.AnimalesApp.usecases.home

import android.content.Context
import android.content.Intent
import com.towo.AnimalesApp.usecases.base.BaseActivityRouter

class HomeRouter: BaseActivityRouter {

    override fun intent(activity: Context): Intent = Intent(activity, HomeActivity::class.java)

}