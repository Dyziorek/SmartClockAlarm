package pl.dyzio.smartclockalarm.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
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
               Column(modifier = Modifier.fillMaxWidth().border(BorderStroke(5.dp, Color.DarkGray), RoundedCornerShape(3.dp)).padding(10.dp)) {
                   Text(stringResource(R.string.notifyId) + "${itemIdx.notifyId}")
                   Text(stringResource(R.string.notifyText) + "${itemIdx.notifyText}")
                   Text(stringResource(R.string.notifyActive) + "${itemIdx.notifyActive}")
                   Text(stringResource(R.string.notifyCreate) + "${itemIdx.notifyDateTime}")
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