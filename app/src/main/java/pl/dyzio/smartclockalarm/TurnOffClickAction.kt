package pl.dyzio.smartclockalarm

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import pl.dyzio.smartclockalarm.net.NetCommand
import pl.dyzio.smartclockalarm.ui.elements.toSByte
import pl.dyzio.smartclockalarm.util.DataStoreManager
import pl.dyzio.smartclockalarm.util.PlugHost
import pl.dyzio.smartclockalarm.util.PlugPort
import java.net.InetAddress

class TurnOffClickAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val dataStore = context.dataStore
        val smartPlugHost = DataStoreManager(dataStore).getPreference(PlugHost)
        val plugPort = DataStoreManager(dataStore).getPreference(PlugPort)
        Log.e("WIDGET", "plugPowerOff: $smartPlugHost $plugPort")

        if (smartPlugHost.contains('.')) {
            NetCommand.powerOff(
                InetAddress.getByAddress(
                    smartPlugHost.split(".").map { it.toSByte() }.toByteArray()
                ),
                plugPort.toInt(),
                1000
            )
        } else {
            NetCommand.powerOff(
                InetAddress.getByName(smartPlugHost),
                plugPort.toInt(),
                1000
            )
        }

      }
}