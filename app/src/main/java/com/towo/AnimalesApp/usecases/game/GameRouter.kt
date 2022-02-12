package com.towo.AnimalesApp.usecases.game

import android.content.Context
import android.content.Intent
import com.towo.AnimalesApp.usecases.base.BaseActivityRouter

class GameRouter: BaseActivityRouter {

    override fun intent(activity: Context): Intent = Intent(activity, GameActivity::class.java)

}