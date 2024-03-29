package pl.dyzio.smartclockalarm

import android.util.Log
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import pl.dyzio.NetCode.NetComms
import pl.dyzio.smartclockalarm.net.NetCommand
import pl.dyzio.smartclockalarm.net.NetSystem
import java.net.InetAddress

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun checkNetworkCall() {
        Log.e("INFO", "checkNetworkCall: begin")
        val smartHosts = NetSystem.instanceLookup(9999).smartPlugSockets
        assertNotNull(smartHosts)
        assertTrue("Could not find hosts", smartHosts.isNotEmpty())
        Log.e("INFO", "checkNetworkCall: ${smartHosts[0]}")
    }

    @Ignore
    @Test
    fun checkNetworkCommand() {
        var result = NetComms.info(InetAddress.getByAddress(byteArrayOf(-64, -88, 2, 56)), 9999, 1000)
        val smartHosts = result.get()
        assertNotNull(smartHosts)
        assertTrue("Could not find hosts", smartHosts.isNotEmpty())
    }

    @Test
    fun checkNetKotlinCommand()
    {
        runBlocking {
            NetCommand.powerOn(InetAddress.getByName("192.168.0.201"), 9999, 5000)
        }

        val result = runBlocking {
            return@runBlocking NetCommand.isPowerOn(InetAddress.getByName("192.168.0.201"), 9999, 5000)
        }

        assertTrue("PowerOn is not working" , result)

    }

    @Test
    fun checkNetOffCommand()
    {
        runBlocking {
            NetCommand.powerOff(InetAddress.getByName("192.168.0.201"), 9999, 5000)
        }

        val result = runBlocking {
            return@runBlocking NetCommand.isPowerOn(InetAddress.getByName("192.168.0.201"), 9999, 5000)
        }

        assertFalse("PowerOn is not working" , result)

    }

    private fun String.toSByte() : Byte
    {
        val check = this.toInt()
        if (check > 127)
        {
            return (check - 256).toByte()
        }
        return  check.toByte()
    }

    @Test
    fun transform()
    {
        val ipv4 = "192.168.2.56"

        val byteArray  = ipv4.split(".").map {  it.toSByte() }.toByteArray()


        val second = byteArrayOf(-64, -88, 2, 56)

        assertArrayEquals("Array is not equal", byteArray,second)


        val testIN = "\\/192.168.2.56".filter {
                charTest -> when (charTest) {
            in '0'..'9' -> true
            '.' -> true
            else -> false
        }}
        assertNotNull(testIN)

        assertArrayEquals("Array is not equal", byteArray,second)
    }

    @After
    fun powerOff()
    {


        runBlocking {
            NetCommand.powerOff(InetAddress.getByAddress(byteArrayOf(-64, -88, 2, 56)), 9999, 5000)
        }
    }
}
