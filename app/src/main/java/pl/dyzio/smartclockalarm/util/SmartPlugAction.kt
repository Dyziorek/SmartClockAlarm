package pl.dyzio.smartclockalarm.util

import android.content.Context
import android.util.Log
import pl.dyzio.smartclockalarm.SmartClockStates
import pl.dyzio.smartclockalarm.data.NotifyItem
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
        var powerOn = "false"
        SmartClockStates.database.notifyDB().insert(NotifyItem(notifyText = "$powerOn, $number", notifyDone = powerOn != "" , notifyDateTime = callStart, notifyActive = true ))
        NetCommand.powerOn(InetAddress.getByName(smartPlugHost), plugPort.toInt(), 10000)
    }
}

