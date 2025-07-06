package me.phh.treble.app

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import android.view.View
import android.widget.ListView

object LenovoSettings : Settings {
    val dt2w = "lenovo_double_tap_to_wake"
    val support_pen = "lenovo_support_pen"

    override fun enabled(context: Context): Boolean {
        val isLenovo = Tools.vendorFp.contains("Lenovo")
        Log.d("PHH", "LenovoSettings enabled() called, isLenovo = $isLenovo")
        return isLenovo
    }
}

class LenovoSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_lenovo)

        if (LenovoSettings.enabled(context)) {
            Log.d("PHH", "Loading Lenovo fragment ${LenovoSettings.enabled(context)}")
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
