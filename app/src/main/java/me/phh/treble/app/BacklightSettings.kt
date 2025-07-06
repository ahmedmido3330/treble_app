package me.phh.treble.app

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import android.view.View
import android.widget.ListView

object BacklightSettings : Settings {
    val minimalBrightness = "key_backlight_minimal_brightness"
    val disableButtonsBacklight = "key_backlight_disable_buttons_backlight"
    val backlightScale = "key_backlight_backlight_scale"
    val lowGammaBrightness = "key_backlight_low_gamma_brightness"
    val linearBrightness = "key_backlight_linear_brightness"

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Backlight settings")
        return true
    }
}

class BacklightSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_display)

        Log.d("PHH", "Backlight settings loaded")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aplica as mesmas configurações visuais do AudioEffectsFragment
        val listView = view.findViewById<ListView>(android.R.id.list)
        listView?.apply {
            divider = null
            dividerHeight = 0
            clipToPadding = true
            setPadding(32, paddingTop, 32, paddingBottom)
        }
    }
}
