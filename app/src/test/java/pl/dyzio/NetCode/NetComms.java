package pl.dyzio.NetCode;

import static android.content.ContentValues.TAG;

import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class NetComms {

    private static Map<String, String> commands = initCommands();

    private static Map<String, String> initCommands() {
        Map<String, String> commandsInit = new HashMap<>();
        commandsInit.put("info", "{\"system\":{\"get_sysinfo\":{}}}");
        commandsInit.put("on", "{\"system\":{\"set_relay_state\":{\"state\":1}}}");
        commandsInit.put("off", "{\"system\":{\"set_relay_state\":{\"state\":0}}}");
        commandsInit.put("ledoff", "{\"system\":{\"set_led_off\":{\"off\":1}}}");
        commandsInit.put("ledon", "{\"system\":{\"set_led_off\":{\"off\":0}}}");
        commandsInit.put("cloudinfo", "{\"cnCloud\":{\"get_info\":{}}}");
        commandsInit.put("wlanscan", "{\"netif\":{\"get_scaninfo\":{\"refresh\":0}}}");
        commandsInit.put("time", "{\"time\":{\"get_time\":{}}}");
        commandsInit.put("schedule", "{\"schedule\":{\"get_rules\":{}}}");
        commandsInit.put("countdown", "{\"count_down\":{\"get_rules\":{}}}");
        commandsInit.put("antitheft", "{\"anti_theft\":{\"get_rules\":{}}}");
        commandsInit.put("reboot", "{\"system\":{\"reboot\":{\"delay\":1}}}");
        commandsInit.put("reset", "{\"system\":{\"reset\":{\"delay\":1}}}");
        commandsInit.put("energy", "{\"emeter\":{\"get_realtime\":{}}}");

        return commandsInit;
    }

    protected static String command(InetAddress inetAddress, int port, int timeOut, String commandText)
    {
        try {
            Socket sockConn = new Socket();
            if (timeOut != 0)
            {
                sockConn.connect(new InetSocketAddress(inetAddress, port), timeOut);
            }
            else {
                sockConn.connect(new InetSocketAddress(inetAddress, port), 100);
            }

            InputStream iStream = sockConn.getInputStream();
            OutputStream oStream = sockConn.getOutputStream();
            oStream.write(XorCrypt.encryptStr(commandText));
            byte[] buffer = new byte[2048];
            iStream.read(buffer);
            oStream.close();
            iStream.close();
            sockConn.close();
            String decrypted = XorCrypt.decryptStr(buffer);
            return decrypted;
        }
        catch (Exception err)
        {
            Log.e("Error with net sockets", err.getMessage());
        }
        return null;
    }

    public static void powerOn(InetAddress inetAddress, int port, int timeOut)
    {
        Runnable commandRun = () -> command(inetAddress, port, timeOut, commands.get("on"));
        Executors.newSingleThreadExecutor().submit(commandRun);
    }

    public static void powerOnSync(InetAddress inetAddress, int port, int timeOut)
    {
        try {
            Runnable commandRun = () -> command(inetAddress, port, timeOut, commands.get("on"));
            Future<?> notReturn = Executors.newSingleThreadExecutor().submit(commandRun);
            notReturn.get(timeOut, TimeUnit.MILLISECONDS);
        }
        catch (Exception runtimeExp)
        {
            Log.e(TAG, "powerOnSync: error");
        }
    }

    public static void powerOff(InetAddress inetAddress, int port, int timeOut)
    {
        Runnable commandRun = () -> command(inetAddress, port, timeOut, commands.get("off"));
        Executors.newSingleThreadExecutor().submit(commandRun);
    }

    public static Future<String> info(InetAddress inetAddress, int port, int timeOut)
    {
        Log.d("NET", "info() called with: inetAddress = [" + inetAddress + "], port = [" + port + "]");
        Callable<String> commandRun = () -> command(inetAddress, port, timeOut, commands.get("info"));
        Future<String> returnData =  Executors.newSingleThreadExecutor().submit(commandRun);

        return returnData;
    }

    public static boolean isPowerOn(InetAddress inetAddress, int port, int timeOut)
    {
        try {
            Log.d("NET", "info() called with: inetAddress = [" + inetAddress + "], port = [" + port + "]");
            Callable<String> commandRun = () -> command(inetAddress, port, timeOut, commands.get("info"));
            Future<String> returnData = Executors.newSingleThreadExecutor().submit(commandRun);

            String jsonString = "{" + returnData.get(timeOut, TimeUnit.MILLISECONDS);
            JSONObject parsedObj = new JSONObject(jsonString);
            JSONObject objSysInfo = parsedObj.getJSONObject("system").getJSONObject("get_sysinfo");
            String hw = objSysInfo.getString("hw_ver");
            String name = objSysInfo.getString("alias");
            return objSysInfo.getInt("relay_state") == 0 ? false : true;
        }
        catch (Exception runtimeExp)
        {
            Log.e(TAG, "powerOnSync: error");
        }
        return  false;
    }
}