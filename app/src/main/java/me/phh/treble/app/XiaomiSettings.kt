package me.phh.treble.app

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import android.view.View
import android.widget.ListView

object XiaomiSettings : Settings {
    val dt2w = "xiaomi_double_tap_to_wake"
    val fodColor = "xiaomi_fod_color"  // Example additional preference
    val dcDimming = "xiaomi_dc_dimming"  // Example additional preference

    override fun enabled(context: Context): Boolean {
        val isXiaomi = Tools.vendorFp.run {
            lowercase().startsWith("xiaomi/") ||
            lowercase().startsWith("redmi/") ||
            lowercase().startsWith("poco/")
        }
        Log.d("PHH", "XiaomiSettings enabled() called, isXiaomi = $isXiaomi")
        return isXiaomi
    }
}

class XiaomiSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_xiaomi)

        if (XiaomiSettings.enabled(context)) {
            Log.d("PHH", "Loading Xiaomi fragment ${XiaomiSettings.enabled(context)}")

            // Bind preference summaries to values
            findPreference(XiaomiSettings.dt2w)?.let {
                SettingsActivity.bindPreferenceSummaryToValue(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Apply consistent visual settings
        val listView = view.findViewById<ListView>(android.R.id.list)
        listView?.apply {
            divider = null
            dividerHeight = 0
            clipToPadding = true
            setPadding(32, paddingTop, 32, paddingBottom)
        }
    }
}
