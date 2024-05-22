import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {


        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket;
        Socket clientSocket;
        String directory = null;
        if(args.length == 2){
            if(args[0].equals("--directory")){
                directory = args[1];
            }
        }

        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            while(true){
                clientSocket = serverSocket.accept(); // Wait for connection from client.
                HttpRequestHandler requestHandler = new HttpRequestHandler(clientSocket, directory );
                Thread.startVirtualThread(requestHandler::run);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
