package me.phh.treble.app

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.os.SystemProperties
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.Toast
import java.io.File

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

object MiscSettings : Settings {
    val biometricstrong = "key_misc_biometricstrong"
    val launcher3 = "key_misc_launcher3"
    val disableSaeUpgrade = "key_misc_disable_sae_upgrade"
    val storageFUSE = "key_misc_storage_fuse"
    val securize = "key_misc_securize"
    val secureAdb = "key_misc_secure_adb"
    val disableDisplayDozeSuspend = "key_misc_disable_display_doze_suspend"
    val disableExpensiveRenderingMode = "key_misc_disable_expensive_rendering_mode"
    val unihertzdt2w = "key_misc_unihertz_dt2w"
    val dt2w = "key_misc_dt2w"

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Misc settings")
        return true
    }

    fun isRoot() = File(Tools.phhsu).exists()
}

class MiscSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_misc)


        // Check enabled status for each preference
        activity?.let { context ->
            EntryService.getEnabledPreferences(context).forEach { (key, isEnabled) ->
                if (!isEnabled) {
                    findPreference(key)?.let { pref ->
                        pref.parent?.removePreference(pref)
                    }
                }
            }
        }

        Log.d("PHH", "Misc settings loaded successfully")
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
