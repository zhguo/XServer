package monkeylord.XServer.api;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import monkeylord.XServer.XServer;
import monkeylord.XServer.handler.MemoryHandler;
import monkeylord.XServer.handler.MethodHandler;

public class MemoryView implements XServer.Operation {
    @Override
    public String handle(String url, Map<String, String> parms, Map<String, String> headers, Map<String, String> files) {
        try {
            Method method=null;
            if(parms.get("op")!=null) {
                switch (parms.get("op")){
                    case "dump":
                        byte[] binary=MemoryHandler.readMemory(Long.parseLong(parms.get("addr")),Integer.parseInt(parms.get("count")));
                        FileOutputStream fo = new FileOutputStream(new File("/sdcard/XServer.dump"));
                        fo.write(binary);
                        return "Dumped at /sdcard/XServer.dump";
                    case "read":
                        byte[] bytes=MemoryHandler.readMemory(Long.parseLong(parms.get("addr")),Integer.parseInt(parms.get("count")));
                        return bytesToHexString(bytes);
                    case "write":
                        MemoryHandler.writeMemory(Long.parseLong(parms.get("addr")),hexStringToBytes(files.get("postData")));
                        byte[] bytes_res=MemoryHandler.readMemory(Long.parseLong(parms.get("addr")),hexStringToBytes(files.get("postData")).length);
                        return bytesToHexString(bytes_res);
                    default:
                        return "";
                }
            }else{
                HashMap<String, Object> map = new HashMap<>();
                map.put("maps", MemoryHandler.getMaps());
                return XServer.render(map, "pages/memory.html");
            }
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public static String bytesToHexString(byte[] src){
        StringBuffer stringBuffer = new StringBuffer(src.length*2+2);
        byte[] hexs = "0123456789ABCDEF".getBytes();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            //Log.e("XServer", "bytesToHexString: "+((src[i]&0xFF)>>4));
            //Log.e("XServer", "bytesToHexString: "+((src[i]&0x0F)));
            stringBuffer.append((char) (hexs[(src[i]&0xFF)>>4]));
            stringBuffer.append((char) (hexs[src[i]&0x0F]));
            /*
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuffer.append(0);
            }
            stringBuffer.append(hv);
            */
        }
        return stringBuffer.toString();
    }
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
