package pl.dyzio.smartclockalarm.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Shop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.dyzio.smartclockalarm.R
import pl.dyzio.smartclockalarm.viewModel.IShopListViewModel
import pl.dyzio.smartclockalarm.viewModel.ShopListViewModel
import pl.dyzio.smartclockalarm.viewModel.ShopListViewModelMock

@ExperimentalFoundationApi
@Composable
fun ShopListPanel(viewModel: IShopListViewModel = ShopListViewModel())  {
    val viewState by viewModel.state.collectAsState()
Scaffold (
    floatingActionButton = {
        FloatingActionButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.Add, "Add Item")
        }
    }
    ){
    val checkPadding = it
    if (viewState.shopItems?.isEmpty() == false) {
        LazyColumn(Modifier.padding(checkPadding)) {
            stickyHeader {
                Column {
                    Text(stringResource(R.string.shopListItems))
                }
            }
            viewState.shopItems?.let { itemList ->
                items (itemList) {  itemEntry ->
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(5.dp, Color.DarkGray), RoundedCornerShape(3.dp))
                        .padding(10.dp)) {
                        Row {
                            Icon( modifier = Modifier.size(60.dp).background(color = Color.LightGray, shape =  CircleShape),
                                imageVector = Icons.Filled.Shop,
                                contentDescription = "Shop"
                                )
                            Column(Modifier.padding(10.dp).fillMaxWidth()) {
                                Text(stringResource(R.string.notifyText) + "${itemEntry.shopItem.shopDataName}")
                                Text(stringResource(R.string.notifyCreate) )
                            }
                        }

                        Spacer(Modifier.height(5.dp))
                    }

                }
            }
        }
    }
}
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultShopList() {
    ShopListPanel(ShopListViewModelMock())
}