package com.towo.AnimalesApp.usecases.game

import androidx.lifecycle.ViewModel
import com.towo.AnimalesApp.R
import java.lang.Math.random

class GameViewModel : ViewModel() {

    // Properties

    // Localization

    val buttonNext = R.string.siguiente
    var button1 = ""
    var button2 = ""
    var button3 = ""
    var ImageViewOne = ""
    var ImageViewTwo = ""

    // Public

    fun numberRandom() {

           var number1 = (0..30).random()
            var number2 = (0..30).random()


                ImageViewOne = number1.toString()
                ImageViewTwo = number2.toString()
            }

    fun sign(operation: String) : Int {

        when(operation) {

            "Sums" -> return R.drawable.adicion
            "Rest" -> return R.drawable.resta
            "Multiplication" -> return R.drawable.multiplicacion
            "Division" -> return R.drawable.division
            "Choice" -> {
                val multi = arrayOf("Sums", "Rest", "Multiplication", "Division")
                val random = (0..3).random()
                return sign(multi[random])
            }

        }
        return 0
    }

}