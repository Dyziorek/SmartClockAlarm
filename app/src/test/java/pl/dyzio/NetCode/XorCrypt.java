package pl.dyzio.NetCode;

import android.util.Log;

import java.nio.ByteBuffer;


public class XorCrypt {

    public static byte[] encryptStr(String input)
    {
        return encrypt(input.getBytes());
    }

    public static String decryptStr(byte[] input)
    {
        byte[] decrypted = decrypt(input);
        try
        {
            byte[] decryptedPad = new byte[decrypted.length-5];
            System.arraycopy(decrypted, 5, decryptedPad, 0, decrypted.length-6);
            return new String(decryptedPad, "UTF-8");
        }
        catch (Exception anyErr)
        {
            Log.e("error in decrypt", anyErr.getMessage());
        }
        return "";
    }

    public static byte[] encrypt(byte[] input)
    {
        ByteBuffer outputData = ByteBuffer.allocate(4 + input.length).putInt(input.length);
        if (input != null && input.length > 0)
        {
            int seed = -85;
            for (int idx = 0; idx < input.length; idx++)
            {
                byte xor = (byte) (seed ^ input[idx]);
                seed = xor;
                outputData.put(xor);
            }
        }

        return outputData.array();
    }

    public static byte[] decrypt(byte[] input)
    {
        if (input != null && input.length > 0)
        {
            int seed = -85;
            for (int idx = 0; idx < input.length; idx++)
            {
                byte xor = (byte) (seed ^ input[idx]);
                seed = input[idx];
                input[idx] = xor;
            }
        }

        return input;
    }
}
