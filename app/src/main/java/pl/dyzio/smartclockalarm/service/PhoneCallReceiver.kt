package pl.dyzio.smartclockalarm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import java.time.LocalDateTime

abstract class PhoneCallReceiver : BroadcastReceiver() {

    companion object {
        var lastState: Int = TelephonyManager.CALL_STATE_IDLE
        var callStartTime: LocalDateTime? = LocalDateTime.now()
        var savedNumber = ""
        var incoming = false
    }

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.intent.action.NEW_OUTGOING_CALL"))
        {
            savedNumber = intent.extras?.getString("android.intent.extra.PHONE_NUMBER") ?: "Unknown"
        }
        else
        {
            val stateStr = intent.extras?.getString(TelephonyManager.EXTRA_STATE) ?: TelephonyManager.EXTRA_STATE_IDLE
            val number = intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: "Unknown"
            var state: Int = when (stateStr)
            {
            TelephonyManager.EXTRA_STATE_IDLE -> TelephonyManager.CALL_STATE_IDLE
            TelephonyManager.EXTRA_STATE_OFFHOOK ->  TelephonyManager.CALL_STATE_OFFHOOK
            TelephonyManager.EXTRA_STATE_RINGING -> TelephonyManager.CALL_STATE_RINGING
                else -> TelephonyManager.CALL_STATE_IDLE
            }
            onCallStateChanged(context, state, number);
        }
    }

    protected abstract fun onIncomingCallReceived(ctx: Context?, number: String?, start: LocalDateTime?)
    protected abstract fun onIncomingCallAnswered(ctx: Context?, number: String?, start: LocalDateTime?)
    protected abstract fun onIncomingCallEnded(
        ctx: Context?,
        number: String?,
        start: LocalDateTime?,
        end: LocalDateTime?
    )

    protected abstract fun onOutgoingCallStarted(ctx: Context?, number: String?, start: LocalDateTime?)
    protected abstract fun onOutgoingCallEnded(
        ctx: Context?,
        number: String?,
        start: LocalDateTime?,
        end: LocalDateTime?
    )

    protected abstract fun onMissedCall(ctx: Context?, number: String?, start: LocalDateTime?)

    private fun onCallStateChanged(context: Context, state : Int, number : String)
    {
        if (lastState == state){
            return
        }

        when(state)
        {
            TelephonyManager.CALL_STATE_RINGING -> {
                incoming = true
                callStartTime = LocalDateTime.now()
                savedNumber = number
                onIncomingCallReceived(context, number, callStartTime)
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                when {
                    lastState != TelephonyManager.CALL_STATE_RINGING -> {
                        incoming = false
                        callStartTime = LocalDateTime.now()
                        onOutgoingCallStarted(context, savedNumber, callStartTime)
                    }
                    else -> {
                        incoming = true
                        callStartTime = LocalDateTime.now()
                        onIncomingCallAnswered(context, savedNumber, callStartTime)
                    }
                }
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                when {
                    lastState == TelephonyManager.CALL_STATE_RINGING -> {
                        onMissedCall(context, savedNumber, callStartTime)
                    }
                    incoming -> {
                        onIncomingCallEnded(context, savedNumber, callStartTime, LocalDateTime.now())
                    }
                    else -> {
                        onOutgoingCallEnded(context, savedNumber, callStartTime, LocalDateTime.now())
                    }
                }
            }
        }

        lastState = state
    }
}