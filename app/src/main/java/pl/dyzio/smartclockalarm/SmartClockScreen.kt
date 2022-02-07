package pl.dyzio.smartclockalarm

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import pl.dyzio.smartclockalarm.ui.elements.ClockAlarmPanel
import pl.dyzio.smartclockalarm.ui.elements.NotificationsPanel
import pl.dyzio.smartclockalarm.ui.elements.SettingsBody

enum class SmartClockScreen(
    val icon : ImageVector,
    val title : Int,
    val body : @Composable ((String) -> Unit) -> Unit)
{

    ClockAlarm (
        icon = Icons.Filled.Alarm,
        title = R.string.appTitle,
        body = { ClockAlarmPanel() }

    ),
    @ExperimentalMaterialApi
    Settings (
        icon = Icons.Filled.Settings,
        title = R.string.Settings,
        body = { SettingsBody() },
    ),
    @ExperimentalFoundationApi
    Notifications (
        icon = Icons.Filled.Notifications,
        title = R.string.notifications,
        body = {NotificationsPanel()}
    );



    @Composable
    fun Content(onScreenChange: (String) -> Unit){
        body(onScreenChange)
    }
}