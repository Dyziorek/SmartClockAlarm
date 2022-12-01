package pl.dyzio.smartclockalarm.data.shoplist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_item")
data class ShopItemData(
    @PrimaryKey
    @ColumnInfo(name = "id") val shopID:String,

    @ColumnInfo(name = "shop_name")
    val shopDataName: String,

    @ColumnInfo(name = "shop_category")
    val shopDataCategory: String
)
