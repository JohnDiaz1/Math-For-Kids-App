package com.towo.AnimalesApp.usecases.game

import androidx.lifecycle.ViewModel
import com.towo.AnimalesApp.R

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

    fun numberRandom(): Array<Int> {

        return arrayOf((1..30).random(), (1..30).random())
    }

    fun result(operation: String, number1: Int, number2: Int): Int {

        when (operation) {

            "Sums" -> return number1 + number2
            "Rest" -> return number1 - number2
            "Multiplication" -> return number1 * number2
            "Division" -> number1 / number2
            "Choice" -> {
                val multi = arrayOf("Sums", "Rest", "Multiplication", "Division")
                val random = (0..3).random()
                return sign(multi[random])
            }

        }

        return 0
    }

    fun sign(operation: String): Int {

        when (operation) {

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