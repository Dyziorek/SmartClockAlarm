package pl.dyzio.smartclockalarm.util

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pl.dyzio.smartclockalarm.R
import pl.dyzio.smartclockalarm.SmartClockStates
import pl.dyzio.smartclockalarm.SmartClockStates.CHANNEL_ID
import pl.dyzio.smartclockalarm.data.notify.NotifyItem
import pl.dyzio.smartclockalarm.dataStore
import pl.dyzio.smartclockalarm.net.NetCommand
import java.net.InetAddress
import java.util.*

@Suppress("UNUSED_VARIABLE")
suspend fun callReceived(ctx : Context?, number : String?, callStart : Date? )
{
    val dataStore = ctx?.dataStore!!

    if (allowedToCall())
    {
        val smartPlugHost = DataStoreManager(dataStore).getPreference(PlugHost)
        val plugPort = DataStoreManager(dataStore).getPreference(PlugPort)
        Log.e("CALL", "callReceived: $smartPlugHost $plugPort")
        NetCommand.powerOn(InetAddress.getByName(smartPlugHost), plugPort.toInt(), 10000)
        val powerOn = NetCommand.isPowerOn(InetAddress.getByName(smartPlugHost), plugPort.toInt(), 10000)
        SmartClockStates.database.notifyDB().insert(NotifyItem(notifyText = "$powerOn, $number", notifyDone = powerOn , notifyDateTime = callStart, notifyActive = powerOn ))
        sendNotification(context = ctx, callStart = callStart, "$powerOn, $number")
    }
}

fun sendNotification(context: Context?, callStart: Date?, notifyText: String) {

    val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_phone_24)
        .setContentTitle(context.getString(R.string.call_note))
        .setContentText(String.format(context.getString(R.string.phone_notify_info), notifyText, callStart.toString()))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
    NotificationManagerCompat.from(context).notify(SmartClockStates.notificationID, builder.build())
}
