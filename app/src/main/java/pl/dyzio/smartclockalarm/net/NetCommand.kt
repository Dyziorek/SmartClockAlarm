package pl.dyzio.smartclockalarm.net

import android.util.Log
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.network.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.SocketTimeoutException
import java.nio.ByteBuffer

fun decryptData(encoded: ByteBuffer) : String {
    val arrData = encoded.array()
    return decryptData(arrData)
}

fun decryptData(encryptedData: ByteArray) : String{
    var seed = -85

    var output = ByteBuffer.allocate(encryptedData.size + 1)
    for( idx in encryptedData.indices )
    {
        val xorVal  = seed xor encryptedData[idx].toInt()
        seed = encryptedData[idx].toInt()
        output.put(xorVal.toByte())
    }
    val paddedDecrypt = output.array().takeLast(output.array().size - 5).toByteArray()
    return String(paddedDecrypt, Charsets.UTF_8)
}

fun Int.to4ByteArrayBE() : ByteArray = byteArrayOf(toByte(), shr(8).toByte(), shr(16).toByte(), shr(24).toByte())
fun Int.to4ByteArrayLE() : ByteArray = byteArrayOf(shr(24).toByte(), shr(16).toByte(), shr(8).toByte(), toByte() )

fun encryptData(inputText : String) : ByteBuffer {
    val arrText = inputText.toByteArray()
    var xorCrypt = ByteBuffer.allocate(arrText.size + 4).put(arrText.size.to4ByteArrayLE())
    var seed = -85
    for (idx in arrText.indices)
    {
        val xorVal = seed xor arrText[idx].toInt()
        seed = xorVal
        xorCrypt.put((xorVal.toByte()))
    }
    return xorCrypt
}

class NetCommand {


    companion object {
        var commands : MutableMap<String, String> = mutableMapOf<String, String>()

        init {
                commands["info"] = "{\"system\":{\"get_sysinfo\":{}}}"
                commands["on"] = "{\"system\":{\"set_relay_state\":{\"state\":1}}}"
                commands["off"] = "{\"system\":{\"set_relay_state\":{\"state\":0}}}"
                commands["ledoff"] = "{\"system\":{\"set_led_off\":{\"off\":1}}}"
                commands["ledon"] = "{\"system\":{\"set_led_off\":{\"off\":0}}}"
                commands["cloudinfo"] = "{\"cnCloud\":{\"get_info\":{}}}"
                commands["wlanscan"] = "{\"netif\":{\"get_scaninfo\":{\"refresh\":0}}}"
                commands["time"] = "{\"time\":{\"get_time\":{}}}"
                commands["schedule"] = "{\"schedule\":{\"get_rules\":{}}}"
                commands["countdown"] = "{\"count_down\":{\"get_rules\":{}}}"
                commands["antitheft"] = "{\"anti_theft\":{\"get_rules\":{}}}"
                commands["reboot"] = "{\"system\":{\"reboot\":{\"delay\":1}}}"
                commands["reset"] = "{\"system\":{\"reset\":{\"delay\":1}}}"
                commands["energy"] = "{\"emeter\":{\"get_realtime\":{}}}"
        }

        private suspend fun command(address: InetAddress?, port : Int , timeOut : Int, commandText: String) : String {
            try {
                val selector = ActorSelectorManager(Dispatchers.IO)
                val socket = aSocket(selector).tcp().connect(InetSocketAddress(address, port)) {
                    if (timeOut > 0)
                        socketTimeout = timeOut.toLong()
                }
                Log.e("INFO", "Socket connection from ${socket.localAddress.toString()} to ${socket.remoteAddress.toString()} on port ${socket.remoteAddress.port}")
                val input = socket.openReadChannel()
                val commandsOut = socket.openWriteChannel(autoFlush = true)
                val commandTextValue = commands[commandText].orEmpty()
                Log.e("INFO", "Send Command text is $commandTextValue")
                val byteInput = encryptData(commandTextValue).array()

                commandsOut.writeAvailable(byteInput, 0, byteInput.size)
                Log.e("INFO","Send Data")
                commandsOut.flush();
                Log.e("ERR" ,"Try Receive Data")
                input.awaitContent()
                var bufferData = ByteArray(input.availableForRead)


               // input.readUTF8LineTo(System.out)
               var buffReturn = input.readAvailable(bufferData)
                Log.e("ERR" ,"Received Data $buffReturn")
                while (buffReturn > 0)
                {
                    Log.e("ERR" ,"To Decode Data ${bufferData.take(10)}")
                    val decoded = decryptData(bufferData)
                    Log.e("ERR" ,"Decoded Data $decoded")
                    if (decoded.takeLast(2) == "}") {
                        Log.e("ERR" ,"Decoded Data $decoded")
                        break;
                    }
                    if (input.availableForRead > 0) {
                        buffReturn = input.readAvailable(bufferData)
                    }
                    else
                        break;
                }
                Log.e("ERR" ,"Return Data ${buffReturn > 0}")
//                val bufferSize = input.readAvailable(1) { bufferData = bufferData.put(it)}
//                Log.e("INFO", "Try Receive Data got ${bufferSize}")
                commandsOut.close()
                socket.close()
                if (buffReturn > 0) {
                    return decryptData(bufferData)
                }
                return ""
            }
            catch (exception : SocketTimeoutException)
            {
                return ""
            }
            catch (connect: ConnectException)
            {
                Log.e("Error", "Connect Error - refused")
            }
           return ""
        }

        public suspend fun info(address: InetAddress?, port : Int , timeOut : Int) : String{
            return command(address, port, timeOut, "info")
        }

        public suspend fun isPowerOn(address: InetAddress?, port : Int , timeOut : Int) : Boolean {
            val resultCommand = command(address, port, timeOut, "info")

            val jsonString = "{$resultCommand"
            val parsedObj = JSONObject(jsonString)
            val objSysInfo = parsedObj.getJSONObject("system").getJSONObject("get_sysinfo")
            //val hw = objSysInfo.getString("hw_ver")
            //val name = objSysInfo.getString("alias")
            val powerStatus = objSysInfo.getInt("relay_state")

            return when(powerStatus){
                1 -> true
                else -> false
            }
        }

        public suspend fun powerOn(address: InetAddress?, port : Int , timeOut : Int) : String{
            return command(address, port, timeOut, "on")
        }

        public suspend fun powerOff(address: InetAddress?, port : Int , timeOut : Int) : String{
            return command(address, port, timeOut, "off")
        }
    }
}