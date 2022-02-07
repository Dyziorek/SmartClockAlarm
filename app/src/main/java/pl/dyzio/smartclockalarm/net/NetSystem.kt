package pl.dyzio.smartclockalarm.net

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketTimeoutException


class NetSystem private constructor(port: Int, linkAddress: String) {

    var smartPlugSockets = emptyList<InetAddress?>()
    var netAddress : InetAddress? = null
    private var netPrefixes : Short? = null
    val plugPort: Int = port
    private val localAddress: String = linkAddress

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is SocketTimeoutException) {
            Log.i("NET", "getNetworkIPs not a plug: " + throwable.message, throwable);
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO )

    constructor(port: Int) : this(port, ""){
        detectSubnetMask()
        getNetworkIP()
    }

    private fun checkInetAddress(address: InetAddress?, number: Int) : InetAddress?
    {
        Log.i("NET", "--- Check Inet address ${address.toString()} on $number")
        var ipv = address?.address
        ipv?.set(3, number.toByte())
        var localAddress = InetAddress.getByAddress(ipv) ?: InetAddress.getByAddress(byteArrayOf(127, 0,0,1))
        if (localAddress.isReachable(500))
            return localAddress

        return null
    }

    private fun getNetworkIP() {
        Log.i("NET", "--- Async Network calls with callback")
        runBlocking (Dispatchers.IO) {
            val channel = Channel<InetAddress?>()
            for (idx in 1..254) {
                launch {
                    Log.i("NET", "--- Async Launch Network  in thread ${Thread.currentThread().name} calls with callback on $idx")
                     channel.send(checkInetAddress(netAddress, idx))
                }
            }
            var allResults = emptyList<InetAddress?>()
            repeat(254){
                val result = channel.receive()
                Log.i("NET", "--- Receive from thread ${Thread.currentThread().name} data ${result?.toString() ?: "null"}")
                if (result != null)
                {
                    allResults = allResults + result
                }
            }

            val channelData = Channel<Pair<String, InetAddress?>>()
            for( elem in allResults)
            {
                Log.i("NET", "--- Connecting from thread ${Thread.currentThread().name} data ${elem?.toString()}")
                launch { channelData.send(Pair(NetCommand.info(elem, 9999, 1000), elem)) }
            }

            repeat(allResults.size)
            {
                val infoData = channelData.receive()
                Log.i("NET", "--- Receive socketData from thread ${Thread.currentThread().name} data ${infoData.first}")
                if (infoData.first.isNotEmpty())
                {
                    smartPlugSockets = smartPlugSockets + infoData.second
                }
            }
        }
    }

    private fun detectSubnetMask() {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()){
                val netInfo = interfaces.nextElement()
                if (netInfo.isLoopback)
                    continue
                val interfaceType = netInfo.interfaceAddresses
                for (item in interfaceType) {
                    if (item.broadcast == null) continue

                    Log.i("INFO", "Found Adepter Address ${item.address}")
                    netAddress = item.address
                    netPrefixes = item.networkPrefixLength
                    return
                }
            }
        }
        catch (err : Exception) {
                Log.e("ERROR", err.message ?: "Unknown Message")
        }
    }

    companion object {
        @Volatile private var instance : NetSystem? = null
        fun getInstance(plugPort: Int, localAddress: String) : NetSystem  =
            instance ?: synchronized(this)
            {
                instance ?: NetSystem(plugPort, localAddress).also { instance = it }
            }


        fun instanceLookup(plugPort: Int) : NetSystem =
            instance ?: synchronized(this)
            {
                instance ?: NetSystem(plugPort).also { instance = it }
            }
    }
}




