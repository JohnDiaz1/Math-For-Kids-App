package com.towo.AnimalesApp.Fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxRewardedAd
import com.towo.AnimalesApp.Interfaces.Efectos
import com.towo.AnimalesApp.R
import kotlinx.android.synthetic.main.sumas_juego.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class Multiplicacion : Fragment(), MaxRewardedAdListener {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Efectos) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startCount()
    }

    private var listener: Efectos? = null

    private lateinit var res1: Button
    private lateinit var res2: Button
    private lateinit var res3: Button
    private lateinit var siguiente: Button
    private lateinit var ivUno: TextView
    private lateinit var ivDos: TextView
    private lateinit var signo: ImageView
    private lateinit var adView: MaxAdView
    private lateinit var myToolBar: Toolbar
    private lateinit var corazones: ImageView
    private lateinit var contador: TextView
    private lateinit var masVidas: ImageView
    private lateinit var homeButton: Button
    private lateinit var retryButton: Button
    private lateinit var timeProgress: ProgressBar
    private lateinit var aceptarButton: Button
    private lateinit var cancelButton: Button

    private var number1: Int = 0
    private var resultado: Int = 0
    private var number2: Int = 0
    private var respuesta: Int = 0
    private var lifes: Int = 3
    private var convertRes: String? = null
    private lateinit var random: Random
    private var ads: Boolean = true
    private var rewardedAd: MaxRewardedAd? = null
    private var retryAttempt = 0.0
    private val constantTime: Long = 31000
    private var time: Long = constantTime
    private val count = object : CountDownTimer(time, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            contador.text = (millisUntilFinished / 1000).toString()
            timeProgress.progress = (millisUntilFinished / 1000).toInt()
        }

        override fun onFinish() {
            // Aqui se quitan los corazones
            lifes--
            listener?.incorrect()
            lifes()
            timmerRun = false
            startCount()
        }
    }
    private var timmerRun: Boolean = false

    private lateinit var rdmButtons: IntArray
    //FIN Inicializacion variables GLOBALES

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.multiplicacion_juego, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        res1 = view.findViewById(R.id.R1)
        res2 = view.findViewById(R.id.R2)
        res3 = view.findViewById(R.id.R3)
        siguiente = view.findViewById(R.id.btnSiguiente)
        ivUno = view.findViewById(R.id.imageView_NumUno)
        ivDos = view.findViewById(R.id.imageView_NumDos)
        signo = view.findViewById(R.id.imageView_signo)
        adView = view.findViewById(R.id.banner_juego)
        myToolBar = view.findViewById(R.id.my_toolbar)
        corazones = view.findViewById(R.id.iv_corazones)
        contador = view.findViewById(R.id.tempo)
        masVidas = view.findViewById(R.id.moreLifes_btn)
        random = Random()
        rdmButtons = IntArray(3)
        timeProgress = view.findViewById(R.id.time_progress)

        getAds()
        threadNumberRandom()

        myToolBar.setNavigationIcon(R.drawable.ic_back)
        myToolBar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_multiplicacion2_to_seleccion)
        }

        siguiente.visibility = View.INVISIBLE
        masVidas.visibility = View.INVISIBLE

        res1.setOnClickListener {
            res1.isEnabled = false
            convertRes = res1.text.toString()
            respuesta = convertRes!!.toInt()
            if (respuesta == resultado) {
                res1.setBackgroundResource(R.drawable.button_round_green)
                listener?.correct()
                res2.isEnabled = false
                res3.isEnabled = false
                siguiente.visibility = View.VISIBLE
            } else {
                res1.setBackgroundResource(R.drawable.button_round_red)
                listener?.incorrect()
                lifes--
                lifes()
            }
        }
        res2.setOnClickListener {
            res2.isEnabled = false
            convertRes = res2.text.toString()
            respuesta = convertRes!!.toInt()
            if (respuesta == resultado) {
                res2.setBackgroundResource(R.drawable.button_round_green)
                listener?.correct()
                res1.isEnabled = false
                res3.isEnabled = false
                siguiente.visibility = View.VISIBLE
            } else {
                res2.setBackgroundResource(R.drawable.button_round_red)
                listener?.incorrect()
                lifes--
                lifes()
            }
        }
        res3.setOnClickListener {
            res3.isEnabled = false
            convertRes = res3.text.toString()
            respuesta = convertRes!!.toInt()
            if (respuesta == resultado) {
                res3.setBackgroundResource(R.drawable.button_round_green)
                listener?.correct()
                res1.isEnabled = false
                res2.isEnabled = false
                siguiente.visibility = View.VISIBLE
            } else {
                res3.setBackgroundResource(R.drawable.button_round_red)
                listener?.incorrect()
                lifes--
                lifes()
            }
        }

        siguiente.setOnClickListener {
            siguiente.visibility = View.INVISIBLE
            resetButtons()
            startCount()
            threadNumberRandom()
        }

        masVidas.setOnClickListener {
            dialogCorazones()
        }

    }

    private fun threadNumberRandom() {

        Thread {

            number1 = random.nextInt(12)
            number2 = random.nextInt(12)

            resultado = number1 * number2

            activity?.runOnUiThread {
                ivUno.text = number1.toString()
                ivDos.text = number2.toString()
            }
            threadButtonRandom()
        }.start()
    }

    private fun dialogCorazones(){
        context?.let {
            val dialogBuilder = AlertDialog.Builder(it)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_corazones, null)
            dialogBuilder.setView(dialogView)

            aceptarButton = dialogView.findViewById(R.id.btn_accept)
            cancelButton = dialogView.findViewById(R.id.btn_cancel)

            val alertDialog = dialogBuilder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            aceptarButton.setOnClickListener {
                if (rewardedAd?.isReady == true) {
                    alertDialog.dismiss()
                    rewardedAd?.showAd()
                }else{
                    alertDialog.dismiss()
                    Toast.makeText(context, R.string.retry_later, Toast.LENGTH_LONG).show()
                }
            }

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
    }

    private fun dialogGameOver(){
        context?.let {
            val dialogBuilder = AlertDialog.Builder(it)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialogo_game_over, null)
            dialogBuilder.setView(dialogView)

            homeButton = dialogView.findViewById(R.id.btn_home)
            retryButton = dialogView.findViewById(R.id.btn_retry)

            val alertDialog = dialogBuilder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            homeButton.setOnClickListener {
                alertDialog.dismiss()
                view?.let { it1 -> Navigation.findNavController(it1).navigate(R.id.action_multiplicacion2_to_seleccion) }
            }
            retryButton.setOnClickListener {
                lifes = 3
                lifes()
                resetButtons()
                threadNumberRandom()
                startCount()
                alertDialog.dismiss()
            }
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    private fun startCount() {
        count.start()
        timmerRun = true
    }

    private fun pauseCount() {
        count.cancel()
        timmerRun = false
    }

    /*private fun resetCount() {
        time = constantTime
    }*/

    private fun lifes() {

        when (lifes) {
            0 -> {
                pauseCount()
                dialogGameOver()
            }
            1 -> {
                showButtonLifes()
                iv_corazones?.setImageResource(R.drawable.lifes_1)
            }
            2 -> {
                showButtonLifes()
                iv_corazones?.setImageResource(R.drawable.lifes_2)
            }
            3 -> {
                masVidas.isEnabled = false
                masVidas.visibility = View.INVISIBLE
                iv_corazones?.setImageResource(R.drawable.lifes_3)
            }
        }
    }

    private fun showButtonLifes() {
        if (rewardedAd?.isReady == true) {
            masVidas.isEnabled = true
            masVidas.visibility = View.VISIBLE
        }
    }

    private fun resetButtons() {
        res1.isEnabled = true
        res2.isEnabled = true
        res3.isEnabled = true
        res1.setBackgroundResource(R.drawable.button_round)
        res2.setBackgroundResource(R.drawable.button_round)
        res3.setBackgroundResource(R.drawable.button_round)
    }

    private fun threadButtonRandom() {

        Thread {

            val choiceButton: Int = random.nextInt(3)

            threadNotRepeat()
            when (choiceButton) {
                0 -> {
                    activity?.runOnUiThread {
                        res1.text = resultado.toString()
                        res2.text = rdmButtons[1].toString()
                        res3.text = rdmButtons[2].toString()
                    }
                }
                1 -> {
                    activity?.runOnUiThread {
                        res2.text = resultado.toString()
                        res1.text = rdmButtons[1].toString()
                        res3.text = rdmButtons[2].toString()
                    }
                }
                else -> {
                    activity?.runOnUiThread {
                        res3.text = resultado.toString()
                        res2.text = rdmButtons[1].toString()
                        res1.text = rdmButtons[2].toString()
                    }
                }
            }
        }.start()

    }

    private fun threadNotRepeat() {

        Thread {

            var contador = 1
            rdmButtons[0] = resultado
            while (contador < rdmButtons.size) {
                val temp = random.nextInt(120)
                var i = 0
                while (i < rdmButtons.size - 1) {
                    if (temp == rdmButtons[i]) {
                        break
                    }
                    i++
                }
                if (temp != rdmButtons[i]) {
                    rdmButtons[contador] = temp
                    contador++
                }
            }
        }.start()
    }

    private fun getAds() {

        Thread {

            val sharedPref =
                activity?.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
                    ?: return@Thread
            ads = sharedPref.getBoolean("no-ads", true)

            if (ads) {
                activity?.runOnUiThread {
                    createRewardedAd()
                    adView.loadAd()
                    adView.visibility = View.VISIBLE
                    adView.startAutoRefresh()
                }
            }

        }.start()
    }

    private fun createRewardedAd() {
        rewardedAd = MaxRewardedAd.getInstance("1880825b3fd71042", activity)
        rewardedAd!!.setListener(this)

        rewardedAd!!.loadAd()
    }

    // MAX Ad Listener
    override fun onAdLoaded(maxAd: MaxAd) {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0.0
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        // Rewarded ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++
        val delayMillis =
            TimeUnit.SECONDS.toMillis(2.0.pow(6.0.coerceAtMost(retryAttempt)).toLong())

        Handler().postDelayed({ rewardedAd?.loadAd() }, delayMillis)
    }

    override fun onAdDisplayFailed(maxAd: MaxAd?, error: MaxError?) {
        // Rewarded ad failed to display. We recommend loading the next ad
        rewardedAd?.loadAd()
    }

    override fun onAdDisplayed(maxAd: MaxAd) {}

    override fun onAdClicked(maxAd: MaxAd) {}
    override fun onAdRevenuePaid(ad: MaxAd?) {
    }

    override fun onAdHidden(maxAd: MaxAd) {
        // rewarded ad is hidden. Pre-load the next ad
        rewardedAd?.loadAd()
    }

    override fun onRewardedVideoStarted(maxAd: MaxAd) {}

    override fun onRewardedVideoCompleted(maxAd: MaxAd) {}

    override fun onUserRewarded(maxAd: MaxAd, maxReward: MaxReward) {
        // Rewarded ad was displayed and user should receive the reward
        lifes++
        lifes()
    }

    override fun onPause() {
        super.onPause()
        pauseCount()
    }

    override fun onResume() {
        super.onResume()
        startCount()
    }

}