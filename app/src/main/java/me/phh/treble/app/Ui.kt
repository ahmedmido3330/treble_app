package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

object Ui : EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when (key) {
            UiSettings.twoPaneLayout -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.two_pane_layout", if (value) "true" else "false")
            }

            UiSettings.forceNavbarOff -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.mainkeys", if (value) "1" else "0")
            }

            UiSettings.pointerType -> {
                when (sp.getString(key, "mouse")) {
                    "mouse" -> {
                        SystemProperties.set("persist.sys.overlay.spen_pointer", "false")
                        SystemProperties.set("persist.sys.overlay.trans_pointer", "false")
                    }

                    "pen" -> {
                        SystemProperties.set("persist.sys.overlay.spen_pointer", "true")
                        SystemProperties.set("persist.sys.overlay.trans_pointer", "false")
                    }

                    "transparent" -> {
                        SystemProperties.set("persist.sys.overlay.spen_pointer", "false")
                        SystemProperties.set("persist.sys.overlay.trans_pointer", "true")
                    }
                }
            }

            UiSettings.fodColor -> {
                val value = sp.getString(key, "00ff00")
                SystemProperties.set("persist.sys.phh.fod_color", value)
            }

            UiSettings.statusbarpaddingtop -> {
                val value = sp.getString(key, "-1")?.toIntOrNull() ?: -1
                SystemProperties.set("persist.sys.phh.status_bar_padding_top", value.toString())
            }

            UiSettings.statusbarpaddingstart -> {
                val value = sp.getString(key, "-1")?.toIntOrNull() ?: -1
                SystemProperties.set("persist.sys.phh.status_bar_padding_start", value.toString())
            }

            UiSettings.statusbarpaddingend -> {
                val value = sp.getString(key, "-1")?.toIntOrNull() ?: -1
                SystemProperties.set("persist.sys.phh.status_bar_padding_end", value.toString())
            }

            UiSettings.qsclockleftpadding -> {
                val value = sp.getString(key, "-1")?.toIntOrNull() ?: -1
                SystemProperties.set("persist.sys.phh.qs_clock_left_padding", if (value != -1) value.toString() else "-1")
            }

            UiSettings.qsclockrightpadding -> {
                val value = sp.getString(key, "-1")?.toIntOrNull() ?: -1
                SystemProperties.set("persist.sys.phh.qs_clock_right_padding", if (value != -1) value.toString() else "-1")
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Loading UI fragment")
        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh all on boot
        listOf(
            UiSettings.twoPaneLayout,
            UiSettings.forceNavbarOff,
            UiSettings.pointerType,
            UiSettings.fodColor,
            UiSettings.statusbarpaddingtop,
            UiSettings.statusbarpaddingstart,
            UiSettings.statusbarpaddingend,
            UiSettings.qsclockleftpadding,
            UiSettings.qsclockrightpadding
        ).forEach { spListener.onSharedPreferenceChanged(sp, it) }
    }
}
