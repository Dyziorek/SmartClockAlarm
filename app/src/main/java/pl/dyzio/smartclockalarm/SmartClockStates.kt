package pl.dyzio.smartclockalarm


import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import pl.dyzio.smartclockalarm.data.CombineDataBase
import pl.dyzio.smartclockalarm.data.notify.NotifyRepository
import pl.dyzio.smartclockalarm.data.shoplist.ShopListRepository
import kotlin.random.Random

object SmartClockStates {

    lateinit var database: CombineDataBase private set

    val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    val notifiesStore by lazy {
        NotifyRepository(
            notifyDB = database.notifyDB()
        )
    }

    val shopListStore by lazy {
        ShopListRepository.getInstance(database.shopItemDB())
    }

    const val CHANNEL_ID : String = "SMARTCLOCK_NOTES"

    var notificationID : Int = Random.Default.nextInt()

    fun provide(context: Context)
    {
        database = Room.databaseBuilder(context, CombineDataBase::class.java, "notify.db").fallbackToDestructiveMigration().build()
    }
}