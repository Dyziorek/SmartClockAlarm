package pl.dyzio.smartclockalarm

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding

class SmartClockButtonGlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = SmartClockButtonGlanceWidget

    companion object {
        const val TURN_ON_DEEP_LINK: String = "app://turn_on.task"
        const val TURN_OFF_DEEP_LINK: String = "app://turn_off.task"
    }
}

object SmartClockButtonGlanceWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode
        get() = SizeMode.Single

    @Composable
    override fun Content() {
        SmartClockButtonContent()
    }

//    val openActionButton = Intent(
//        Intent.ACTION_VIEW,
//        SmartClockButtonGlanceWidgetReceiver.TURN_ON_DEEP_LINK.toUri(),
//        MainActivity.applicationContext(),
//        MainActivity::class.java
//    )
}

@Composable
fun SmartClockButtonContent() {

    Row(
        modifier = GlanceModifier.fillMaxSize().background(Color.LightGray.copy(0.3f)).cornerRadius(2.dp).padding(2.dp),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    )
    {
        Image(provider = ImageProvider(R.drawable.ic_alarm_on_foreground), contentDescription = "Turn On", modifier = GlanceModifier.clickable(
            actionRunCallback<TurnOffClickAction>()))
      //  Image(provider = ImageProvider(R.drawable.ic_alarm_off_foreground),contentDescription = "Turn Off", modifier = GlanceModifier.clickable(actionRunCallback<TurnOnClickAction>()))
    }
}

