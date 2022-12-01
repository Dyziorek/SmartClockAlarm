package pl.dyzio.smartclockalarm.data.shoplist

class ShopListRepository private constructor(
    private val shopItemDao : ShopListItemDao
) {

    suspend fun createShopList(shopItem: String, shopQty : Int) {
        val shopEntry = ShopListItem(shopItem, shopQty)
        shopItemDao.insertShopItem(shopEntry)
    }

    suspend fun removeShopEntry(shopEntry : ShopListItem){
        shopItemDao.deleteShopItem(shopEntry)
    }

    fun getShopEntries() = shopItemDao.getShopItemList()

    companion object {
        @Volatile private var instance: ShopListRepository? = null

        fun getInstance(shopItemDao: ShopListItemDao) =
            instance ?: synchronized(this){
                instance ?: ShopListRepository(shopItemDao).also { instance = it }
            }
    }
}