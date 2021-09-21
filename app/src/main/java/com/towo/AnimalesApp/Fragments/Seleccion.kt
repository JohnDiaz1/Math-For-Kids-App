package com.towo.AnimalesApp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.play.core.review.ReviewManagerFactory
import com.towo.AnimalesApp.Fragments.Combinadas
import com.towo.AnimalesApp.Interfaces.ReemplazaFragment
import com.towo.AnimalesApp.Interfaces.Sonido
import java.util.concurrent.TimeUnit
import kotlin.math.pow


class Seleccion : Fragment(), PurchasesUpdatedListener, MaxAdListener {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Sonido && context is ReemplazaFragment) {
            listener = context
            reemplaza = context
        }
    }

    private var listener: Sonido? = null
    private var reemplaza: ReemplazaFragment? = null

    // private lateinit var fragmentAprender: Aprender
    private lateinit var sumas: ImageButton
    private lateinit var restas: ImageButton
    private lateinit var multiplicacion: ImageButton
    private lateinit var division: ImageButton
    private lateinit var configuracion: ImageButton
    private lateinit var aprender: ImageButton
    private lateinit var combinadas: ImageButton
    private lateinit var otherApps: ImageButton
    private lateinit var acceptButton: Button
    private lateinit var adView: MaxAdView
    private lateinit var closeDialog: ImageView

    private lateinit var musicButton: ToggleButton
    private lateinit var effectButton: ToggleButton
    private lateinit var adsButton: Button
    private lateinit var rateUsButton: Button
    private lateinit var shareButton: Button
    private lateinit var terminos: TextView
    private lateinit var politica: TextView
    private lateinit var fragmentCombinadas: Combinadas
    private var dlogo: Int = 0
    private var sonido: Boolean = true
    private var efectos: Boolean = true
    private var info: Boolean = false
    private var ads: Boolean = true
    private var seleccion: BooleanArray = booleanArrayOf(false, false, false, false)
    private var intersitial: MaxInterstitialAd? = null
    private var retryAttempt = 0.0

    private var navController: NavController? = null

    private var billingClient: BillingClient? = null
    private val productId = "sumas_no_ads_"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seleccion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //   fragmentAprender = Aprender()
        adView = view.findViewById(R.id.adView)
        sumas = view.findViewById(R.id.sumas)
        restas = view.findViewById(R.id.restas)
        multiplicacion = view.findViewById(R.id.multiplicacion)
        division = view.findViewById(R.id.division)
        configuracion = view.findViewById(R.id.config)
        aprender = view.findViewById(R.id.aprender)
        otherApps = view.findViewById(R.id.btn_apps)
        combinadas = view.findViewById(R.id.combinadas)
        fragmentCombinadas = Combinadas()

        navController = Navigation.findNavController(view)

        getPreferences()
        threadSetupBilling()


        if (dlogo == 5) {
            showDialogRateUs()
        }

        if (!info) {
            showDialogPolitics()
        }
        initAds()

        sumas.setOnClickListener {
            intersicial()
            Navigation.findNavController(view).navigate(R.id.action_seleccion_to_sumas2)
        }

        restas.setOnClickListener {
            intersicial()
            Navigation.findNavController(view).navigate(R.id.action_seleccion_to_restas2)
        }

        multiplicacion.setOnClickListener {
            intersicial()
            Navigation.findNavController(view)
                .navigate(R.id.action_seleccion_to_multiplicacion2)
        }

        division.setOnClickListener {
            intersicial()
            Navigation.findNavController(view).navigate(R.id.action_seleccion_to_division2)
        }
        configuracion.setOnClickListener {
            showDialogSettings()
        }
        aprender.setOnClickListener {
            Toast.makeText(context, R.string.proximo, Toast.LENGTH_LONG).show()
        }
        otherApps.setOnClickListener{
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/dev?id=6603947329956780467&hl")
                )
            )
        }
        combinadas.setOnClickListener {
            dialogCombined()
        }

    }

    private fun initAds() {
        if (ads) {
            adView.loadAd()
            adView.visibility = View.VISIBLE
            adView.stopAutoRefresh()

            intersitial = MaxInterstitialAd("38ad8d51efcc885b", activity)
            intersitial!!.setListener(this)

            // Load the first ad
            intersitial!!.loadAd()
        }
    }

    private fun rateUs() {
        val appPackageName: String? = context?.packageName
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            )
        )
    }

    private fun shareApp() {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
            var aux = "Descarga la app\n"
            aux =
                aux + "https://play.google.com/store/apps/details?id=" + context?.packageName
            i.putExtra(Intent.EXTRA_TEXT, aux)
            startActivity(i)
        } catch (e: Exception) {
        }
    }

    private fun showDialogRateUs() {

        if (dlogo < 6) {
            dlogo++
            save("dialogo", dlogo)
        } else if (dlogo == 5) {

            activity?.let {
                val manager = ReviewManagerFactory.create(it)
                val request = manager.requestReviewFlow()
                request.addOnCompleteListener {
                    if (request.isSuccessful) {
                        // We got the ReviewInfo object
                        val reviewInfo = request.result
                        val flow =
                            activity?.let { it1 -> manager.launchReviewFlow(it1, reviewInfo) }
                        flow?.addOnCompleteListener { _ ->
                            // The flow has finished. The API does not indicate whether the user
                            // reviewed or not, or even whether the review dialog was shown. Thus, no
                            // matter the result, we continue our app flow.
                        }

                    } else {
                        dlogo = 4
                        save("dialogo", dlogo)
                    }
                }
            }


            /*  context?.let {
                  val dialogBuilder = AlertDialog.Builder(it)
                  val inflater = this.layoutInflater
                  val dialogVista = inflater.inflate(R.layout.dialogo_valorar, null)
                  dialogBuilder.setView(dialogVista)

                  dValorar = dialogVista.findViewById(R.id.dvalorar)
                  dNunca = dialogVista.findViewById(R.id.dnunca)
                  dTarde = dialogVista.findViewById(R.id.dtarde)

                  val otherDialog = dialogBuilder.create()
                  otherDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                  dValorar.setOnClickListener {
                      guardar("dialogo", 6)
                      valorarApp()
                      Toast.makeText(context, "Gracias Por Valorarnos", Toast.LENGTH_SHORT).show()
                      otherDialog.dismiss()
                  }
                  dTarde.setOnClickListener {
                      guardar("dialogo", 3)
                      otherDialog.dismiss()
                  }
                  dNunca.setOnClickListener {
                      guardar("dialogo", 6)
                      otherDialog.dismiss()
                  }
                  otherDialog.setCancelable(false)
                  otherDialog.show()
                  }*/
        }
    }

    private fun showDialogSettings() {

        context?.let { it2 ->
            val dialogBuilder = AlertDialog.Builder(it2)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialogo_config, null)
            dialogBuilder.setView(dialogView)

            musicButton = dialogView.findViewById(R.id.musicToggle)
            effectButton = dialogView.findViewById(R.id.efectoToggle)
            adsButton = dialogView.findViewById(R.id.btn_ads)
            rateUsButton = dialogView.findViewById(R.id.btn_valorar)
            shareButton = dialogView.findViewById(R.id.btn_compartir)
            closeDialog = dialogView.findViewById(R.id.cerrar_dialogo)
            terminos = dialogView.findViewById(R.id.terminos)
            politica = dialogView.findViewById(R.id.politica)

            val alertDialog = dialogBuilder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            comprobarSonidoYEfecto()
            links()

            closeDialog.setOnClickListener {
                alertDialog.dismiss()
            }
            musicButton.setOnClickListener {
                listener?.cambiaValorSonido()
            }
            shareButton.setOnClickListener {
                shareApp()
            }
            rateUsButton.setOnClickListener {
                rateUs()
            }
            adsButton.setOnClickListener {
                if (billingClient!!.isReady) {
                    threadInitiatePurchase()
                } else {
                    billingClient = context?.let {
                        BillingClient.newBuilder(it)
                            .enablePendingPurchases().setListener(this).build()
                    }
                    billingClient!!.startConnection(object : BillingClientStateListener {
                        override fun onBillingSetupFinished(billingResult: BillingResult) {
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                threadInitiatePurchase()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error: " + billingResult.debugMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onBillingServiceDisconnected() {

                        }

                    })
                }
            }
            effectButton.setOnClickListener {
                listener?.cambiaValorEfectos()
            }
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    private fun showDialogPolitics() {
        context?.let {
            val dialogBuilder = AlertDialog.Builder(it)
            dialogBuilder.setTitle(R.string.politica)
            dialogBuilder.setMessage(R.string.mensaje_politica)
            dialogBuilder.setPositiveButton(R.string.vale) { _, _ ->
                save("politica", true)
            }
            dialogBuilder.setNegativeButton(R.string.info) { _, _ ->
                save("politica", false)
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://towogames.com/politics.html")
                    )
                )
            }
            dialogBuilder.create()
            dialogBuilder.setCancelable(false)
            dialogBuilder.show()
        }

    }

    /*  private fun dialogoAprender() {
          context?.let {
              val dialogBuilder = AlertDialog.Builder(it)
              val inflater = this.layoutInflater
              val dialogView = inflater.inflate(R.layout.dialogo_aprender, null)
              dialogBuilder.setView(dialogView)

              cerrarDialogo = dialogView.findViewById(R.id.cerrar_dialogoAP)
              multiplicar = dialogView.findViewById(R.id.multiplicar)
              dividir = dialogView.findViewById(R.id.dividir)

              val alertDialog = dialogBuilder.create()
              alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

              cerrarDialogo.setOnClickListener {
                  alertDialog.dismiss()
              }

              multiplicar.setOnClickListener {
                  data.putInt("multiplicar", 1)
                  fragmentAprender.arguments = data
                  alertDialog.dismiss()
                  (activity as MainActivity).ReemplazarFragment(fragmentAprender)
              }

              dividir.setOnClickListener {
                  data.putInt("dividir", 1)
                  fragmentAprender.arguments = data
                  alertDialog.dismiss()
                  (activity as MainActivity).ReemplazarFragment(fragmentAprender)
              }

              alertDialog.setCancelable(false)
              alertDialog.show()
          }
          }*/

    private fun dialogCombined() {

        context?.let {
            val dialogBuilder = AlertDialog.Builder(it)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialogo_combinadas, null)
            dialogBuilder.setView(dialogView)

            sumas = dialogView.findViewById(R.id.sumas)
            restas = dialogView.findViewById(R.id.restas)
            multiplicacion = dialogView.findViewById(R.id.multiplicacion)
            division = dialogView.findViewById(R.id.division)
            acceptButton = dialogView.findViewById(R.id.aceptar)
            closeDialog = dialogView.findViewById(R.id.cerrar_dialogo)

            val alertDialog = dialogBuilder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            closeDialog.setOnClickListener {
                alertDialog.dismiss()
            }
            sumas.setOnClickListener {
                buttonSelect(0)
            }
            restas.setOnClickListener {
                buttonSelect(1)
            }
            multiplicacion.setOnClickListener {
                buttonSelect(2)
            }
            division.setOnClickListener {
                buttonSelect(3)
            }
            acceptButton.setOnClickListener {
                comprobacionSeleccion()
                alertDialog.dismiss()
            }
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }



    private fun comprobacionSeleccion() {

            var seleccionados = 0

            for (verdadero in 0..3) {

                if (seleccion[verdadero]) {
                    seleccionados++
                }
            }
            if (seleccionados <= 1) {
                activity?.runOnUiThread {
                    Toast.makeText(context, R.string.seleccione_mas, Toast.LENGTH_LONG).show()
                }
            } else {

                    activity?.runOnUiThread {
                        val b1 = Bundle()
                        b1.putBooleanArray("seleccionado", seleccion)
                        fragmentCombinadas.arguments = b1
                        
                        intersicial()
                        reemplaza?.reemplazarFragment(fragmentCombinadas)
                        //view?.let { it2 -> Navigation.findNavController(it2).navigate(action)}

                }
            }
        }

    private fun intersicial() {
    Thread {
        if (ads) {
            if (intersitial!!.isReady) {
                activity?.runOnUiThread {
                        intersitial!!.showAd()
                    }
                }
            }
        }.start()
    }

    private fun buttonSelect(button: Int) {

        if (!seleccion[button]) {
            seleccion[button] = true
            when (button) {
                0 -> sumas.setImageResource(R.drawable.btn_sumas_check)
                1 -> restas.setImageResource(R.drawable.btn_resta_check)
                2 -> multiplicacion.setImageResource(R.drawable.btn_multi_check)
                3 -> division.setImageResource(R.drawable.btn_div_check)
            }
        } else {
            seleccion[button] = false
            when (button) {
                0 -> sumas.setImageResource(R.drawable.btn_sumas)
                1 -> restas.setImageResource(R.drawable.btn_resta)
                2 -> multiplicacion.setImageResource(R.drawable.btn_multi)
                3 -> division.setImageResource(R.drawable.btn_div)
            }
        }
    }

    private fun save(key: String, valor: Int) {

        val sharedPref =
            activity?.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(key, valor)
            commit()
        }

    }

    private fun save(key: String, valor: Boolean) {
        val sharedPref =
            activity?.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(key, valor)
            commit()
        }

    }

    private fun getPreferences() {
        val sharedPref =
            activity?.getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE) ?: return
        dlogo = sharedPref.getInt("dialogo", 0)
        sonido = sharedPref.getBoolean("sonido", true)
        efectos = sharedPref.getBoolean("efecto", true)
        info = sharedPref.getBoolean("politica", false)
        ads = sharedPref.getBoolean("no-ads", true)
    }

    private fun comprobarSonidoYEfecto() {
        musicButton.isChecked = sonido
        effectButton.isChecked = efectos
    }

    private fun links() {
        terminos.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://towogames.com/terms.html")
                )
            )
        }

        politica.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://towogames.com/politics.html")
                )
            )
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun threadSetupBilling() {
        Thread {
            billingClient = context?.let {
                BillingClient.newBuilder(it)
                    .enablePendingPurchases().setListener(this).build()
            }
            billingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        val queryPurchase = billingClient!!.queryPurchases(INAPP)
                        val queryPurchases: MutableList<Purchase>? = queryPurchase.purchasesList
                        if (queryPurchases != null && queryPurchases.size > 0) {
                            handlePurchases(queryPurchases)
                        } else {
                            save("no-ads", true)
                        }
                    }
                }

                override fun onBillingServiceDisconnected() {

                }

            })
        }.start()
    }

    /*private fun setupBilling(){
        billingClient = context?.let {
            BillingClient.newBuilder(it)
                .enablePendingPurchases().setListener(this).build()
        }
        billingClient!!.startConnection(object : BillingClientStateListener{
            override fun onBillingSetupFinished(billingResult: BillingResult){
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                    val queryPurchase = billingClient!!.queryPurchases(INAPP)
                    val queryPurchases: MutableList<Purchase>? = queryPurchase.purchasesList
                    if (queryPurchases != null && queryPurchases.size > 0){
                        handlePurchases(queryPurchases)
                    }
                    else{
                        guardar("no-ads", true)
                    }
                }
            }

            override fun onBillingServiceDisconnected() {

            }

        })
    }*/

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            val queryAlreadyPurchasesResult = billingClient!!.queryPurchases(INAPP)
            val alreadyPurchases: MutableList<Purchase>? = queryAlreadyPurchasesResult.purchasesList
            if (alreadyPurchases != null) {
                handlePurchases(alreadyPurchases)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(context, R.string.buy_canceled, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show()
        }
    }

    fun threadInitiatePurchase() {
        Thread {
            val skuList: MutableList<String> = ArrayList()
            skuList.add(productId)
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(INAPP)

            billingClient!!.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (skuDetailsList != null && skuDetailsList.size > 0) {
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList[0])
                            .build()
                        billingClient!!.launchBillingFlow(context as Activity, flowParams)
                    }
                }
            }
        }.start()
    }

    /*fun initiatePurchase(){
        val skuList: MutableList<String> = ArrayList()
        skuList.add(productId)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(INAPP)

        billingClient!!.querySkuDetailsAsync(params.build()){
            billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                if (skuDetailsList != null && skuDetailsList.size > 0){
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsList[0])
                        .build()
                    billingClient!!.launchBillingFlow(context as Activity, flowParams )
                }
            }
        }
    }*/

    fun handlePurchases(purchases: MutableList<Purchase>) {

        val ackPurchase = AcknowledgePurchaseResponseListener { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                save("no-ads", false)
            }
        }

        for (purchase in purchases) {

            if (productId == purchase.sku && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient!!.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase)
                } else {

                    if (ads) {
                        save("no-ads", false)
                        Toast.makeText(context, R.string.usuarioPrimum, Toast.LENGTH_LONG).show()
                    }
                }

            } else if (productId == purchase.sku && purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Toast.makeText(context, R.string.compra_pendiente, Toast.LENGTH_LONG).show()
            } else if (productId == purchase.sku && purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                save("no-ads", true)
            }

        }

    }

    override fun onAdLoaded(maxAd: MaxAd)
    {
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0.0
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?)
    {
        // Interstitial ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis( 2.0.pow(6.0.coerceAtMost(retryAttempt)).toLong() )

        Handler().postDelayed( { intersitial!!.loadAd() }, delayMillis )
    }

    override fun onAdDisplayFailed(maxAd: MaxAd?, error: MaxError?)
    {
        // Interstitial ad failed to display. We recommend loading the next ad
        intersitial!!.loadAd()
    }

    override fun onAdDisplayed(maxAd: MaxAd) {}

    override fun onAdClicked(maxAd: MaxAd) {}
    override fun onAdRevenuePaid(ad: MaxAd?) {
    }

    override fun onAdHidden(maxAd: MaxAd)
    {
        // Interstitial ad is hidden. Pre-load the next ad
        intersitial!!.loadAd()
    }

}






