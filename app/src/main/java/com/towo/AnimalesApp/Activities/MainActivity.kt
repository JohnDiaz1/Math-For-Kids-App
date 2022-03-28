package com.towo.AnimalesApp

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.integration.IntegrationHelper
import com.towo.AnimalesApp.Interfaces.Efectos
import com.towo.AnimalesApp.Interfaces.Sonido
import com.towo.AnimalesApp.provider.preferences.PreferencesKey
import com.towo.AnimalesApp.provider.preferences.PreferencesProvider

private lateinit var firebaseAnalytics: FirebaseAnalytics

class MainActivity : AppCompatActivity(), Sonido, Efectos{

    private var sonido: Boolean = true
    private var efectos: Boolean = true
    private var mp: MediaPlayer? = null
    private var mpCorrect: MediaPlayer? = null
    private var mpIncorrect: MediaPlayer? = null

    private var appKey: String = "12eb5054d"

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

    private fun getPreferences() {
        sonido = PreferencesProvider.bool(this, PreferencesKey.SOUND)!!
        efectos = PreferencesProvider.bool(this, PreferencesKey.EFFECT)!!
    }

    private fun initializeMobileAds() {
        Thread {
            IronSource.init(this, appKey)
            IronSource.setMetaData("do_not_sell","false")
            IronSource.setMetaData("is_child_directed","true")
            IntegrationHelper.validateIntegration(Activity())
/*
            // Please make sure to set the mediation provider value to "max" to ensure proper functionality
            AppLovinSdk.getInstance( this ).mediationProvider = "max"
            AppLovinSdk.getInstance( this ).initializeSdk {
                // AppLovin SDK is initialized, start loading ads
                AppLovinPrivacySettings.setHasUserConsent( false, this )
                AppLovinPrivacySettings.setIsAgeRestrictedUser( true, this )
                AppLovinPrivacySettings.setDoNotSell( true, this )
            }

        */}.start()
    }


    override fun comprobarSonido() {
        mp = MediaPlayer.create(this, R.raw.musica)

        if (sonido) {
            // se crea el sonido MediaPlayer
            mp?.isLooping = true
            mp?.start()
            PreferencesProvider.set(this, PreferencesKey.SOUND, true)
        } else {
            //NO SE CREA NADA
            PreferencesProvider.set(this, PreferencesKey.SOUND, false)
        }
    }

    override fun comprobarEfectos() {
        mpCorrect = MediaPlayer.create(this, R.raw.correcto)
        mpIncorrect = MediaPlayer.create(this, R.raw.incorrecto)

        if (efectos) {
            //SE CREA LOS EFECTOS DE SONIDO
            PreferencesProvider.set(this, PreferencesKey.EFFECT, true)
        } else {
            //NO SE CREA NADA
            PreferencesProvider.set(this, PreferencesKey.EFFECT, false)
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
        PreferencesProvider.set(this, PreferencesKey.SOUND, sonido)
    }

    override fun cambiaValorEfectos() {
        if (efectos) {
            efectos = false
        } else {
            efectos = true
        }
        PreferencesProvider.set(this, PreferencesKey.EFFECT, efectos)
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
        IronSource.onPause(this);
        if (mp?.isPlaying!!) {
            mp?.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        IronSource.onResume(this);
        if (sonido) {
            mp?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mp?.release()
    }

}
