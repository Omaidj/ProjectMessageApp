import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 1234;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, PORT);
        System.out.println("Connected to the server");

        DataInputStream input = new DataInputStream(socket.getInputStream());
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        Scanner scanner = new Scanner(System.in);

        System.out.print("=========================\n"+"Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("=========================\n"+"Enter the channel name: ");
        String channel = scanner.nextLine();

        // Creating this JSON object to send to the server
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("channel", channel);

        // this will send the JSON object to the server as a string
        output.writeUTF(json.toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = input.readUTF();
                        System.out.println(message);
                    } catch (IOException e) {
                        break;
                    }
                }
            }
        }).start();

        while (true) {
            try {
                String message = scanner.nextLine();
                output.writeUTF(message);
            } catch (IOException e) {
                System.out.println("An error occurred while sending the message: " + e.getMessage());
                break;
            }
        }
    }
}
