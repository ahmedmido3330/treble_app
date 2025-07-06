package me.phh.treble.app

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Bundle
import android.preference.ListPreference
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

    val accentColor = "key_ui_accent_color"
    val iconShape = "key_ui_icon_shape"
    val fontFamily = "key_ui_font_family"
    val iconPack = "key_ui_icon_pack"

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
    private var packages = listOf<PackageInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let { OverlayPicker.startup(it) }
        addPreferencesFromResource(R.xml.pref_ui)

        if (UiSettings.enabled(context)) {
            packages = activity.packageManager.getInstalledPackages(0)

            Tools.updatePreferenceState(this, UiSettings.stateMap)

            listOf(
                UiSettings.fodColor,
                UiSettings.pointerType,
                UiSettings.statusbarpaddingtop,
                UiSettings.statusbarpaddingstart,
                UiSettings.statusbarpaddingend,
                UiSettings.qsclockleftpadding,
                UiSettings.qsclockrightpadding,
                UiSettings.accentColor,
                UiSettings.iconShape,
                UiSettings.fontFamily,
                UiSettings.iconPack
            ).forEach { prefKey ->
                findPreference(prefKey)?.let {
                    SettingsActivity.bindPreferenceSummaryToValue(it)
                }
            }

            findPreference(UiSettings.restartSystemUI)?.setOnPreferenceClickListener {
                restartUIDialog()
                true
            }

            setupThemePreferences()
        }
    }

    private fun setupThemePreferences() {
        setupListPreference(UiSettings.accentColor, OverlayPicker.ThemeOverlay.AccentColor)
        setupListPreference(UiSettings.iconShape, OverlayPicker.ThemeOverlay.IconShape)
        setupListPreference(UiSettings.fontFamily, OverlayPicker.ThemeOverlay.FontFamily)
        setupIconPackPreference()
    }

    private fun setupListPreference(key: String, overlayType: OverlayPicker.ThemeOverlay) {
        val pref = findPreference(key) as? ListPreference
        pref?.let {
            val overlays = OverlayPicker.getThemeOverlays(overlayType)
            Log.d("PHH", "Overlays for $overlayType: ${overlays.map { it.packageName }}")

            val entries = listOf("Default") + overlays.map { getTargetName(it.packageName) }
            val values = listOf("") + overlays.map { it.packageName }

            it.entries = entries.toTypedArray()
            it.entryValues = values.toTypedArray()
        }
    }

    private fun setupIconPackPreference() {
        val pref = findPreference(UiSettings.iconPack) as? ListPreference
        pref?.let {
            val iconPackOverlays = OverlayPicker.getThemeOverlays(OverlayPicker.ThemeOverlay.IconPack)
            val iconPackMap = hashMapOf<String, String>().apply {
                iconPackOverlays.forEach { addOverlayToMap(it.packageName) }
            }

            val entries = listOf("Default") + iconPackMap.values
            val values = listOf("") + iconPackMap.keys

            it.entries = entries.toTypedArray()
            it.entryValues = values.toTypedArray()
        }
    }

    private fun getTargetName(packageName: String): String {
        return packages.find { it.packageName == packageName }?.applicationInfo
            ?.loadLabel(activity.packageManager)
            ?.toString()
            ?: packageName.substringAfterLast(".").replaceFirstChar { it.uppercase() }
    }

    private fun HashMap<String, String>.addOverlayToMap(overlay: String): HashMap<String, String> {
        val genericValue = overlay.substringBeforeLast(".")
        if (none { it.key.substringBeforeLast(".") == genericValue }) {
            put(overlay, getTargetName(overlay))
        }
        return this
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
