package me.phh.treble.app

import android.content.Context
import android.os.ServiceManager
import android.content.om.IOverlayManager
import android.content.om.OverlayInfo
import android.os.RemoteException
import android.os.SystemProperties
import android.util.Log

object OverlayPicker: EntryStartup {
    private var om: IOverlayManager? = null
    private var overlays = listOf<OverlayInfo>()

    private val platform = SystemProperties.get("ro.board.platform")
    private val vendorFp = SystemProperties.get("ro.vendor.build.fingerprint")

    enum class ThemeOverlay {
        AccentColor,
        IconShape,
        FontFamily,
        IconPack
    }

    fun setOverlayEnabled(o: String, enabled: Boolean) {
        try {
            om?.setEnabled(o, enabled, 0)
            Log.d("PHH", "setOverlayEnabled: $o -> $enabled")
        } catch (e: RemoteException) {
            Log.e("PHH", "Failed to set overlay $o to $enabled", e)
        }
    }

    fun getThemeOverlays(to: ThemeOverlay): List<OverlayInfo> {
        val filtered = when(to) {
            ThemeOverlay.AccentColor ->
                overlays.filter {
                    it.targetPackageName == "android" &&
                    it.packageName.startsWith("com.android.theme.color.")
                }
            ThemeOverlay.IconShape ->
                overlays.filter {
                    it.targetPackageName == "android" &&
                    it.packageName.startsWith("com.android.theme.icon.")
                }
            ThemeOverlay.FontFamily ->
                overlays.filter {
                    it.targetPackageName == "android" &&
                    it.packageName.startsWith("com.android.theme.font.")
                }
            ThemeOverlay.IconPack ->
                overlays.filter {
                    it.packageName.startsWith("com.android.theme.icon_pack.")
                }
        }
        Log.d("PHH", "getThemeOverlays: tipo=$to retornou ${filtered.size} overlays")
        filtered.forEach {
            Log.d("PHH", "Overlay filtrado: package=${it.packageName}, target=${it.targetPackageName}")
        }
        return filtered
    }

    private fun enableLte(ctxt: Context) {
        if ("mt6580" != platform) {
            setOverlayEnabled("me.phh.treble.overlay.telephony.lte", true)
        }
    }

    private fun handleNokia(ctxt: Context) {
        if(vendorFp == null) return
        if(vendorFp.matches(Regex("Nokia/Phoenix.*"))) {
            setOverlayEnabled("me.phh.treble.overlay.nokia.pnx_8_1_x7.systemui", true)
        }
    }

    private fun handleSamsung(ctxt: Context) {
        if(vendorFp == null) return
        if(vendorFp.matches(Regex(".*(crown|star)[q2]*lte.*")) ||
            vendorFp.matches(Regex(".*(SC-0[23]K|SCV3[89]).*"))) {
            setOverlayEnabled("me.phh.treble.overlay.samsung.s9.systemui", true)
        }
    }

    private fun handleXiaomi(ctxt: Context) {
        if(vendorFp == null) return
        if(vendorFp.matches(Regex(".*iaomi/perseus.*"))) {
            setOverlayEnabled("me.phh.treble.overlay.xiaomi.mimix3.systemui", true)
        }
        if(vendorFp.matches(Regex(".*iaomi/cepheus.*"))) {
            setOverlayEnabled("me.phh.treble.overlay.xiaomi.mi9.systemui", true)
        }
    }

    private fun getOverlays() {
        if (om == null) {
            Log.w("PHH", "IOverlayManager não inicializado")
            overlays = emptyList()
            return
        }
        try {
            val all = om!!.getAllOverlays(0)
            val list = mutableListOf<OverlayInfo>()
            @Suppress("UNCHECKED_CAST")
            (all as? Map<String, List<OverlayInfo>>)?.forEach { (_, overlayList) ->
                list.addAll(overlayList)
            }
            overlays = list.toList()
            Log.d("PHH", "getOverlays: carregados ${overlays.size} overlays no total")
            overlays.forEach {
                Log.d("PHH", "Overlay: package=${it.packageName}, target=${it.targetPackageName}, enabled=${it.isEnabled}")
            }
        } catch (e: Exception) {
            Log.e("PHH", "Erro ao obter overlays", e)
            overlays = emptyList()
        }
    }

    override fun startup(ctxt: Context) {
        om = IOverlayManager.Stub.asInterface(
                ServiceManager.getService("overlay"))

        Log.d("PHH", "OverlayPicker startup iniciada")

        enableLte(ctxt)
        handleNokia(ctxt)
        handleSamsung(ctxt)
        handleXiaomi(ctxt)
        getOverlays()

        setOverlayEnabled("me.phh.treble.overlay.systemui.falselocks", true)

        Log.d("PHH", "OverlayPicker startup finalizada")
    }
}
