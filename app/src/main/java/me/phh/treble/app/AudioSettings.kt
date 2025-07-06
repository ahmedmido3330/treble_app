package me.phh.treble.app

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import android.view.View
import android.widget.ListView

object AudioSettings : Settings {
    val headsetDevinput = "key_audio_headset_devinput"
    val disableAudioEffects = "key_audio_disable_audio_effects"
    val disableFastAudio = "key_audio_disable_fast_audio"
    val disableVoiceCallIn = "key_audio_disable_voice_call_in"
    val alternateAudiopolicy = "key_audio_alternate_audiopolicy"
    val sysbta = "key_bt_dynamic_sysbta"
    val workarounds = "key_bt_workarounds"
    val escoTransportUnitSize = "key_bt_esco_transport_unit_size"
    val maxBTAudioDevices = "key_bt_max_bluetooth_audio_devices"
    val unsupportedCommands = "key_bt_unsupported_commands"
    val unsupportedOgFeatures = "key_bt_unsupported_og"
    val unsupportedLeFeatures = "key_bt_unsupported_le"
    val unsupportedStates = "key_bt_unsupported_states"
    val leVersionCap = "key_bt_le_version_cap"
    val disableLeApcfExtended = "key_bt_disable_le_apcfe"

    val stateMap = mapOf(
        "key_bt_unsupported_commands" to "persist.sys.bt.unsupported.commands",
        "key_bt_unsupported_og" to "persist.sys.bt.unsupported.ogfeatures",
        "key_bt_unsupported_le" to "persist.sys.bt.unsupported.lefeatures",
        "key_bt_unsupported_states" to "persist.sys.bt.unsupported.states",
        "key_bt_le_version_cap" to "persist.sys.bt.max_vendor_cap",
    )

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Audio settings")
        return true
    }
}

class AudioSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_audio)

        // Aplica o mesmo estilo visual do AudioEffectsFragment
        Tools.updatePreferenceState(this, AudioSettings.stateMap)

        // Configura os sumários das preferências
        listOf(
            AudioSettings.workarounds,
            AudioSettings.escoTransportUnitSize,
            AudioSettings.maxBTAudioDevices,
            AudioSettings.unsupportedCommands,
            AudioSettings.unsupportedOgFeatures,
            AudioSettings.unsupportedLeFeatures,
            AudioSettings.unsupportedStates,
            AudioSettings.leVersionCap
        ).forEach { key ->
            findPreference(key)?.let {
                SettingsActivity.bindPreferenceSummaryToValue(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aplica as mesmas configurações visuais do AudioEffectsFragment
        val listView = view.findViewById<ListView>(android.R.id.list)
        listView?.apply {
            divider = null
            dividerHeight = 0
            clipToPadding = true
            setPadding(32, paddingTop, 32, paddingBottom)
        }
    }
}
