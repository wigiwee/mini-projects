package com.redis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class RdbFile {

    // file details
    static String dir = Main.dir;
    static String dbfilename = Main.dbfilename;

    // file parameters
    static String magicString;
    static String version;

    // file database data
    static ConcurrentHashMap<String, String> RDBkeyValueHashMap = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, Long> RDBkeyExpiryHashMap = new ConcurrentHashMap<>();

    static void refreshRDBFile() throws IOException {
        try (InputStream fis = new FileInputStream(new File(dir, dbfilename))) {

            byte[] redis = new byte[5];
            byte[] ver = new byte[4];

            fis.read(redis);
            fis.read(ver);

            magicString = new String(redis, StandardCharsets.UTF_8);
            version = new String(ver, StandardCharsets.UTF_8);
            int bytee;
            while ((bytee = fis.read()) != -1) {
                if (bytee == 0xFB) {
                    int rdbKeyValueHashTableSize = fis.read();
                    int rdbKeyExpiryHashTableSize = fis.read();
                    int b;
                    long expireTimeStampInMs = -1;
                    int valuetype;
                    for (int i = 0; i < rdbKeyValueHashTableSize; i++) {
                        b = fis.read();
                        if (b == 0xFC) {
                            byte[] unixTimeStamp = new byte[8];
                            fis.read(unixTimeStamp);
                            ByteBuffer byteBuffer = ByteBuffer.wrap(unixTimeStamp);
                            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                            expireTimeStampInMs = byteBuffer.getLong();
                            valuetype = fis.read();
                        } else if (b == 0xFD) {
                            byte[] timeStamp = new byte[4];
                            fis.read(timeStamp);
                            ByteBuffer byteBuffer = ByteBuffer.wrap(timeStamp);
                            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                            expireTimeStampInMs = (byteBuffer.getInt() & 0xFFFFFFFFL) * 1000; 
                            valuetype = fis.read();
                        } else {
                            valuetype = b;
                        }

                        int keyLength = fis.read();
                        byte[] keyBytes = new byte[keyLength];
                        fis.read(keyBytes);

                        int valueLength = fis.read();
                        byte[] valueByte = new byte[valueLength];
                        fis.read(valueByte);

                        RDBkeyValueHashMap.put(new String(keyBytes, StandardCharsets.UTF_8),
                                new String(valueByte, StandardCharsets.UTF_8));
                        if(expireTimeStampInMs != -1){
                            RDBkeyExpiryHashMap.put(new String(keyBytes, StandardCharsets.UTF_8), expireTimeStampInMs);
                        }
                    }

                }
            }

        } catch (FileNotFoundException e) {

        }

    }

    static String[] getKeys() throws IOException {
        refreshRDBFile();
        return RDBkeyValueHashMap.keySet().toArray(new String[RDBkeyValueHashMap.keySet().size()]);
    }

    static void syncHashMaps() {

    }

}
