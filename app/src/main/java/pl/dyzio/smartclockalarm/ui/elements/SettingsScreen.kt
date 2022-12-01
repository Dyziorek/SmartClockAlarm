package pl.dyzio.smartclockalarm.ui.elements

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.google.accompanist.permissions.*
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import kotlinx.coroutines.launch
import pl.dyzio.smartclockalarm.MainActivity
import pl.dyzio.smartclockalarm.R
import pl.dyzio.smartclockalarm.dataStore
import pl.dyzio.smartclockalarm.net.NetSystem
import pl.dyzio.smartclockalarm.service.AlarmReceiver
import pl.dyzio.smartclockalarm.ui.MaskTransforms.MaskIPTransformation
import pl.dyzio.smartclockalarm.ui.MaskTransforms.MaskTransformation
import pl.dyzio.smartclockalarm.ui.MaskTransforms.PortNumberMask
import pl.dyzio.smartclockalarm.ui.theme.SmartClockAlarmTheme
import pl.dyzio.smartclockalarm.util.*
import java.util.*


@ExperimentalMaterialApi
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsBody(mocking : Boolean = false){
    if (mocking)
    {
        SettingsBodyMain()
    }
    else {
        val callTrackingPermissionApi = rememberPermissionState(permission = android.Manifest.permission.READ_PHONE_STATE)

        PermissionRequired(
            permissionState = callTrackingPermissionApi,
            permissionNotGrantedContent = {
                Rationale(
                    onRequestPermission = {
                        callTrackingPermissionApi.launchPermissionRequest()
                    }
                )
            },
            permissionNotAvailableContent = { PermissionDenied() })
        {
            SettingsBodyMain()
        }
    }
}

@Composable
fun Rationale( onRequestPermission : () -> Unit){
    Column{
        Text(stringResource(R.string.permission_request_reason))
        Spacer(modifier = Modifier.height(10.dp))
        Row{
            Button (onClick = onRequestPermission){
                Text (stringResource(R.string.permission_request))
            }
        }
    }
}

@Composable
fun PermissionDenied(){
    val context = LocalContext.current
    Column{
        Text(stringResource(R.string.permission_deny_info))
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            MainActivity.applicationContext().startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
            )
        }) {
            Text(stringResource(R.string.open_settings))

        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SettingsBodyMain() {
    val context = LocalContext.current
    val dataStore = context.dataStore
    val dataManager = remember { DataStoreManager(dataStore) }
    val scope = rememberCoroutineScope()
    val enableRest by dataManager.getPreferenceFlow(MonitorSwitch).collectAsState(initial = false)
    val enableAlarm by dataManager.getPreferenceFlow(AlarmActive).collectAsState(initial = false)
    val prefs by dataManager.preferenceFlow.collectAsState(initial = null)


    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    )
    {
        item {
            ListItem(
                modifier = Modifier.padding(0.dp),
                text = {
                    Text(
                        text = stringResource(id = R.string.monitor_calls),
                        style = MaterialTheme.typography.caption,
                        maxLines = 1 , modifier = Modifier.width(100.dp)
                    )
                },
                icon = {
                       Icon(Icons.Filled.Alarm,
                       contentDescription = null)
                },
                trailing = {
                    Switch(
                        checked = prefs?.get(MonitorSwitch.key) ?: MonitorSwitch.defaultValue,
                        onCheckedChange = {
                            newVal ->
                                scope.launch { dataManager.editPreference(MonitorSwitch.key, newValue = newVal) }
                        }
                    )
                }
            )
        }
        item {
            ListItem(
                modifier = Modifier.padding(0.dp),
                icon = {
                    Icon(Icons.Filled.AlarmOn,
                        contentDescription = null)
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.monitor_start_time), modifier = Modifier.width(150.dp),
                        style = MaterialTheme.typography.caption
                    )
                },
                trailing = {
                    Row() {

                        TextField(
                            modifier = Modifier.width(140.dp),
                            value = (prefs?.get(MonitorStart.key)
                                ?: MonitorStart.defaultValue).split(":").joinToString(""),
                            onValueChange = { newVal ->
                                scope.launch {
                                    dataManager.editPreference(
                                        MonitorStart.key,
                                        newValue = newVal
                                    )
                                }
                            },
                            enabled = enableRest,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = MaskTransformation(),
                            trailingIcon = {
                                Icon(Icons.Default.AlarmAdd, "D&T", Modifier.clickable {
                                    showDatePicker(context as AppCompatActivity, prefs?.get(MonitorStart.key)
                                        ?: MonitorStart.defaultValue) {
                                        scope.launch {
                                            dataManager.editPreference(
                                                MonitorStart.key,
                                                newValue = it
                                            )
                                        }
                                    }
                                })
                            }
                        )
                    }
                }
            )
        }
        item {
            ListItem(
                text = {
                Text(text = stringResource(id = R.string.monitor_end_time), modifier = Modifier.width(150.dp),
                    style = MaterialTheme.typography.caption)
            },
                icon = {
                    Icon(Icons.Filled.AlarmOff,
                        contentDescription = null)
                },
            trailing = {
                TextField(
                    modifier = Modifier.width(140.dp),
                    value = (prefs?.get(MonitorEnd.key) ?: MonitorEnd.defaultValue),
                    onValueChange = { newVal ->
                        scope.launch { dataManager.editPreference(MonitorEnd.key, newValue = newVal) }
                    },
                    enabled = enableRest,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = MaskTransformation(),
                    trailingIcon = {
                        Icon(Icons.Default.AlarmAdd, "D&T", Modifier.clickable {
                            showDatePicker(context as AppCompatActivity, prefs?.get(MonitorEnd.key) ?: MonitorEnd.defaultValue) {
                                scope.launch {
                                    dataManager.editPreference(
                                        MonitorEnd.key,
                                        newValue = it
                                    )
                                }
                            }
                        })
                    }
                )
            })
        }
        item {
            ListItem( text = {
                Text(text = stringResource(id = R.string.smartplug_host),style = MaterialTheme.typography.caption, modifier = Modifier.width(150.dp))
            },

                icon = {
                    Icon(Icons.Filled.Computer,
                        contentDescription = null)
                },
                trailing = {
                    TextField(
                        modifier = Modifier.width(140.dp),
                        value = (prefs?.get(PlugHost.key) ?: PlugHost.defaultValue).split(".").joinToString(""),
                        onValueChange = { newVal ->
                            scope.launch { dataManager.updatePlugHost(newVal) }
                        },
                        enabled = enableRest,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = MaskIPTransformation()
                    )
                })
        }
        item {
            ListItem( text = {
                Text(
                    style = MaterialTheme.typography.caption,
                    text = stringResource(id = R.string.smartplug_port),
                    textAlign = TextAlign.Right
                )
            },

                icon = {
                    Icon(Icons.Filled.Plumbing,
                        contentDescription = null)
                },
            trailing = {
                TextField(
                    modifier = Modifier.width(140.dp),
                    value = prefs?.get(PlugPort.key) ?: PlugPort.defaultValue,
                    onValueChange = { newVal ->
                        val plugPort = when
                        {
                            newVal.length > 4 -> newVal.take(4)
                            newVal.length < 4 -> newVal.padStart(4, '0')
                            else -> newVal
                        }
                        scope.launch { dataManager.editPreference(PlugPort.key, newValue = plugPort) }
                    },
                    enabled = enableRest,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PortNumberMask()
                )
            })
        }
        item {
            ListItem(
                modifier = Modifier.padding(0.dp),
                text = {
                    Text(
                        text = stringResource(id = R.string.detect_smartPlug),
                        style = MaterialTheme.typography.caption,
                        maxLines = 1 , modifier = Modifier.width(100.dp)
                    )
                },
                icon = {
                    Icon(Icons.Filled.Alarm,
                        contentDescription = null)
                },
                trailing = {
                    Button( onClick = {
                        var value = prefs?.get(PlugPort.key) ?: PlugPort.defaultValue
                        scope.launch {
                            val plugHosts = NetSystem.instanceLookup(value.toInt()).smartPlugSockets
                            val plugString = when
                            {
                                plugHosts.isEmpty() -> "0.0.0.0"
                                plugHosts.isNotEmpty() -> plugHosts[0].toString()
                                else -> "0.0.0.0"
                            }
                            var internetAddress = plugString.filter {
                                    charTest -> when (charTest) {
                                in '0'..'9' -> true
                                '.' -> true
                                else -> false
                                }
                            }
                            dataManager.updatePlugHost(internetAddress)
                        }
                    })
                    {
                        Icon(Icons.Filled.DeveloperBoard, contentDescription = null)
                    }
                }
            )
        }
        item {
            ListItem(
                modifier = Modifier.padding(0.dp),
                text = {
                    Text(
                        text = stringResource(id = R.string.alarm_active),
                        style = MaterialTheme.typography.caption,
                        maxLines = 1 , modifier = Modifier.width(100.dp)
                    )
                },
                icon = {
                    Icon(Icons.Filled.Alarm,
                        contentDescription = null)
                },
                trailing = {
                    Switch(
                        checked = prefs?.get(AlarmActive.key) ?: AlarmActive.defaultValue,
                        onCheckedChange = {
                                newVal ->
                            scope.launch { dataManager.editPreference(AlarmActive.key, newValue = newVal) }
                        }
                    )
                }
            )
        }
        item {
            ListItem(
                modifier = Modifier.padding(0.dp),
                text = {
                    Text(
                        text = stringResource(R.string.alarm_time),
                        style = MaterialTheme.typography.caption,
                        maxLines = 1 , modifier = Modifier.width(100.dp)
                    )
                },
                icon = {
                    Icon(Icons.Filled.Alarm,
                        contentDescription = null)
                },
                trailing = {
                    TextField(
                        modifier = Modifier.width(140.dp),
                        value = (prefs?.get(AlarmTime.key) ?: AlarmTime.defaultValue),
                        onValueChange = { newVal ->
                            scope.launch { dataManager.editPreference(MonitorEnd.key, newValue = newVal) }
                        },
                        enabled = enableAlarm,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = MaskTransformation(),
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, "D&T",
                                Modifier.clickable(
                                    enabled = enableAlarm
                                ) {
                                showDatePicker(context as AppCompatActivity, prefs?.get(AlarmTime.key) ?: AlarmTime.defaultValue) {
                                    scope.launch {
                                        dataManager.editPreference(
                                            AlarmTime.key,
                                            newValue = it
                                        )
                                        if (it.toInt() != 0) {
                                            val alarmManager =
                                                context.getSystemService(ALARM_SERVICE) as AlarmManager
                                            val intent = Intent(context, AlarmReceiver::class.java)
                                            var pendingIntent =
                                                PendingIntent.getBroadcast(context, 991, intent, 0);
                                            val calendar = Calendar.getInstance()
                                            calendar.set(Calendar.HOUR_OF_DAY, it.take(2).toInt())
                                            calendar.set(Calendar.MINUTE, it.takeLast(2).toInt())
                                            var time =
                                                calendar.timeInMillis - calendar.timeInMillis % 60000
                                            if (System.currentTimeMillis() > time) {
                                                time += if (Calendar.AM_PM == 0) 1000 * 60 * 60 * 12 else 1000 * 60 * 60 * 24
                                            }
                                            alarmManager.setAndAllowWhileIdle(
                                                AlarmManager.RTC_WAKEUP,
                                                time,
                                                pendingIntent
                                            )
                                        }
                                    }
                                }
                            })
                        }
                    )
                }

            )
        }
    }
}


fun showDatePicker(appContext: AppCompatActivity, initialValue : String?, updatedData: (date: String) -> Unit)
{

    fun funCheck (checkVal : String, ranger : IntRange) : Int {
        if (checkVal.isNotEmpty() && checkVal.isDigitsOnly()) {
            return checkVal.filterIndexed{ind , _ -> ind in ranger}.toInt()
        }
        return 0
    }

    val hour = initialValue?.let { funCheck(it, 0..1) } ?: 0
    val minute = initialValue?.let{ funCheck(it, 2..3) } ?: 0
    val picker = MaterialTimePicker.Builder().setTimeFormat(CLOCK_24H).setHour(hour).setMinute(minute).build()
    picker.show(appContext.supportFragmentManager, "Time Pick")
    picker.addOnPositiveButtonClickListener {
        updatedData("${picker.hour.toString().padStart(2,'0')}${picker.minute.toString().padStart(2,'0')}")
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SmartClockAlarmTheme {
        SettingsBody(true)
    }
}