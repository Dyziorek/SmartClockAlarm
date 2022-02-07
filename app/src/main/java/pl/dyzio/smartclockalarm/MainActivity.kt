package pl.dyzio.smartclockalarm

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import pl.dyzio.smartclockalarm.data.NotifyDB
import pl.dyzio.smartclockalarm.data.NotifyDataBase
import pl.dyzio.smartclockalarm.ui.elements.SmartClockTabRow
import pl.dyzio.smartclockalarm.ui.theme.SmartClockAlarmTheme


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var notifyDB: NotifyDB
    private lateinit var db : NotifyDataBase

    init {
        instance = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        db = Room.databaseBuilder(applicationContext, NotifyDataBase::class.java, "notify_db").fallbackToDestructiveMigration().build()

        notifyDB = db.notifyDB()
        super.onCreate(savedInstanceState)
        setContent {
            AlarmClockApp()
        }
    }

    companion object {
        private var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

        fun applicationDb() : NotifyDB {
            return  instance!!.notifyDB
        }
    }
}



@Composable
fun AlarmClockApp() {

    SmartClockAlarmTheme {
        val allPanels = SmartClockScreen.values().toList()
        var currentPanel by rememberSaveable { mutableStateOf( SmartClockScreen.ClockAlarm)}
        // A surface container using the 'background' color from the theme
        Scaffold(backgroundColor =  MaterialTheme.colors.background,
            topBar = {
                SmartClockTabRow(allScreens = allPanels,
                    onTabSelected = { panel -> currentPanel = panel},
                    currentScreen = currentPanel)
            }

        ) { paddingValue ->
            Box(modifier = Modifier.padding(paddingValue)) {
                currentPanel.Content(onScreenChange = {
                    panel -> currentPanel = SmartClockScreen.valueOf(panel)
                })
            }
        }
    }

}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AlarmClockApp()
}