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
import pl.dyzio.smartclockalarm.dataStore
import pl.dyzio.smartclockalarm.net.NetCommand
import pl.dyzio.smartclockalarm.util.DataStoreManager
import pl.dyzio.smartclockalarm.util.PlugHost
import pl.dyzio.smartclockalarm.util.PlugPort
import java.net.InetAddress
import java.util.*

@Suppress("DEPRECATION")
class AlarmStopActivity : Activity() {
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
        Toast.makeText(applicationContext, "Turn Off Switch", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.Main).launch(Dispatchers.Default, CoroutineStart.DEFAULT) {
            val dataStore = applicationContext?.dataStore!!
            val smartPlugHost = DataStoreManager(dataStore).getPreference(PlugHost)
            val plugPort = DataStoreManager(dataStore).getPreference(PlugPort)
            Log.v("TURN OFF", "Activity TurnOFF: $smartPlugHost $plugPort")
            NetCommand.powerOff(InetAddress.getByName(smartPlugHost), plugPort.toInt(), 10000)
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