package pl.dyzio.smartclockalarm.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dyzio.smartclockalarm.SmartClockStates
import pl.dyzio.smartclockalarm.data.notify.NotifyItem
import pl.dyzio.smartclockalarm.data.notify.NotifyRepository
import java.time.Instant
import java.util.*

abstract class INotifyViewModel(
) : ViewModel() {
    abstract val _state : MutableStateFlow<NotifyViewState>

    val state: StateFlow<NotifyViewState>
        get() = _state
}

class NotifyViewModel(
    private val notifyStore: NotifyRepository = SmartClockStates.notifiesStore
) : INotifyViewModel() {
    override val _state: MutableStateFlow<NotifyViewState> = MutableStateFlow(NotifyViewState())

    init {
        viewModelScope.launch {
            val lastNotifies = notifyStore.readAllData(20)
            lastNotifies.collect {
                _state.value = NotifyViewState(it)
            }

        }
    }

    fun insert(notifyItem : NotifyItem) {
        viewModelScope.launch(Dispatchers.IO) {
            notifyStore.addNotify(notifyItem)
        }
    }

    fun update(notifyItem : NotifyItem) {
        viewModelScope.launch(Dispatchers.IO) {
            notifyStore.updateNotify(notifyItem)
        }
    }

    fun delete(notifyItem: NotifyItem) {
        viewModelScope.launch(Dispatchers.IO) {
            notifyStore.deleteNotify(notifyItem)
        }
    }


}

data class NotifyViewState(
    val lastNotifies : List<NotifyItem> = emptyList()
)

fun Int.odd(): Boolean {
    return this % 2 == 1
}

class NotifyViewModelMock : INotifyViewModel()
{
    override val _state = MutableStateFlow(NotifyViewState())

    init {
        val lastNotifies : MutableList<NotifyItem> = mutableListOf()
        for(i in 1..3)
        {
            lastNotifies.add(NotifyItem(i.toLong(),  "Notify with id $i", Date.from(Instant.now()), i.odd(), !i.odd()))
        }
       _state.value = NotifyViewState(lastNotifies)
    }
}
//class NotifyViewFactory (
//    private val app : Application
//        ) : ViewModelProvider.Factory {
//    @Suppress ("UNCHECKED_CAST")
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(NotifyViewModel::class.java)){
//            return NotifyViewModel() as T
//        }
//        throw IllegalArgumentException("Incorrect View Model")
//    }
//
//}