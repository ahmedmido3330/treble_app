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

object MiscSettings : Settings {
    val biometricstrong = "key_misc_biometricstrong"
    val treatVirtualSensorsAsReal = "key_misc_treat_virtual_sensors_as_real"
    val launcher3 = "key_misc_launcher3"
    val disableSaeUpgrade = "key_misc_disable_sae_upgrade"
    val storageFUSE = "key_misc_storage_fuse"
    val securize = "key_misc_securize"
    val dynamicsuperuser = "key_misc_dynamic_superuser"
    val unihertzdt2w = "key_misc_unihertz_dt2w"

    val stateMap = mapOf(
        "key_misc_dynamic_superuser" to "persist.sys.phh.dynamic_superuser",
    )

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

        Tools.updatePreferenceState(this, MiscSettings.stateMap)

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

        // Setup securize handler
        findPreference(MiscSettings.securize)?.setOnPreferenceClickListener {
            showSecurizeDialog()
            true
        }

        Log.d("PHH", "Misc settings loaded successfully")
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

    private fun showSecurizeDialog() {
        AlertDialog.Builder(activity).apply {
            setTitle("Removing Root")
            setMessage("Are you sure? This will remove in-built root access")
            setPositiveButton(android.R.string.yes) { _, _ -> executeSecurize() }
            setNegativeButton(android.R.string.no, null)
        }.show()
    }

    private fun executeSecurize() {
        try {
            val process = Runtime.getRuntime().exec("su")
            process.outputStream.bufferedWriter().use { writer ->
                writer.write("/system/bin/phh-securize.sh\n")
                writer.write("exit\n")
            }

            val exitCode = process.waitFor()
            if (exitCode == 0) {
                Log.d("PHH", "Successfully executed phh-securize.sh")
                Toast.makeText(activity, R.string.toast_reboot, Toast.LENGTH_LONG).show()
            } else {
                Log.e("PHH", "Failed with exit code: $exitCode")
            }
        } catch (e: Exception) {
            Log.e("PHH", "Failed to exec su shell", e)
        }
    }
}
