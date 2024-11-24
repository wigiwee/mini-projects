package com.redis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHandler {

    public Socket clientSocket;
    final static String CRLF = "\r\n";
    static ConcurrentHashMap<String, String> keyValueHashMap = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, Long> keyExpiryHashMap = new ConcurrentHashMap<>();
    static final String NIL = "$-1" + CRLF;
    
    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    static String bulkString(String str){
        return "$" + str.length() + CRLF + str + CRLF;
    }

    static int stringEncoding(InputStream fis) throws IOException {
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

    static int sizeEncoding(InputStream fis) throws IOException {
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

    static String encodeArray(String[] inputArray) {
        StringBuilder output = new StringBuilder("");
        output.append("*").append(inputArray.length).append(CRLF);
        for (int i = 0; i < inputArray.length; i++) {
            output.append("$").append(inputArray[i].length()).append(CRLF).append(inputArray[i]).append(CRLF);
        }
        return output.toString();
    }


    static String RESP2format(String str){
        String[] strArr = str.split(" ");
        StringBuilder output = new StringBuilder();
        output.append("*").append(strArr.length);
        output.append(CRLF).append(bulkString(str));
        output.append(CRLF);
        return output.toString();
    }
    public void run() throws IOException {
        RdbFile.refreshRDBFile();
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));) {
            String content;
            while ((content = reader.readLine()) != null) {
                // Parse the RESP array
                if (content.startsWith("*")) {
                    int numArgs = Integer.parseInt(content.substring(1));
                    String[] args = new String[numArgs];
                    for (int i = 0; i < numArgs; i++) {
                        String lengthLine = reader.readLine();
                        if (!lengthLine.startsWith("$")) {
                            writer.write("-ERROR: Invalid RESP format\r\n");
                            writer.flush();
                            continue;
                        }
                        int length = Integer.parseInt(lengthLine.substring(1));
                        args[i] = reader.readLine();
                        if (args[i].length() != length) {
                            writer.write("-ERROR: Length mismatch\r\n");
                            writer.flush();
                            continue;
                        }
                    }

                    System.out.println(Arrays.toString(args));
                    if (args[0].equalsIgnoreCase("ping")) {
                        writer.write("+PONG\r\n");
                        writer.flush();

                    } else if (args[0].equalsIgnoreCase("echo") && numArgs == 2) {
                        String message = args[1];
                        writer.write(bulkString(message));
                        writer.flush();

                    } else if (args[0].equalsIgnoreCase("set") && numArgs >= 3) {
                        keyValueHashMap.put(args[1], args[2]);
                        if (numArgs > 3) {
                            if (args[3].equalsIgnoreCase("px")) {
                                keyExpiryHashMap.put(args[1], System.currentTimeMillis() + Long.parseLong(args[4]));
                            }
                        }
                        writer.write("+OK\r\n");
                        writer.flush();

                    } else if (args[0].equalsIgnoreCase("get") && numArgs == 2) {
                        if (keyValueHashMap.containsKey(args[1])) {
                            if (keyExpiryHashMap.containsKey(args[1])) {
                                if (System.currentTimeMillis() < keyExpiryHashMap.get(args[1])) {
                                    writer.write(bulkString(keyValueHashMap.get(args[1])));
                                    writer.flush();
                                } else {
                                    keyExpiryHashMap.remove(args[1]);
                                    keyValueHashMap.remove(args[1]);
                                    writer.write(NIL);
                                    writer.flush();
                                }
                            } else {
                                writer.write(bulkString(keyValueHashMap.get(args[1])));
                                writer.flush();
                            }
                        } else if (RdbFile.RDBkeyValueHashMap.containsKey(args[1])) {
                            if (RdbFile.RDBkeyExpiryHashMap.containsKey(args[1])) {
                                if (System.currentTimeMillis() < RdbFile.RDBkeyExpiryHashMap.get(args[1])) {
                                    writer.write(bulkString(RdbFile.RDBkeyValueHashMap.get(args[1])));
                                    writer.flush();
                                } else {
                                    writer.write(NIL);
                                    writer.flush();
                                }
                            } else {
                                writer.write(bulkString(RdbFile.RDBkeyValueHashMap.get(args[1])));
                                writer.flush();
                            }
                            
                        } else {
                            writer.write(NIL);
                            writer.flush();
                        }

                    } else if (args[0].equalsIgnoreCase("config")) {
                        if (args[1].equalsIgnoreCase("get")) {
                            if (args[2].equalsIgnoreCase("dir")) {
                                writer.write(encodeArray(new String[] { "dir", Main.dir }));
                                writer.flush();
                            } else if (args[2].equalsIgnoreCase("dbfilename")) {
                                writer.write(encodeArray(new String[] { "dbfilename", Main.dbfilename }));
                                writer.flush();
                            } else {
                                writer.write("-ERROR: Unknown configuration key arguments\r\n");
                                writer.flush();
                            }
                        } else {
                            writer.write("-ERROR: Unknown command or incorrect arguments\r\n");
                            writer.flush();
                        }
                    } else if (args[0].equalsIgnoreCase("keys")) {
                        if (Main.dbfilename.isEmpty() && Main.dir.isEmpty()) {
                            writer.write("-ERROR: RDB File not found\r\n");
                            writer.flush();
                        } else {

                            if (args[1].equals("*")) {
                                System.out.println("keys: " + Arrays.toString(RdbFile.getKeys()));
                                writer.write(encodeArray(RdbFile.getKeys()));
                                writer.flush();
                            }
                        }

                    } else if (args[0].equalsIgnoreCase("info")){
                        if(Main.hostPort == -1 &&  Main.hostname.isBlank()){
                            StringBuilder output = new StringBuilder();
                            output.append("role:master");
                            output.append("\n");
                            output.append("master_replid:").append("8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb");
                            output.append("\n");
                            output.append("master_repl_offset:").append("0");
                            writer.write(bulkString(output.toString()));
                            writer.flush();
                        }else{
                            writer.write(bulkString("role:slave"));
                            writer.flush();
                        }

                    } else {
                        writer.write("-ERROR: Unknown command or incorrect arguments\r\n");
                        writer.flush();
                    }

                } else {
                    writer.write("-ERROR: Invalid RESP input\r\n");
                    writer.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); // Ensure socket is closed to avoid resource leaks
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}