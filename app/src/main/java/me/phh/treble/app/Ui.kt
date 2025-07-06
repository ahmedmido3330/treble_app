package me.phh.treble.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager
import java.lang.ref.WeakReference

@SuppressLint("StaticFieldLeak")
object Ui : EntryStartup {
    private lateinit var ctxt: WeakReference<Context>

        object UiSettings {
            // Chaves UI originais
            const val twoPaneLayout = "key_ui_two_pane_layout"
            const val forceNavbarOff = "key_UI_force_navbar_off"
            const val pointerType = "key_ui_pointer_type"
            const val fodColor = "key_ui_fod_color"
            const val statusbarpaddingtop = "key_ui_sb_padding_top"
            const val statusbarpaddingstart = "key_ui_sb_padding_start"
            const val statusbarpaddingend = "key_ui_sb_padding_end"
            const val qsclockleftpadding = "key_misc_qs_clock_left_padding"
            const val qsclockrightpadding = "key_misc_qs_clock_right_padding"

            // Chaves custom agora dentro de UiSettings também
            const val accentColor = "key_custom_accent_color"
            const val iconShape = "key_custom_icon_shape"
            const val fontFamily = "key_custom_font_family"
            const val iconPack = "key_custom_icon_pack"
        }

        private val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
            val context = ctxt.get() ?: return@OnSharedPreferenceChangeListener

            when (key) {
                // Configurações originais do Ui
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

                // Configurações custom dentro de UiSettings
                UiSettings.accentColor -> {
                    val value = sp.getString(key, "")
                    val accentColorOverlays = OverlayPicker.getThemeOverlays(OverlayPicker.ThemeOverlay.AccentColor)
                    accentColorOverlays
                    .filter { it.packageName != value }
                    .forEach { OverlayPicker.setOverlayEnabled(it.packageName, false) }
                    if (!value.isNullOrEmpty()) {
                        OverlayPicker.setOverlayEnabled(value, true)
                    }
                }
                UiSettings.iconShape -> {
                    val value = sp.getString(key, "")
                    val iconShapeOverlays = OverlayPicker.getThemeOverlays(OverlayPicker.ThemeOverlay.IconShape)
                    iconShapeOverlays
                    .filter { it.packageName != value }
                    .forEach { OverlayPicker.setOverlayEnabled(it.packageName, false) }
                    if (!value.isNullOrEmpty()) {
                        OverlayPicker.setOverlayEnabled(value, true)
                    }
                }
                UiSettings.fontFamily -> {
                    val value = sp.getString(key, "")
                    val fontFamilyOverlays = OverlayPicker.getThemeOverlays(OverlayPicker.ThemeOverlay.FontFamily)
                    fontFamilyOverlays
                    .filter { it.packageName != value }
                    .forEach { OverlayPicker.setOverlayEnabled(it.packageName, false) }
                    if (!value.isNullOrEmpty()) {
                        OverlayPicker.setOverlayEnabled(value, true)
                    }
                }
                UiSettings.iconPack -> {
                    val value = sp.getString(key, "")
                    val iconPackOverlays = OverlayPicker.getThemeOverlays(OverlayPicker.ThemeOverlay.IconPack)
                    val genericValue = value.toString().substringBeforeLast(".")
                    for (o in iconPackOverlays) {
                        if (!value.isNullOrEmpty() && o.packageName.startsWith(genericValue)) {
                            OverlayPicker.setOverlayEnabled(o.packageName, true)
                        } else {
                            OverlayPicker.setOverlayEnabled(o.packageName, false)
                        }
                    }
                }
            }
        }

        override fun startup(ctxt: Context) {
            Log.d("PHH", "Loading UI and Custom settings fragment")

            this.ctxt = WeakReference(ctxt.applicationContext)
            val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
            sp.registerOnSharedPreferenceChangeListener(spListener)

            // Atualiza todas as preferências ao iniciar
            listOf(
                // Preferências do Ui
                UiSettings.twoPaneLayout,
                UiSettings.forceNavbarOff,
                UiSettings.pointerType,
                UiSettings.fodColor,
                UiSettings.statusbarpaddingtop,
                UiSettings.statusbarpaddingstart,
                UiSettings.statusbarpaddingend,
                UiSettings.qsclockleftpadding,
                UiSettings.qsclockrightpadding,

                // Preferências do Custom (dentro do mesmo UiSettings)
                UiSettings.accentColor,
                UiSettings.iconShape,
                UiSettings.fontFamily,
                UiSettings.iconPack
            ).forEach { key ->
                if (sp.contains(key)) {
                    spListener.onSharedPreferenceChanged(sp, key)
                }
            }
        }
}
