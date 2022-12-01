package pl.dyzio.smartclockalarm.data.shoplist

import androidx.room.Embedded
import androidx.room.Relation

/**
 * This class captures the relationship between a [ShopItemData] and a user's [ShopListItem], which is
 * used by Room to fetch the related entities.
 */
data class ShopItemAndListItems(
    @Embedded
    val shopItem : ShopItemData,

    @Relation(parentColumn = "id", entityColumn = "shop_item_code")
    val shopItemList : List<ShopListItem> = emptyList()
)
