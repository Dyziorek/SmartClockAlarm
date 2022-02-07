package pl.dyzio.smartclockalarm.ui.elements

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.async
import pl.dyzio.smartclockalarm.R
import pl.dyzio.smartclockalarm.SmartClockStates
import pl.dyzio.smartclockalarm.dataStore
import pl.dyzio.smartclockalarm.net.NetCommand
import pl.dyzio.smartclockalarm.ui.theme.SmartClockAlarmTheme
import pl.dyzio.smartclockalarm.util.DataStoreManager
import pl.dyzio.smartclockalarm.util.PlugHost
import pl.dyzio.smartclockalarm.util.PlugPort
import java.net.InetAddress

private fun String.toSByte() : Byte
{
    val check = this.toInt()
    if (check > 127)
    {
        return (check - 256).toByte()
    }
    return  check.toByte()
}

@Composable
fun ClockAlarmPanel() {
    val context = LocalContext.current
    val dataStore = context.dataStore
    val dataManager = remember { DataStoreManager(dataStore) }
    val prefs by dataManager.preferenceFlow.collectAsState(initial = null)
    val hostName = stringResource(R.string.smartplug_host) + ":" +
            (prefs?.get(PlugHost.key) ?: PlugHost.defaultValue).split('.')
                .joinToString(".") { it.trimStart('0') }
    val scope = rememberCoroutineScope()
    val ioScope = SmartClockStates.ioDispatcher
    Scaffold {
        Column {
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                    scope.async(ioScope) {

                        val smartPlugHost = DataStoreManager(dataStore).getPreference(PlugHost)
                        val plugPort = DataStoreManager(dataStore).getPreference(PlugPort)
                        Log.e("INFO", "Socket connection from $smartPlugHost with $plugPort")
                        if (smartPlugHost.contains('.')) {
                            NetCommand.powerOn(
                                InetAddress.getByAddress(
                                    smartPlugHost.split(".").map{ it.trimStart('0') }.map { it.toSByte() }.toByteArray()
                                ),
                                plugPort.toInt(),
                                10000
                            )
                        }
                        else
                        {
                            NetCommand.powerOn(
                                InetAddress.getByName(smartPlugHost),
                                plugPort.toInt(),
                                10000
                            )
                        }
                    }
                },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
            ) {
                Icon(imageVector = Icons.Filled.Power, contentDescription = stringResource(id = R.string.powerOn))
                Text(
                    text = stringResource(id = R.string.powerOn),
                    style = MaterialTheme.typography.button.copy(Color.Red),
                    modifier = Modifier.padding(10.dp)
                )
            }
            Button(
                onClick = {
                    scope.async(ioScope) {
                        val smartPlugHost = DataStoreManager(dataStore).getPreference(
                            PlugHost
                        )
                        val plugPort = DataStoreManager(dataStore).getPreference(PlugPort)
                        if (smartPlugHost.contains('.'))
                        {
                            NetCommand.powerOff(
                                InetAddress.getByAddress(smartPlugHost.split(".").map {  it.toSByte() }.toByteArray()),
                                plugPort.toInt(),
                                10000
                            )
                        }
                        else {
                            NetCommand.powerOff(
                                InetAddress.getByName(smartPlugHost),
                                plugPort.toInt(),
                                10000
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Icon(imageVector = Icons.Filled.PowerOff, contentDescription = stringResource(id = R.string.powerOff))
                Text(
                    text = stringResource(id = R.string.powerOff),
                    style = MaterialTheme.typography.button.copy(Color.Blue),
                    modifier = Modifier.padding(10.dp)
                )
            }

        }
        Spacer(Modifier.height(20.dp))

//                Surface(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(10.dp),
//                    shape = Shapes.medium
//                )
//                {



            // }

        Text(text = hostName, style = MaterialTheme.typography.body1, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp))
        Text(stringResource(id = R.string.smartplug_port) +": ${prefs?.get(PlugPort.key) ?: PlugHost.defaultValue}", style = MaterialTheme.typography.subtitle1, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp))
        }

    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartClockAlarmTheme {
        ClockAlarmPanel()
    }
}