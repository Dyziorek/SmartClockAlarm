package pl.dyzio.smartclockalarm


import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import pl.dyzio.smartclockalarm.data.NotifyDataBase
import pl.dyzio.smartclockalarm.data.NotifyRepository

object SmartClockStates {

    lateinit var database: NotifyDataBase private set

    val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    val notifiesStore by lazy {
        NotifyRepository(
            notifyDB = database.notifyDB()
        )
    }

    fun provide(context: Context)
    {
        database = Room.databaseBuilder(context, NotifyDataBase::class.java, "notify.db").fallbackToDestructiveMigration().build()
    }
}