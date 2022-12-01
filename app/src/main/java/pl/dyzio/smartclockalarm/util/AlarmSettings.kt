package pl.dyzio.smartclockalarm.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey


object MonitorSwitch : PreferenceRequest<Boolean>(booleanPreferencesKey("monitor_calls"), true)
object MonitorStart : PreferenceRequest<String>(stringPreferencesKey("monitor_start_time"), "")
object MonitorEnd : PreferenceRequest<String>(stringPreferencesKey("monitor_end_time"), "")
object MonitorDayWeeks : PreferenceRequest<Set<String>>(stringSetPreferencesKey("monitor_day_weeks"), emptySet())
object PlugHost : PreferenceRequest<String>(stringPreferencesKey("smartplug_host"), "0.0.0.0")
object PlugPort : PreferenceRequest<String>(stringPreferencesKey("smartplug_port"), "9999")

object AlarmActive : PreferenceRequest<Boolean>(booleanPreferencesKey("alarm_active"), false)
object AlarmTime : PreferenceRequest<String>(stringPreferencesKey("alarm_time"), "");

object RxPIN : PreferenceRequest<String>(stringPreferencesKey("rxPin"), "0000")
object RxPESEL : PreferenceRequest<String>(stringPreferencesKey("rxPESEL"), "73102701296")