package com.towo.AnimalesApp

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.towo.AnimalesApp.Interfaces.Efectos
import com.towo.AnimalesApp.Interfaces.ReemplazaFragment
import com.towo.AnimalesApp.Interfaces.Sonido

private lateinit var firebaseAnalytics: FirebaseAnalytics

class MainActivity : AppCompatActivity(), Sonido, Efectos, ReemplazaFragment {

    private var sonido: Boolean = true
    private var efectos: Boolean = true
    private var mp: MediaPlayer? = null
    private var mpCorrect: MediaPlayer? = null
    private var mpIncorrect: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //AppLovinSdk.getInstance( this ).showMediationDebugger()

        firebaseAnalytics = Firebase.analytics

        getPreferences()
        comprobarSonido()
        comprobarEfectos()
        initializeMobileAds()

    }

    private fun save(clave: String, valor: Boolean) {
        val sharedPref = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(clave, valor)
            commit()
        }
    }

    private fun getPreferences() {
        val sharedPref = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE) ?: return
        sonido = sharedPref.getBoolean("sonido", true)
        efectos = sharedPref.getBoolean("efecto", true)
    }

    private fun initializeMobileAds() {
        Thread {

            // Please make sure to set the mediation provider value to "max" to ensure proper functionality
            AppLovinSdk.getInstance( this ).mediationProvider = "max"
            AppLovinSdk.getInstance( this ).initializeSdk {
                // AppLovin SDK is initialized, start loading ads
                AppLovinPrivacySettings.setHasUserConsent( false, this )
                AppLovinPrivacySettings.setIsAgeRestrictedUser( true, this )
                AppLovinPrivacySettings.setDoNotSell( true, this )
            }

        }.start()
    }

    override fun reemplazarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.ContenedorFragments, fragment)
            commit()
        }
    }

    override fun comprobarSonido() {
        if (sonido) {
            // se crea el sonido MediaPlayer
            mp = MediaPlayer.create(this, R.raw.musica)
            mp?.isLooping = true
            mp?.start()
            save("sonido", sonido)
        } else {
            //NO SE CREA NADA
            mp = MediaPlayer.create(this, R.raw.musica)
            save("sonido", sonido)
        }
        if (efectos) {
            mpCorrect = MediaPlayer.create(this, R.raw.correcto)
            mpIncorrect = MediaPlayer.create(this, R.raw.incorrecto)
        }
    }

    override fun comprobarEfectos() {
        if (efectos) {
            //SE CREA LOS EFECTOS DE SONIDO
            save("efecto", efectos)
        } else {
            //NO SE CREA NADA
            save("efecto", efectos)
        }
    }

    override fun cambiaValorSonido() {
        if (sonido) {
            sonido = false
            mp?.pause()
        } else {
            sonido = true
            mp?.start()
        }
        save("sonido", sonido)
    }

    override fun cambiaValorEfectos() {
        if (efectos) {
            efectos = false
        } else {
            efectos = true
        }
        save("efecto", efectos)
    }

    override fun correct() {
        if (efectos) {
            mpCorrect?.start()
        }
    }

    override fun incorrect() {
        if (efectos) {
            mpIncorrect?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mp?.isPlaying!!) {
            mp?.pause()
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (sonido) {
            mp?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mp?.release()
    }

}
