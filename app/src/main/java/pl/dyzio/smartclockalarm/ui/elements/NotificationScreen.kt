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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.dyzio.smartclockalarm.R
import pl.dyzio.smartclockalarm.viewModel.INotifyViewModel
import pl.dyzio.smartclockalarm.viewModel.NotifyViewModel
import pl.dyzio.smartclockalarm.viewModel.NotifyViewModelMock
import java.text.SimpleDateFormat

@ExperimentalFoundationApi
@Composable
fun NotificationsPanel(viewModel: INotifyViewModel = NotifyViewModel())
{

    val viewState by viewModel.state.collectAsState()

   if (viewState.lastNotifies.isNotEmpty()) {
       LazyColumn {
           stickyHeader {
               Column() {
                   Text(stringResource(R.string.notifications))
                   Spacer(modifier = Modifier.height(5.dp))
               }
           }
           items(viewState.lastNotifies) { itemIdx ->
               Column(modifier = Modifier
                   .fillMaxWidth()
                   .border(BorderStroke(5.dp, Color.DarkGray), RoundedCornerShape(3.dp))
                   .padding(10.dp)) {
                   Row {
                      Icon( modifier = Modifier.size(60.dp).background(color = Color.LightGray, shape =  CircleShape),
                          imageVector = if (itemIdx.notifyActive) Icons.Filled.Power else Icons.Filled.PowerOff,
                          contentDescription = "Power",
                          tint = if (itemIdx.notifyActive) Color.Blue else Color.Red)
                       Column(Modifier.padding(10.dp).fillMaxWidth()) {
                           Text(stringResource(R.string.notifyText) + "${itemIdx.notifyText}")
                           Text(stringResource(R.string.notifyCreate) + "${SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(itemIdx.notifyDateTime!!)}")
                       }
                   }

                   Spacer(Modifier.height(5.dp))
               }
           }
       }
   }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreviewNotes() {
    NotificationsPanel(NotifyViewModelMock())
}