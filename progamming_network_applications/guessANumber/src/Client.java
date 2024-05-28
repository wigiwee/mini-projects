
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public Client() throws Exception {

        Socket socket = new Socket("localhost",8080);
        System.out.println("Successful connection to the server.");

        // I/O streams
        BufferedReader in_socket = new BufferedReader (new InputStreamReader (socket.getInputStream()));
        PrintWriter out_socket = new PrintWriter (new OutputStreamWriter (socket.getOutputStream()), true);
        Scanner keyboard = new Scanner(System.in);

        String userNumber;
        while(in_socket.readLine().startsWith("Guess")) {
            System.out.println("Server says guess a number 1-10:");
            userNumber = keyboard.nextLine();
            out_socket.println(userNumber);


        }
        System.out.println("you got it");

        keyboard.close();
        socket.close();
        System.out.println("Socket closed.");

    }

    public static void main(String[] args) {
        try {
            new Client();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
