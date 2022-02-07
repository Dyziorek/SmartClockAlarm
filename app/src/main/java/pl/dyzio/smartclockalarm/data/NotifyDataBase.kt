package pl.dyzio.smartclockalarm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.dyzio.smartclockalarm.util.Converters

@Database(entities = [NotifyItem::class], version = 1, exportSchema = false)
@TypeConverters(value = [Converters::class])
abstract class NotifyDataBase : RoomDatabase() {

    abstract fun notifyDB() : NotifyDB

    companion object {
        private var INST : NotifyDataBase? = null

        fun getInst(context: Context) : NotifyDataBase {
            synchronized(this){
                var inst = INST

                if (inst == null)
                {
                    inst = Room.databaseBuilder(context, NotifyDataBase::class.java, "notify_database").fallbackToDestructiveMigration().build()
                    INST = inst
                }

                return inst
            }

        }
    }
}