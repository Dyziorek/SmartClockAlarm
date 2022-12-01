package pl.dyzio.smartclockalarm.data.shoplist

import androidx.room.*
import java.util.*

@Entity(
    tableName = "my_shop_list",
    foreignKeys = [
        ForeignKey ( entity = ShopItemData::class, parentColumns = ["id"], childColumns = ["shop_item_code"])],
    indices = [Index("shop_item_code")]
)
data class ShopListItem(
    @ColumnInfo( name= "shop_item_code")
    var shopItemCode: String,

    @ColumnInfo( name = "shop_item_qty")
    var shopItemQty: Int
){
    @ColumnInfo(name = "shop_id")
    @PrimaryKey(autoGenerate = true)
    var shopItemId : Long = 0L

    @ColumnInfo( name = "shop_item_completed_date")
    var shopItemDoneDate: Calendar = Calendar.getInstance()

    @ColumnInfo( name = "shop_item_completed")
    var shopItemDone: Boolean = false
}



