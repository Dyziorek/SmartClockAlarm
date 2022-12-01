package pl.dyzio.smartclockalarm.service

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.dyzio.smartclockalarm.R
import pl.dyzio.smartclockalarm.SmartClockStates
import pl.dyzio.smartclockalarm.data.notify.NotifyItem
import pl.dyzio.smartclockalarm.dataStore
import pl.dyzio.smartclockalarm.net.NetCommand
import pl.dyzio.smartclockalarm.util.DataStoreManager
import pl.dyzio.smartclockalarm.util.PlugHost
import pl.dyzio.smartclockalarm.util.PlugPort
import pl.dyzio.smartclockalarm.util.sendNotification
import java.net.InetAddress
import java.util.*

@Suppress("DEPRECATION")
class AlarmActivity : Activity() {
    private var doneWork = false
    private var localLock: PowerManager.WakeLock? = null
    @SuppressLint("ServiceCast", "InvalidWakeLockTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        localLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "ImsWakeLock"
        )
        localLock?.acquire()
        super.onCreate(savedInstanceState)
        Toast.makeText(applicationContext, "Alarm Ring Ring", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.Main).launch(Dispatchers.Default, CoroutineStart.DEFAULT) {
            val dataStore = applicationContext?.dataStore!!
            val smartPlugHost = DataStoreManager(dataStore).getPreference(PlugHost)
            val plugPort = DataStoreManager(dataStore).getPreference(PlugPort)
            Log.v("ALARM", "AlarmReceiver: $smartPlugHost $plugPort")
            NetCommand.powerOn(InetAddress.getByName(smartPlugHost), plugPort.toInt(), 10000)
            val powerOn = NetCommand.isPowerOn(InetAddress.getByName(smartPlugHost), plugPort.toInt(), 10000)
            sendNotification(context = applicationContext, callStart = Calendar.getInstance().time, applicationContext.getString(
                R.string.alarm_fire))
            SmartClockStates.database.notifyDB().insert(NotifyItem(notifyText = "Alarm Called", notifyDone = powerOn , notifyDateTime = Calendar.getInstance().time, notifyActive = powerOn ))
            doneWork = true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (doneWork)
        {
            try {
                localLock?.release()
            }
            catch (error : Exception)
            {
                Log.e("LOCK", "Wake Lock crash")
            }
        }
    }
}