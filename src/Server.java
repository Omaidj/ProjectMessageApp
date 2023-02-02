import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private static final int PORT = 1234;

    //this allows the server to easily keep track of which clients are in which channel
    //and to broadcast message to the correct client
    private static final Map<String, List<Socket>> channelClients = new HashMap<>();
    private static final Map<String, List<String>> channelNames = new HashMap<>();

    //this is where a new client is connected, and a new thread is created for the
    //communication with the server
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Chat server started on port " + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client connected: " + socket);

            new Thread(new ClientHandler(socket)).start();
        }
    }


    private static class ClientHandler implements Runnable {
        //this code defines a private inner class which would implement the runnable interface.
        //this will be used to handle communication between the server and the cliant, each socket represent the connection
        private Socket socket;
        private DataInputStream input;
        private DataOutputStream output;
        private String name;
        private String channel;

        //client handler
        public ClientHandler(Socket socket) throws IOException {
            //this code is using the socket to read and write data between the client and server
            this.socket = socket;
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            // Parse the JSON object sent by the client
            String jsonString = input.readUTF();
            JSONObject json = new JSONObject(jsonString);
            name = json.getString("username");
            channel = json.getString("channel");

            //this will add the socket and username to the appropriate lists for the channel
            //If the channel doesn't exist, create the channel
            List<Socket> clients = channelClients.get(channel);
            List<String> names = channelNames.get(channel);
            if (clients == null) {
                clients = new ArrayList<>();
                channelClients.put(channel, clients);
            }
            if (names == null) {
                names = new ArrayList<>();
                channelNames.put(channel, names);
            }
            clients.add(socket);
            names.add(name);

            //Read the messages from the file and send them to the client
            //If the file for the channel doesn't exist, send a message to the client that they have created a channel
            File file = new File(channel + ".txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(channel + ".txt"));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.writeUTF(line);
                }
                reader.close();
            } else {
                output.writeUTF("New channel: " + "'" + channel + "'" + " has been created..");
            }

            //This will broadcast a message to all clients in the channel that the user has connected
            for (int i = 0; i < clients.size(); i++) {
                Socket clientSocket = clients.get(i);
                DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
                clientOutput.writeUTF(name + " has connected to channel " + channel);
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String message = input.readUTF();
                        //user writes a message to the file
                        BufferedWriter writer = new BufferedWriter(new FileWriter(channel + ".txt", true));
                        writer.write(name + ": " + message);
                        writer.newLine();
                        writer.close();

                        //this will broadcast the message to all clients in the channel
                        List<Socket> clients = channelClients.get(channel);
                        for (int i = 0; i < clients.size(); i++) {
                            Socket clientSocket = clients.get(i);
                            DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
                            clientOutput.writeUTF(name + ": " + message);
                        }

                } catch (IOException e) {
                    //Remove the socket and username from the appropriate lists for the channel
                    List<Socket> clients = channelClients.get(channel);
                    List<String> names = channelNames.get(channel);
                    clients.remove(socket);
                    names.remove(name);

                    //Broadcast a message to all clients in the channel that the user has disconnected
                    for (int i = 0; i < clients.size(); i++) {
                        Socket clientSocket = clients.get(i);
                        DataOutputStream clientOutput = null;
                        try {
                            clientOutput = new DataOutputStream(clientSocket.getOutputStream());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        try {
                            clientOutput.writeUTF(name + " has disconnected from channel " + channel);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                    //this will close the socket
                    try {
                        socket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}

