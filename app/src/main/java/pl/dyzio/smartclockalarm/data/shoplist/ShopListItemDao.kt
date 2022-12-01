package pl.dyzio.smartclockalarm.data.shoplist

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShopListItemDao {
    @Query("SELECT * FROM my_shop_list")
    fun getShopList() : LiveData<List<ShopListItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM my_shop_list WHERE shop_id = :shopID LIMIT 1)")
    fun hasAddedToShopList(shopID: String): LiveData<Boolean>

    @Transaction
    @Query("SELECT * FROM shop_item WHERE id IN (SELECT DISTINCT(shop_ID) FROM my_shop_list)")
    fun getShopItemList() : LiveData<List<ShopItemAndListItems>>

    @Insert
    suspend fun insertShopItem(shopItem : ShopListItem) : Long

    @Delete
    suspend fun deleteShopItem(shopItem: ShopListItem)
}