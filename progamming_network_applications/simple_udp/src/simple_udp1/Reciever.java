package simple_udp1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Reciever  {
	public Reciever() throws Exception {
		
		DatagramSocket socket =new DatagramSocket(8080);
		System.out.println("Reciever is running.");
		Scanner keyboard = new Scanner(System.in);
		
		byte[] buffer = new byte[1500]; // MTU = 1500
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		socket.receive(packet);//retrieving sender's message
		
		String message = new String(buffer).trim();
		System.out.println("Recieved:" + message);
		
		InetAddress senders_address = packet.getAddress();
		int senders_port = packet.getPort();
		
		System.out.println("Enter your message: ");
		message = keyboard.nextLine();
		buffer = message.getBytes();
		packet = new DatagramPacket(buffer,buffer.length , senders_address, senders_port);
		socket.send(packet);
		
		System.out.println("send: " + message);
		
		
		
		
	}
	
	public static void main(String[] args) {
		try {
			new Reciever();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

}
