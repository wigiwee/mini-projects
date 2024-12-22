package com.redis.serverProfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import com.redis.configAndUtils.Config;
import com.redis.configAndUtils.RdbUtils;
import com.redis.configAndUtils.Utils;

public class SlaveProfile implements Runnable {

    public Socket clientSocket;

    public static ConcurrentHashMap<String, String> keyValueHashMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Long> keyExpiryHashMap = new ConcurrentHashMap<>();

    public SlaveProfile(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));) {

            String content;

            while ((content = reader.readLine()) != null) {

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
                    processCommand(writer, args);

                } else {
                    writer.write("-ERROR: Unknown command or incorrect arguments\r\n");
                    writer.flush();
                }
            }

        } catch (IOException e) {
            System.out.println("Something went wrong while processing the command");
            System.out.println("Cannot read/write from the socket");
            e.printStackTrace();
        }
    }

    public static void processCommand(BufferedWriter writer, String[] args) throws IOException {

        int numArgs = args.length;

        if (args[0].equalsIgnoreCase("ping")) {

            System.out.println("Sending response pong");
            writer.write("+PONG\r\n");
            writer.flush();
            System.out.println("done sending response returning");

        } else if (args[0].equalsIgnoreCase("echo") && numArgs == 2) {

            String message = args[1];
            writer.write(Utils.bulkString(message));
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
            Utils.sendReplicaionCommands(args);

        } else if (args[0].equalsIgnoreCase("get") && numArgs == 2) {
            if (keyValueHashMap.containsKey(args[1])) {
                if (keyExpiryHashMap.containsKey(args[1])) {
                    if (System.currentTimeMillis() < keyExpiryHashMap.get(args[1])) {
                        writer.write(Utils.bulkString(keyValueHashMap.get(args[1])));
                        writer.flush();
                    } else {
                        keyExpiryHashMap.remove(args[1]);
                        keyValueHashMap.remove(args[1]);
                        writer.write(Config.NIL);
                        writer.flush();
                    }
                } else {
                    writer.write(Utils.bulkString(keyValueHashMap.get(args[1])));
                    writer.flush();
                }
            } else if (RdbUtils.RDBkeyValueHashMap.containsKey(args[1])) {
                if (RdbUtils.RDBkeyExpiryHashMap.containsKey(args[1])) {
                    if (System.currentTimeMillis() < RdbUtils.RDBkeyExpiryHashMap.get(args[1])) {
                        writer.write(Utils.bulkString(RdbUtils.RDBkeyValueHashMap.get(args[1])));
                        writer.flush();
                    } else {
                        writer.write(Config.NIL);
                        writer.flush();
                    }
                } else {
                    writer.write(Utils.bulkString(RdbUtils.RDBkeyValueHashMap.get(args[1])));
                    writer.flush();
                }

            } else {
                writer.write(Config.NIL);
                writer.flush();
            }

        } else if (args[0].equalsIgnoreCase("config")) {

            if (args[1].equalsIgnoreCase("get")) {
                if (args[2].equalsIgnoreCase("dir")) {
                    writer.write(Utils.encodeArray(new String[] { "dir", Config.dir }));
                    writer.flush();
                } else if (args[2].equalsIgnoreCase("dbfilename")) {
                    writer.write(Utils.encodeArray(new String[] { "dbfilename", Config.dbfilename }));
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

            if (Config.dbfilename.isEmpty() && Config.dir.isEmpty()) {
                writer.write("-ERROR: RDB File not found\r\n");
                writer.flush();
            } else {

                if (args[1].equals("*")) {
                    System.out.println("keys: " + Arrays.toString(RdbUtils.getKeys()));
                    writer.write(Utils.encodeArray(RdbUtils.getKeys()));
                    writer.flush();
                }
            }

        } else if (args[0].equalsIgnoreCase("info")) {

            if (Config.hostPort == -1 && Config.hostName.isBlank()) {
                StringBuilder output = new StringBuilder();
                output.append("role:master");
                output.append("\n");
                output.append("master_replid:").append("8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb");
                output.append("\n");
                output.append("master_repl_offset:").append("0");
                writer.write(Utils.bulkString(output.toString()));
                writer.flush();
            } else {
                writer.write(Utils.bulkString("role:slave"));
                writer.flush();
            }

        } else if (args[0].equalsIgnoreCase("replconf")) {
            if (args[1].equalsIgnoreCase("listening-port")) {
                System.out.println("Repl listening port: " + Integer.parseInt(args[2]));
                writer.write("+OK" + Config.CRLF);
                writer.flush();
            } else if (args[1].equalsIgnoreCase("capa")) {
                System.out.println("capabilitles: " + args[2]);
                writer.write("+OK" + Config.CRLF);
                writer.flush();
            } else if (args[1].equalsIgnoreCase("getack")) {
                String command = Utils.RESP2format("REPLCONF ACK " + Config.bytesProcessedBySlave);
                writer.write(command);
                Config.bytesProcessedBySlave = command.length() / 2;
                writer.flush();
            }
        } else {
            writer.write("-ERROR: Unknown command or incorrect arguments\r\n");
            writer.flush();
        }

    }

    public static void handshake() throws Exception {

        try (Socket socket = new Socket(Config.hostName, Config.hostPort);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        ) {

            // stage 1 sending ping
            // writer.write(Utils.RESP2format("PING"));
            writer.write("*1\r\n$4\r\nPING\r\n");
            writer.flush();
            System.out.println(reader.readLine());

            // stage 2 sending replconf I
            writer.write(Utils.RESP2format("REPLCONF listening-port " + Config.port));
            writer.flush();
            System.out.println(reader.readLine());

            // stage 3 sending replconf II
            writer.write(Utils.RESP2format("REPLCONF capa psync2"));
            writer.flush();
            System.out.println(reader.readLine());

            // configuring replica to send ACK
            // writer.write(RESP2format("REPLCONF GETACK *"));
            // writer.flush();

            writer.write(Utils.RESP2format("PSYNC ? -1"));
            writer.flush();
            System.out.println("fullresync: " + reader.readLine());

            String str = reader.readLine();
            int rdbSize = 0;
            if (str.startsWith("$")) {
                rdbSize = Integer.parseInt(str.substring(1));
            }
            System.out.println("rdb file size: " + rdbSize);
            StringBuilder string = new StringBuilder();
            while ((char) reader.read() != '*') {
                string.append((char) reader.read());
            }
            System.out.println(string);

            Config.isHandshakeComplete = true;

            int commandArrayLength = Integer.parseInt(reader.readLine().trim());
            String[] commandArray = new String[commandArrayLength];
            for (int i = 0; i < commandArrayLength; i++) {
                String lengthString = reader.readLine();
                int commandLength = 0;
                if (lengthString.startsWith("$")) {
                    commandLength = Integer.parseInt(lengthString.substring(1));
                    commandArray[i] = reader.readLine();
                    if (commandArray[i].length() != commandLength) {
                        throw new Exception("invalid RESP string, length dosne't match");
                    }
                }
            }
            System.out.println("first command array : " + Arrays.toString(commandArray));
            SlaveProfile.processCommand(writer, commandArray);
            System.out.println("returned");
            String content = "";
            while (true) {
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
                        System.out.println("Command received :" + Arrays.toString(args));
                        SlaveProfile.processCommand(writer, args);
                    }
                    System.out.println(content);
                }
            }

        } catch (IOException e) {
            System.out.println("Something went wrong while establishing handshake");
        }

    }

}