package me.phh.treble.app

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.Toast

object UiSettings : Settings {
    val twoPaneLayout = "key_ui_two_pane_layout"
    val forceNavbarOff = "key_UI_force_navbar_off"
    val fodColor = "key_ui_fod_color"
    val pointerType = "key_ui_pointer_type"
    val statusbarpaddingtop = "key_ui_sb_padding_top"
    val statusbarpaddingstart = "key_ui_sb_padding_start"
    val statusbarpaddingend = "key_ui_sb_padding_end"
    val qsclockleftpadding = "key_misc_qs_clock_left_padding"
    val qsclockrightpadding = "key_misc_qs_clock_right_padding"
    val restartSystemUI = "key_ui_restart_systemui"

    val stateMap = mapOf(
        statusbarpaddingtop to "persist.sys.phh.status_bar_padding_top",
        statusbarpaddingstart to "persist.sys.phh.status_bar_padding_start",
        statusbarpaddingend to "persist.sys.phh.status_bar_padding_end"
    )

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing UI settings")
        return true
    }
}

class UiSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_ui)

        if (UiSettings.enabled(context)) {
            Tools.updatePreferenceState(this, UiSettings.stateMap)

            listOf(
                UiSettings.fodColor,
                UiSettings.pointerType,
                UiSettings.statusbarpaddingtop,
                UiSettings.statusbarpaddingstart,
                UiSettings.statusbarpaddingend,
                UiSettings.qsclockleftpadding,
                UiSettings.qsclockrightpadding,
            ).forEach { prefKey ->
                findPreference(prefKey)?.let {
                    SettingsActivity.bindPreferenceSummaryToValue(it)
                }
            }

            findPreference(UiSettings.restartSystemUI)?.setOnPreferenceClickListener {
                restartUIDialog()
                true
            }
        }
    }

    private fun restartUIDialog(): Boolean {
        AlertDialog.Builder(activity)
        .setTitle("Restarting System UI")
        .setMessage("Are you sure?")
        .setPositiveButton(android.R.string.yes) { _, _ ->
            try {
                val exitCode = Runtime.getRuntime().exec(arrayOf("su", "-c", "pkill -f com.android.systemui")).waitFor()
                if (exitCode == 0) {
                    Toast.makeText(activity, "SystemUI restarted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Failed to restart SystemUI", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        .setNegativeButton(android.R.string.no, null)
        .show()
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(android.R.id.list)
        listView?.apply {
            divider = null
            dividerHeight = 0
            clipToPadding = true
            setPadding(32, paddingTop, 32, paddingBottom)
        }
    }
}
