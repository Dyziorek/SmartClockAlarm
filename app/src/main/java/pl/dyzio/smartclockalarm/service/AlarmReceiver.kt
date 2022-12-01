package pl.dyzio.smartclockalarm.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context?, intent: Intent?) {
        val localIntent = Intent(context, AlarmActivity::class.java)
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(localIntent)
//        Log.v("ALARM""AlarmReceiver called.")

//        CoroutineScope(Dispatchers.Main).launch(Dispatchers.Default, CoroutineStart.DEFAULT) {
//            SmartClockStates.database.notifyDB().insert(NotifyItem(notifyText = "Alarm Called", notifyDone = true , notifyDateTime = Calendar.getInstance().time, notifyActive = true ))
//            val dataStore = context?.dataStore!!
//            val smartPlugHost = DataStoreManager(dataStore).getPreference(PlugHost)
//            val plugPort = DataStoreManager(dataStore).getPreference(PlugPort)
//            Log.v("ALARM", "AlarmReceiver: $smartPlugHost $plugPort")
//            NetCommand.powerOn(InetAddress.getByName(smartPlugHost), plugPort.toInt(), 10000)
//            val powerOn = NetCommand.isPowerOn(InetAddress.getByName(smartPlugHost), plugPort.toInt(), 10000)
//            sendNotification(context = context, callStart = Calendar.getInstance().time, context.getString(
//                            R.string.alarm_fire))
//            SmartClockStates.database.notifyDB().insert(NotifyItem(notifyText = "Alarm Called", notifyDone = powerOn , notifyDateTime = Calendar.getInstance().time, notifyActive = powerOn ))
//        }
    }
}