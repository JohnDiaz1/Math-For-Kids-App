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
import com.towo.AnimalesApp.databinding.RestasJuegoBinding
import java.util.*


class Restas : Fragment(), RewardedVideoListener {

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

    private var _binding: RestasJuegoBinding? = null
    private val binding get() = _binding!!

    private lateinit var banner: IronSourceBannerLayout

    private lateinit var homeButton: Button
    private lateinit var retryButton: Button
    private lateinit var timeProgress: ProgressBar
    private lateinit var aceptarButton: Button
    private lateinit var cancelButton: Button

    private var number1: Int = 0
    private var resultado: Int = 0
    private var number2: Int = 0
    private var respuesta: Int = 0
    private var convertRes: String? = null
    private lateinit var random: Random
    private var lifes: Int = 3
    private var ads: Boolean = true
    private val constantTime: Long = 31000
    private var time: Long = constantTime

    private val count = object : CountDownTimer(time, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.tempo.text = (millisUntilFinished / 1000).toString()
            binding.timeProgress.progress = (millisUntilFinished / 1000).toInt()
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

    private lateinit var rdmButtons: IntArray
    //FIN Inicializacion variables GLOBALES

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = RestasJuegoBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        random = Random()
        rdmButtons = IntArray(3)

        getAds()
        threadNumberRandom()

        binding.myToolbar.setNavigationIcon(R.drawable.ic_back)
        binding.myToolbar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_restas2_to_seleccion)
        }

        binding.btnSiguiente.visibility = View.INVISIBLE
        binding.moreLifesBtn.visibility = View.INVISIBLE

        binding.R1.setOnClickListener {
            binding.R1.isEnabled = false
            convertRes = binding.R1.text.toString()
            respuesta = convertRes!!.toInt()
            if (respuesta == resultado) {
                binding.R1.setBackgroundResource(R.drawable.button_round_green)
                listener?.correct()
                binding.R2.isEnabled = false
                binding.R3.isEnabled = false
                binding.btnSiguiente.visibility = View.VISIBLE
            } else {
                binding.R1.setBackgroundResource(R.drawable.button_round_red)
                listener?.incorrect()
                lifes--
                life()
            }
        }
        binding.R2.setOnClickListener {
            binding.R2.isEnabled = false
            convertRes = binding.R2.text.toString()
            respuesta = convertRes!!.toInt()
            if (respuesta == resultado) {
                binding.R2.setBackgroundResource(R.drawable.button_round_green)
                listener?.correct()
                binding.R1.isEnabled = false
                binding.R3.isEnabled = false
                binding.btnSiguiente.visibility = View.VISIBLE
            } else {
                binding.R2.setBackgroundResource(R.drawable.button_round_red)
                listener?.incorrect()
                lifes--
                life()
            }
        }
        binding.R3.setOnClickListener {
            binding.R3.isEnabled = false
            convertRes = binding.R3.text.toString()
            respuesta = convertRes!!.toInt()
            if (respuesta == resultado) {
                binding.R3.setBackgroundResource(R.drawable.button_round_green)
                listener?.correct()
                binding.R2.isEnabled = false
                binding.R1.isEnabled = false
                binding.btnSiguiente.visibility = View.VISIBLE
            } else {
                binding.R3.setBackgroundResource(R.drawable.button_round_red)
                listener?.incorrect()
                lifes--
                life()
            }
        }

        binding.btnSiguiente.setOnClickListener {
            binding.btnSiguiente.visibility = View.INVISIBLE
            resetButtons()
            startCount()
            threadNumberRandom()
        }

        binding.moreLifesBtn.setOnClickListener {
            dialogCorazones()
        }

    }

    private fun threadNumberRandom() {
        Thread {

            number1 = random.nextInt(30)
            number2 = random.nextInt(30)

            resultado = number1 - number2

            if (resultado >= 0) {
                activity?.runOnUiThread {
                    binding.imageViewNumUno.text = number1.toString()
                    binding.imageViewNumDos.text = number2.toString()
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
                view?.let { it1 -> Navigation.findNavController(it1).navigate(R.id.action_restas2_to_seleccion) }
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

    /*private fun resetCount() {
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
                binding.ivCorazones.setImageResource(R.drawable.lifes_1)
            }
            2 -> {
                showButtonLifes()
                binding.ivCorazones.setImageResource(R.drawable.lifes_2)
            }
            3 -> {
                binding.moreLifesBtn.isEnabled = false
                binding.moreLifesBtn.visibility = View.INVISIBLE
                binding.ivCorazones.setImageResource(R.drawable.lifes_3)
            }
        }
    }

    private fun showButtonLifes() {
        if (IronSource.isRewardedVideoAvailable()) {
            binding.moreLifesBtn.isEnabled = true
            binding.moreLifesBtn.visibility = View.VISIBLE
        }
    }

    private fun resetButtons() {
        binding.R1.isEnabled = true
        binding.R2.isEnabled = true
        binding.R3.isEnabled = true
        binding.R1.setBackgroundResource(R.drawable.button_round)
        binding.R2.setBackgroundResource(R.drawable.button_round)
        binding.R3.setBackgroundResource(R.drawable.button_round)
    }

    private fun threadButtonRandom() {

        Thread {

            val choiceButton: Int = random.nextInt(3)

            threadNotRepeat()
            when (choiceButton) {

                0 -> {
                    activity?.runOnUiThread {
                        binding.R1.text = resultado.toString()
                        binding.R2.text = rdmButtons[1].toString()
                        binding.R3.text = rdmButtons[2].toString()
                    }
                }
                1 -> {
                    activity?.runOnUiThread {
                        binding.R2.text = resultado.toString()
                        binding.R1.text = rdmButtons[1].toString()
                        binding.R3.text = rdmButtons[2].toString()
                    }
                }
                else -> {
                    activity?.runOnUiThread {
                        binding.R3.text = resultado.toString()
                        binding.R2.text = rdmButtons[1].toString()
                        binding.R1.text = rdmButtons[2].toString()
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
                val temp = random.nextInt(30)
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

            IronSource.shouldTrackNetworkState(activity, true);

            if (ads) {
                activity?.runOnUiThread {
                    banner = IronSource.createBanner(activity, ISBannerSize.BANNER)
                    val layoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT)
                    binding.bannerContainer.addView(banner, 0, layoutParams)
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