package com.towo.AnimalesApp.usecases.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.towo.AnimalesApp.databinding.ActivityHomeBinding
import com.towo.AnimalesApp.usecases.game.GameRouter

class HomeActivity : AppCompatActivity() {

    //Properties

    private lateinit var binding: ActivityHomeBinding

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        //Content
        setContentView(binding.root)

        //ViewModel
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Setup
        setup()

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    private fun setup(){

        buttons()

    }

private fun buttons() {
    binding.ImageButtonSums.setOnClickListener {
        GameRouter().launchBundle(this, "Sums")
        //finish()
    }
    binding.ImageButtonSubtraction.setOnClickListener {
        GameRouter().launchBundle(this, "Rest")
        //finish()
    }
    binding.ImageButtonMultiplication.setOnClickListener {
        GameRouter().launchBundle(this, "Multiplication")
        //finish()
    }
    binding.ImageButtonDivision.setOnClickListener {
        GameRouter().launchBundle(this, "Division")
        //finish()
    }
    binding.ImageButtonChoice.setOnClickListener {
        //This open a dialog
        GameRouter().launchBundle(this, "Choice")
        //finish()
    }
}

}