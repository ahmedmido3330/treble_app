package me.phh.treble.app

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import android.view.View
import android.widget.ListView

object CameraSettings : Settings {
    val multiCameras = "key_camera_multi_camera"
    val forceCamera2APIHAL3 = "key_camera_force_camera2api_hal3"
    val cameraTimestampOverride = "key_camera_camera_timestamp"

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Camera settings")
        return true
    }
}

class CameraSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_camera)

        // Bind preference summary for timestamp override
        findPreference(CameraSettings.cameraTimestampOverride)?.let {
            SettingsActivity.bindPreferenceSummaryToValue(it)
        }

        Log.d("PHH", "Camera settings loaded successfully")
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
