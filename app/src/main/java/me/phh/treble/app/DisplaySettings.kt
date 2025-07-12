package me.phh.treble.app

import android.app.Fragment
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment
import android.util.Log
import android.view.View
import android.widget.ListView

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

object DisplaySettings : Settings {
    val displayFps = "key_display_display_fps"
    val dynamicFps = "key_display_dynamic_fps"
    val noHwcomposer = "key_display_no_hwcomposer"
    val aod = "key_display_aod"
    val disableSfGlBackpressure = "key_display_disable_sf_gl_backpressure"
    val disableSfHwcBackpressure = "key_display_disable_sf_hwc_backpressure"
    val sfBlurEnabled = "key_display_sf_blur_enabled"
    val sfRenderEngineBackend = "key_display_sf_renderengine_backend"

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Display settings")
        return true
    }
}

class DisplaySettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_display)

        val prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(activity)
        if (prefs.all[DisplaySettings.sfRenderEngineBackend] is Boolean) {
            prefs.edit().remove(DisplaySettings.sfRenderEngineBackend).apply()
        }

        SettingsActivity.bindPreferenceSummaryToValue(findPreference(DisplaySettings.displayFps)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(DisplaySettings.sfBlurEnabled)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(DisplaySettings.sfRenderEngineBackend)!!)

        val fpsPref = findPreference(DisplaySettings.displayFps) as ListPreference
        val displayManager = activity?.getSystemService(DisplayManager::class.java)
        displayManager?.displays?.get(0)?.let { display ->
            val fpsEntries = listOf("Don't force") + display.supportedModes.map {
                val fps = it.refreshRate
                val w = it.physicalWidth
                val h = it.physicalHeight
                "${w}x${h}@${fps}"
            }
            val fpsValues = (-1..display.supportedModes.size).toList().map { it.toString() }
            fpsPref.entries = fpsEntries.toTypedArray()
            fpsPref.entryValues = fpsValues.toTypedArray()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(android.R.id.list)
        listView?.apply {
            divider = null
            dividerHeight = 0
            clipToPadding = false // importante
            setPadding(32, 64, 32, 32) // padding fixo mais seguro
        }
    }
}
