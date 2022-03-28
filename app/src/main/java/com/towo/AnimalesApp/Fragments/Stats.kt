package com.towo.AnimalesApp.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import com.applovin.mediation.ads.MaxAdView
import com.towo.AnimalesApp.R
import com.towo.AnimalesApp.provider.preferences.PreferencesKey
import com.towo.AnimalesApp.provider.preferences.PreferencesProvider


class Stats : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getAds()

    }

    private lateinit var myToolbar: Toolbar
    private lateinit var adView: MaxAdView

    private var ads: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.stats_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myToolbar = view.findViewById(R.id.my_toolbar)
        adView = view.findViewById(R.id.banner_juego)

        myToolbar.setNavigationIcon(R.drawable.ic_back)
        myToolbar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_stats2_to_seleccion)
        }

    }

    private fun getAds() {

        Thread {

            ads = context?.let { PreferencesProvider.bool(it, PreferencesKey.ADS) }!!

            if (ads) {
                activity?.runOnUiThread {
                    adView.loadAd()
                    adView.visibility = View.VISIBLE
                    adView.startAutoRefresh()
                }
            }

        }.start()
    }

}