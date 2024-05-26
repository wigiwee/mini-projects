import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Main {


    public static void main(String[] args){
        System.out.println("Logs from your program will appear here!");
        try(DatagramSocket serverSocket = new DatagramSocket(2053)) {
            while(true) {
                final byte[] requestBuffer = new byte[512];
                final DatagramPacket requestPacket = new DatagramPacket(requestBuffer, requestBuffer.length);
                serverSocket.receive(requestPacket);
                System.out.println("Received data");

                DNSMessage dnsMessage = new DNSMessage(requestPacket, requestBuffer);
                final byte[] responseBuffer = dnsMessage.getResponse(requestBuffer);
                final DatagramPacket packetResponse =
                        new DatagramPacket(responseBuffer, responseBuffer.length, requestPacket.getSocketAddress());
                serverSocket.send(packetResponse);
            }
        } catch (IOException e) {
         System.out.println("IOException: " + e.getMessage());
        }
    }
}
