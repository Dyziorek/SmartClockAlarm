package pl.dyzio.smartclockalarm.data.shoplist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface ShopItemDao {
    @Query("Select * from shop_item order by shop_name")
    fun getShopItems() : LiveData<ShopItemData>

    @Query("SELECT * FROM shop_item order by shop_category ")
    fun recentShoppedItems() : LiveData<ShopItemData>
}