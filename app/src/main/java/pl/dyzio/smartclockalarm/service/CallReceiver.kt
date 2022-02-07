package pl.dyzio.smartclockalarm.service

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.dyzio.smartclockalarm.util.callReceived
import java.time.LocalDateTime
import java.time.ZoneId

class CallReceiver : PhoneCallReceiver() {
    override fun onIncomingCallReceived(ctx: Context?, number: String?, start: LocalDateTime?) {
        CoroutineScope(Dispatchers.Main).launch(Dispatchers.Default, CoroutineStart.DEFAULT) {
            callReceived(
                ctx,
                number,
                java.util.Date.from(start?.atZone(ZoneId.systemDefault())?.toInstant())
            )
        }

    }

    override fun onIncomingCallAnswered(ctx: Context?, number: String?, start: LocalDateTime?) {
       // TODO("Not yet implemented")
    }

    override fun onIncomingCallEnded(
        ctx: Context?,
        number: String?,
        start: LocalDateTime?,
        end: LocalDateTime?
    ) {
        //TODO("Not yet implemented")
    }

    override fun onOutgoingCallStarted(ctx: Context?, number: String?, start: LocalDateTime?) {
        //TODO("Not yet implemented")
    }

    override fun onOutgoingCallEnded(
        ctx: Context?,
        number: String?,
        start: LocalDateTime?,
        end: LocalDateTime?
    ) {
        //TODO("Not yet implemented")
    }

    override fun onMissedCall(ctx: Context?, number: String?, start: LocalDateTime?) {
        //TODO("Not yet implemented")
    }
}