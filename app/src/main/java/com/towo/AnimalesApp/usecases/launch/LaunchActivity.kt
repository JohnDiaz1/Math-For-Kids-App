package com.towo.AnimalesApp.usecases.launch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.towo.AnimalesApp.databinding.ActivityLaunchBinding
import com.towo.AnimalesApp.model.session.Session
import com.towo.AnimalesApp.usecases.home.HomeRouter

class LaunchActivity : AppCompatActivity() {

    //Properties

    private lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)

        //Content
        setContentView(binding.root)

        //Setup
        setup()
        data()

    }

    private fun setup() {
        supportActionBar?.hide()

        //Analytics
        Firebase.analytics

        // Remote notifications
        Firebase.messaging.isAutoInitEnabled = true
    }

    private fun data(){
        // Session
        Session.instance.configure(this)
        showHome()
        /*Session.instance.fullReloadUser(this) {
            showHome()
        }*/
    }

    private fun showHome(){
        HomeRouter().launch(this)
        finish()
    }

}