package pl.dyzio.smartclockalarm

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.ktor.utils.io.errors.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pl.dyzio.smartclockalarm.data.NotifyDB
import pl.dyzio.smartclockalarm.data.NotifyDataBase
import pl.dyzio.smartclockalarm.data.NotifyItem
import java.util.*

@RunWith (AndroidJUnit4::class)
class NotifyDBTest {

    private lateinit var notifyDB: NotifyDB
    private lateinit var db : NotifyDataBase

    @Before
    fun createDB()    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, NotifyDataBase::class.java).allowMainThreadQueries().build()

        notifyDB = db.notifyDB()


    }

    @After
    @Throws (IOException::class)
    fun closeDB()
    {
        db.close()
    }

    @Test
    @Throws (Exception::class)
    fun insertDBAndView()
    {

            val itemNotify = NotifyItem( notifyId = 0, notifyActive = false, notifyDateTime = Calendar.getInstance().time, notifyDone = true, notifyText = "Call Found")
            val values = notifyDB.getCount()
            if (values == 0)
            {
                notifyDB.insert(item = itemNotify)
            }

            itemNotify.notifyActive = true
            itemNotify.notifyId = 0
            itemNotify.notifyDateTime = Calendar.getInstance().time
            notifyDB.insert(itemNotify)
            itemNotify.notifyText = "Uncalled"
            itemNotify.notifyId = 0
            itemNotify.notifyDateTime = Calendar.getInstance().time
            notifyDB.insert(itemNotify)

            if (notifyDB.getCount() > 0) {
                val allNotes = notifyDB.getAllLocal();
                allNotes.forEach {
                    Log.e("INF", it.notifyText ?: "Unknown" + it.notifyId)
                }
            }
            else
            {
                Assert.fail("Insertions failed")
            }
    }

}