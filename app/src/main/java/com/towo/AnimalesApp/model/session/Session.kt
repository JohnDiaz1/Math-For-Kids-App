package com.towo.AnimalesApp.model.session

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.towo.AnimalesApp.provider.preferences.PreferencesKey
import com.towo.AnimalesApp.provider.preferences.PreferencesProvider
import java.util.*

class Session {
    // Initialization
    companion object {
        val instance = Session()
    }

    // Properties

    var Ads: String = ""
        private set


    private enum class LanguageType(val code: String) {
        ES("ES"),
        EN("EN")
    }

    // Life cycle

    fun configure(context: Context) {
        PreferencesProvider.string(context, PreferencesKey.Ads)?.let {
            Ads = it
        }
    }

    // Public

    // Private
    private fun setupNotification(add: Boolean, topic: String) {

        /*if (topic != Constants.ADMIN_LOGIN && topic == user?.login) {
            return
        }*/

        val languageCode = (Locale.getDefault().language ?: LanguageType.EN.code).uppercase()
        val subscribeLanguageType = if (languageCode == LanguageType.ES.name) LanguageType.ES else LanguageType.EN
        val unsubscribeLanguageType = if (languageCode != LanguageType.ES.name) LanguageType.ES else LanguageType.EN

        FirebaseMessaging.getInstance().apply {
            if (add) {
                subscribeToTopic("${topic}${subscribeLanguageType.code}")
                unsubscribeFromTopic("${topic}${unsubscribeLanguageType.code}")
            } else {
                unsubscribeFromTopic("${topic}${subscribeLanguageType.code}")
                unsubscribeFromTopic("${topic}${unsubscribeLanguageType.code}")
            }
        }
    }

}