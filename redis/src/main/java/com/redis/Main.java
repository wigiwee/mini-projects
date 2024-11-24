package com.redis;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    
    static String dir = "";
    static String dbfilename = "";
    public static void main(String[] args) {

        if(args.length >=4 ){
            if(args[0].equals("--dir")){
                dir = args[1];
            }
            if (args[2].equals("--dbfilename")){
                dbfilename = args[3];
            }
        }
        System.out.println(dir);
        System.out.println(dbfilename);

        System.out.println("Logs from your program will appear here!");
        
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {

            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            while (true) {
    
                clientSocket = serverSocket.accept();
    
                RequestHandler requestHandler = new RequestHandler(clientSocket);
                Thread.startVirtualThread(requestHandler::run);
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
