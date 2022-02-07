package pl.dyzio.smartclockalarm.data

import kotlinx.coroutines.flow.Flow

class NotifyRepository(private val notifyDB: NotifyDB) {

    fun readAllData(limit : Int) : Flow<List<NotifyItem>> = notifyDB.getAll(limit)

    suspend fun addNotify (notifyItem : NotifyItem){
        notifyDB.insert(notifyItem)
    }

    suspend fun updateNotify(notifyItem: NotifyItem){
        notifyDB.update(notifyItem)
    }

    suspend fun deleteNotify(notifyItem: NotifyItem){
        notifyDB.delete(notifyItem)
    }

    suspend fun purgeAllData() {
        notifyDB.purgeTable()
    }

    suspend fun trimData() {
        notifyDB.trimTable()
    }
}