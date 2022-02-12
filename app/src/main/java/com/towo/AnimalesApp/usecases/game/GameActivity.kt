package com.towo.AnimalesApp.usecases.game

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.towo.AnimalesApp.R
import com.towo.AnimalesApp.databinding.ActivityGameBinding
import com.towo.AnimalesApp.utils.extensions.addClose
import com.towo.AnimalesApp.utils.extensions.titleName

class GameActivity : AppCompatActivity() {

    // Properties

    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)

        // Content
        setContentView(binding.root)

        //View Model
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        //Setup
        localize()
        setup()
        load()
    }

    override fun onSupportNavigateUp(): Boolean {
        this.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fab_close, R.anim.fab_open)
    }

    //Public


    //Private

    private fun localize() {

        binding.ButtonNext.text = getText(viewModel.buttonNext)

    }

    private fun setup(){

        val bundle = intent.extras
        val data = bundle?.getString("nombre")


        supportActionBar?.titleName(data!!)
        supportActionBar?.elevation = 0f

        val backIcon = (ContextCompat.getDrawable(this, R.drawable.ic_back))
        supportActionBar?.setHomeAsUpIndicator(backIcon)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private fun load() {
        binding.ImageViewSign.setImageResource(viewModel.sign("Choice"))
    }

}