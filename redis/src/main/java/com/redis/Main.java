package com.redis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    
    static String dir = "";
    static String dbfilename = "";
    static int port = 6379;
    static String hostname = "";
    static int hostPort = -1;


    static void handshake() throws UnknownHostException, IOException{
        Socket socket = new Socket(hostname, hostPort);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer.write(("*1"+ RequestHandler.CRLF+RequestHandler.bulkString("PING")));
        writer.flush();
        System.out.println(reader.readLine());
    }

    public static void main(String[] args) throws UnknownHostException, IOException {

        for(int i = 0 ; i < args.length; i+=2){
            if(args[i].equals("--dir")){
                dir= args[i+1];
            }else if( args[i].equals("--dbfilename")){
                dbfilename = args[i+1];
            }else if(args[i].equals("--port")){
                port = Integer.parseInt(args[i+1]);
            }else if( args[i].equals("--replicaof")){
                String value = args[i+1];
                String[] strArray = value.split(" ");
                hostname =  strArray[0];
                hostPort = Integer.parseInt(strArray[1]);
            }
        }
        if(hostPort !=-1 && !hostname.isEmpty()){
            handshake();
        }
        System.out.println("Logs from your program will appear here!");
        
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {

            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            while (true) {
    
                clientSocket = serverSocket.accept();
    
                RequestHandler requestHandler = new RequestHandler(clientSocket);
                Thread.startVirtualThread(() -> {
                    try {
                        requestHandler.run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
