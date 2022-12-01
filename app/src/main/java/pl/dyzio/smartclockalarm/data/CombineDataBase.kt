package pl.dyzio.smartclockalarm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.dyzio.smartclockalarm.data.notify.NotifyDB
import pl.dyzio.smartclockalarm.data.notify.NotifyItem
import pl.dyzio.smartclockalarm.data.shoplist.ShopItemData
import pl.dyzio.smartclockalarm.data.shoplist.ShopListItem
import pl.dyzio.smartclockalarm.data.shoplist.ShopListItemDao
import pl.dyzio.smartclockalarm.util.Converters

@Database(entities = [NotifyItem::class, ShopListItem::class, ShopItemData::class], version = 1, exportSchema = false)
@TypeConverters(value = [Converters::class, CalendarConverters::class])
abstract class CombineDataBase : RoomDatabase() {

    abstract fun notifyDB() : NotifyDB
    abstract fun shopItemDB() : ShopListItemDao

    companion object {
        private var INST : CombineDataBase? = null

        fun getInst(context: Context) : CombineDataBase {
            synchronized(this){
                var inst = INST

                if (inst == null)
                {
                    inst = Room.databaseBuilder(context, CombineDataBase::class.java, "combine_database").fallbackToDestructiveMigration().build()
                    INST = inst
                }

                return inst
            }

        }
    }
}