package pl.dyzio.smartclockalarm.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "my_notifications")
data class NotifyItem(

    @PrimaryKey (autoGenerate = true)
    var notifyId: Long = 0L,

    @ColumnInfo (name = "text")
    var notifyText: String?,

    @ColumnInfo(name = "date")
    var notifyDateTime: Date?,

    @ColumnInfo(name = "done")
    var notifyDone: Boolean,

    @ColumnInfo(name = "active")
    var notifyActive: Boolean
)
