package com.towo.AnimalesApp.Fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSourceBannerLayout
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.model.Placement
import com.ironsource.mediationsdk.sdk.RewardedVideoListener
import com.towo.AnimalesApp.Interfaces.Efectos
import com.towo.AnimalesApp.R
import com.towo.AnimalesApp.provider.preferences.PreferencesKey
import com.towo.AnimalesApp.provider.preferences.PreferencesProvider
import kotlinx.android.synthetic.main.dialogo_combinadas.*
import kotlinx.android.synthetic.main.sumas_juego.*
import java.util.*

class Division : Fragment(), RewardedVideoListener {

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
    private lateinit var bannerContainer: FrameLayout
    private lateinit var banner: IronSourceBannerLayout
    private lateinit var myToolBar: Toolbar
    private lateinit var random: Random
    private lateinit var rdmButtons: IntArray
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
    private var ads: Boolean = true
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
            life()
            timmerRun = false
            startCount()
        }
    }
    private var timmerRun: Boolean = false

    //FIN Inicializacion variables GLOBALES

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.division_juego, container, false)

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
        bannerContainer = view.findViewById(R.id.bannerContainer)
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
            Navigation.findNavController(view).navigate(R.id.action_division2_to_seleccion)
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
                life()
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
                life()
            }
        }
        res3.setOnClickListener {
            res3.isEnabled = false
            convertRes = res3.text.toString()
            respuesta = convertRes!!.toInt()
            if (respuesta == resultado) {
                res3.setBackgroundResource(R.drawable.button_round_green)
                listener?.correct()
                res2.isEnabled = false
                res1.isEnabled = false
                siguiente.visibility = View.VISIBLE
            } else {
                res3.setBackgroundResource(R.drawable.button_round_red)
                listener?.incorrect()
                lifes--
                life()
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

            number1 = random.nextInt(100)
            number2 = random.nextInt(100)

            if (number2 != 0) {
                if ((number1 % number2) == 0) {

                    resultado = number1 / number2

                    activity?.runOnUiThread {
                        ivUno.text = number1.toString()
                        ivDos.text = number2.toString()
                    }
                } else {
                    threadNumberRandom()
                }
            } else {
                threadNumberRandom()
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
                if (IronSource.isRewardedVideoAvailable()) {
                    alertDialog.dismiss()
                    IronSource.showRewardedVideo("Corazones_Reward");
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
                view?.let { it1 -> Navigation.findNavController(it1).navigate(R.id.action_division2_to_seleccion) }
            }
            retryButton.setOnClickListener {
                lifes = 3
                life()
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

    /* private fun resetCount() {
        time = constantTime
    }*/

    private fun life() {

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
        if (IronSource.isRewardedVideoAvailable()) {
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
                val temp = random.nextInt(50)
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

            ads = context?.let { PreferencesProvider.bool(it, PreferencesKey.ADS) }!!

            IronSource.shouldTrackNetworkState(activity, true);

            if (ads) {
                activity?.runOnUiThread {
                    banner = IronSource.createBanner(activity, ISBannerSize.BANNER)
                    val layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT)
                    bannerContainer.addView(banner, 0, layoutParams)
                    IronSource.loadBanner(banner)
                }
            }

        }.start()
    }

    override fun onRewardedVideoAdOpened() {
        TODO("Not yet implemented")
    }

    override fun onRewardedVideoAdClosed() {
        TODO("Not yet implemented")
    }

    override fun onRewardedVideoAvailabilityChanged(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onRewardedVideoAdStarted() {
        TODO("Not yet implemented")
    }

    override fun onRewardedVideoAdEnded() {
        lifes++
        life()
    }

    override fun onRewardedVideoAdRewarded(p0: Placement?) {
        val rewardAmount: Int = p0!!.rewardAmount
        lifes += rewardAmount
        life()
    }

    override fun onRewardedVideoAdShowFailed(p0: IronSourceError?) {
        TODO("Not yet implemented")
    }

    override fun onRewardedVideoAdClicked(p0: Placement?) {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        super.onPause()
        IronSource.onPause(activity)
        pauseCount()
    }

    override fun onResume() {
        super.onResume()
        IronSource.onResume(activity)
        startCount()
    }

}