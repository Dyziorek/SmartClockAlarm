package pl.dyzio.smartclockalarm

import android.app.Application

class SmartClockAlarmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SmartClockStates.provide(this)
    }
}