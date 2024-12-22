package com.redis.configAndUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    public static int stringEncoding(InputStream fis) throws IOException {
        int length = 0;
        int b = fis.read();
        int first2Byte = b & 11000000;
        if (first2Byte == 0x00) {
            length = b & 00111111;
        } else if (first2Byte == 0xC0) {
            length = 8;
        } else if (first2Byte == 0xC1) {

        } else if (first2Byte == 0xC2) {

        } else if (first2Byte == 0xC3) {
            // LZF algorithm

        }

        return length;
    }

    public static int sizeEncoding(InputStream fis) throws IOException {
        int b = fis.read(); // reading first byte
        int length = 00;
        int first2bits = b & 11000000;
        if (first2bits == 00000000) {
            length = b;
        } else if (first2bits == 01000000) {
            int nextByte = fis.read();
            int lsb6 = b & 00111111;
            int shiftby6bits = lsb6 << 8; // shift by 8 bits to make space of next 8 bits of second byte;
            length = shiftby6bits | (nextByte & 0xFF);
        } else if (first2bits == 10000000) { // combining next 4 bytes to form the length
            length = ((fis.read() & 0xFF) << 24) |
                    ((fis.read() & 0xFF) << 16) |
                    ((fis.read() & 0xFF) << 8) |
                    ((fis.read() & 0xFF));
        }
        return length;
    }

    public static String encodeArray(String[] inputArray) {
        StringBuilder output = new StringBuilder("");
        output.append("*").append(inputArray.length).append(com.redis.configAndUtils.Config.CRLF);
        for (int i = 0; i < inputArray.length; i++) {
            output.append("$").append(inputArray[i].length()).append(Config.CRLF).append(inputArray[i])
                    .append(Config.CRLF);
        }
        return output.toString();
    }

    public static String encodeCommandArray(String[] commands) {
        StringBuilder output = new StringBuilder();
        output.append("*").append(commands.length);
        for (String command : commands) {
            output.append(Config.CRLF);
            output.append("$").append(command.length());
            output.append(Config.CRLF);
            output.append(command);
        }
        output.append(Config.CRLF);
        return output.toString();
    }

    public static String bulkString(String str) {
        return "$" + str.length() + Config.CRLF + str + Config.CRLF;
    }

    public static String RESP2format(String str) {
        String[] strArr = str.split(" ");
        StringBuilder output = new StringBuilder();
        output.append("*").append(strArr.length);
        output.append(Config.CRLF);
        for (String string : strArr) {
            output.append("$").append(string.length());
            output.append(Config.CRLF);
            output.append(string);
            output.append(Config.CRLF);
        }
        return output.toString();
    }

    public static void readConfiguration(String[] args) {

        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("--dir")) {
                Config.dir = args[i + 1];
            } else if (args[i].equals("--dbfilename")) {
                Config.dbfilename = args[i + 1];
            } else if (args[i].equals("--port")) {
                Config.port = Integer.parseInt(args[i + 1]);
            } else if (args[i].equals("--replicaof")) {
                Config.role = Roles.SLAVE;
                String value = args[i + 1];
                String[] strArray = value.split(" ");
                Config.hostName = strArray[0];
                Config.hostPort = Integer.parseInt(strArray[1]);
            }
        }
    }

    public static void sendReplicaionCommands(String[] commands) throws IOException {

        for (OutputStream replica : Config.replicas) {
            replica.write(Utils.encodeCommandArray(commands).getBytes());
        }
    }
}
