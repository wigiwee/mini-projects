package simple_udp1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Sender {
	public Sender() throws Exception{
		DatagramSocket socket = new DatagramSocket();
		Scanner keyboard = new Scanner(System.in);
		
		System.out.println("Enter your message: ");
		String message = keyboard.nextLine();
		byte[] buffer = message.getBytes();
		
		DatagramPacket packet = new DatagramPacket(buffer ,buffer.length , InetAddress.getByName("127.0.0.0"),8080);
		System.out.println("send : "+ message);
		
		buffer = new byte[1500];
		packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		
		message = new String(buffer).trim();
		System.out.println("Recieved: "+ message);
		
	}
	
	public static void main(String[] args) {
		try {
			
			new Sender();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

}
