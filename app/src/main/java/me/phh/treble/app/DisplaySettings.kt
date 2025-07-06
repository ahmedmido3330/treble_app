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

object DisplaySettings : Settings {
    val displayFps = "key_display_display_fps"
    val dynamicFps = "key_display_dynamic_fps"
    val noHwcomposer = "key_display_no_hwcomposer"
    val aod = "key_display_aod"
    val disableSfGlBackpressure = "key_display_disable_sf_gl_backpressure"
    val disableSfHwcBackpressure = "key_display_disable_sf_hwc_backpressure"
    val sfBlurAlgorithm = "key_display_sf_blur_algorithm"
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

        // Bind preference summaries
        listOf(
            DisplaySettings.displayFps,
            DisplaySettings.sfBlurAlgorithm,
            DisplaySettings.sfRenderEngineBackend
        ).forEach { prefKey ->
            findPreference(prefKey)?.let {
                SettingsActivity.bindPreferenceSummaryToValue(it)
            }
        }

        // FPS preference setup
        (findPreference(DisplaySettings.displayFps) as? ListPreference)?.let { fpsPref ->
            val displayManager = activity?.getSystemService(DisplayManager::class.java)
            displayManager?.displays?.get(0)?.let { display ->
                val fpsEntries = listOf("Don't force") + display.supportedModes.map {
                    "${it.physicalWidth}x${it.physicalHeight}@${it.refreshRate}"
                }
                val fpsValues = (-1..display.supportedModes.size).toList().map { it.toString() }
                fpsPref.entries = fpsEntries.toTypedArray()
                fpsPref.entryValues = fpsValues.toTypedArray()
            }
        }

        Log.d("PHH", "Display settings loaded successfully")
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
