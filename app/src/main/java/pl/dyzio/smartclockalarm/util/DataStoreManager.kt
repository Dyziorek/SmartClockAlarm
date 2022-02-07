package pl.dyzio.smartclockalarm.util


import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import pl.dyzio.smartclockalarm.MainActivity
import pl.dyzio.smartclockalarm.dataStore
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


open class PreferenceRequest<T>(
    val key: Preferences.Key<T>,
    val defaultValue: T,
)

class DataStoreManager(val dataStore: DataStore<Preferences>) {
    val preferenceFlow = dataStore.data

    suspend fun <T> getPreference(preferenceEntry: PreferenceRequest<T>) =
        preferenceFlow.first()[preferenceEntry.key] ?: preferenceEntry.defaultValue

    fun <T> getPreferenceFlow(request: PreferenceRequest<T>) =
        preferenceFlow.map {
            it[request.key] ?: request.defaultValue
        }

    suspend fun <T> editPreference(key: Preferences.Key<T>, newValue: T) {
        dataStore.edit { preferences ->
            preferences[key] = newValue
        }
    }

    suspend fun clearPreferences() {
        dataStore.edit { preferences -> preferences.clear() }
    }


    suspend fun updatePlugHost (newValue: String){
        Log.e("DS", "New value is: $newValue")
        var updateValue = newValue
        if (updateValue.length == 12)
        {
            val first = updateValue.filterIndexed{ idx, _ -> idx < 3}
            val second = updateValue.filterIndexed{ idx, _ -> idx in 3..5 }
            val third = updateValue.filterIndexed{ idx, _ -> idx in 6..8}
            val fourth = updateValue.filterIndexed{ idx, _ -> idx in 9..11}
            updateValue = "$first.$second.$third.$fourth"
        }
        Log.e("DS", "New value is: $updateValue")
        editPreference(PlugHost.key, newValue = updateValue)
    }

}


fun allowedToCall() : Boolean {
   val monitorStart = runBlocking { DataStoreManager(MainActivity.applicationContext().dataStore).getPreference(MonitorStart) }
   val monitorEnd = runBlocking { DataStoreManager(MainActivity.applicationContext().dataStore).getPreference(MonitorEnd) }
   val allowMonitor = runBlocking { DataStoreManager(MainActivity.applicationContext().dataStore).getPreference(MonitorSwitch) }
   if (allowMonitor)
   {
       val callTime = ZonedDateTime.now()
       val allowStart = LocalTime.parse( "${monitorStart.take(2)}:${monitorStart.takeLast(2)}", DateTimeFormatter.ofPattern("H:mm")).atDate(callTime.toLocalDate()).atZone(callTime.zone)
       val allowEnd = LocalTime.parse("${monitorEnd.take(2)}:${monitorEnd.takeLast(2)}", DateTimeFormatter.ofPattern("H:mm")).atDate(callTime.toLocalDate()).atZone(callTime.zone)
       var minutes = Duration.between(allowStart, allowEnd)
       if (minutes.isNegative)
       {
           minutes = Duration.between(allowStart, allowEnd.plusDays(1))
       }

       if (Duration.between(allowStart, callTime).isNegative && Duration.between(callTime, allowEnd).seconds > 0)
       {
           return true
       }
       else if (Duration.between(allowStart, callTime).seconds < minutes.seconds)
       {
           return true
       }
   }
   return false
}