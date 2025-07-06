package me.phh.treble.app

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import android.view.View
import android.widget.ListView

object OnePlusSettings : Settings {
    val displayModeKey = "key_oneplus_display_mode"
    val highBrightnessModeKey = "key_oneplus_display_high_brightness"
    val usbOtgKey = "key_oneplus_usb_otg"
    val dt2w = "key_oneplus_double_tap_to_wake"

    override fun enabled(context: Context): Boolean {
        val isOnePlus = Tools.vendorFp.contains("OnePlus")
        Log.d("PHH", "OnePlusSettings enabled() called, isOnePlus = $isOnePlus")
        return isOnePlus
    }
}

class OnePlusSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_oneplus)

        if (OnePlusSettings.enabled(context)) {
            Log.d("PHH", "Loading OnePlus fragment ${OnePlusSettings.enabled(context)}")

            findPreference(OnePlusSettings.displayModeKey)?.let {
                SettingsActivity.bindPreferenceSummaryToValue(it)
            }

            findPreference(OnePlusSettings.highBrightnessModeKey)?.let {
                SettingsActivity.bindPreferenceSummaryToValue(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Apply same visual settings as AudioEffectsFragment
        val listView = view.findViewById<ListView>(android.R.id.list)
        listView?.apply {
            divider = null
            dividerHeight = 0
            clipToPadding = true
            setPadding(32, paddingTop, 32, paddingBottom)
        }
    }
}
