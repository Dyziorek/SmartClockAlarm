package pl.dyzio.smartclockalarm.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dyzio.smartclockalarm.SmartClockStates
import pl.dyzio.smartclockalarm.data.shoplist.ShopItemAndListItems
import pl.dyzio.smartclockalarm.data.shoplist.ShopItemData
import pl.dyzio.smartclockalarm.data.shoplist.ShopListRepository


abstract class IShopListViewModel() : ViewModel()
{
    abstract val state : MutableStateFlow<ShopListViewState>

    val localState: StateFlow<ShopListViewState>
        get() = state
}

class ShopListViewModel(private val shopStore : ShopListRepository = SmartClockStates.shopListStore) : IShopListViewModel() {
    override val state: MutableStateFlow<ShopListViewState>
        get() = MutableStateFlow(ShopListViewState())

    init {
        viewModelScope.launch {
            val shopList = shopStore.getShopEntries()
            state.value = ShopListViewState(shopList.value)
        }
    }
}


data class ShopListViewState(
    val shopItems : List<ShopItemAndListItems>? = emptyList()
)

class ShopListViewModelMock : IShopListViewModel()
{
    override val state = MutableStateFlow(ShopListViewState())

    init {
        val shopItems : MutableList<ShopItemAndListItems> = mutableListOf()
        for(i in 1..3)
        {
            val itemData = ShopItemData("Apple", "Apple PIE", "Cake")
            shopItems.add(ShopItemAndListItems(shopItem = itemData))
        }
        state.value = ShopListViewState(shopItems)
    }
}