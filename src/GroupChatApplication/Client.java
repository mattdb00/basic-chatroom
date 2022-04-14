package GroupChatApplication;

import javafx.fxml.Initializable;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Scanner;

// Each client sends messages to the server & the server spawns a thread to communicate with the client.
// Each communication with a client is added to an array list so any message sent gets sent to every other client
// by looping through it.

public class Client extends Observable implements Observer {

    // A client has a socket to connect to the server and a reader and writer to receive and send messages
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private final static int port = 6000;
    public static int userCount = 0;

    public Client(String username) {

        // Keep prompting for a valid IP address, continue when successfully connected.
        boolean attempt = true;
        Socket socket = null;
        int port = 6000;
        while (attempt) {
            try {
    //              System.out.print("Enter the IP address of the server: ");
    //              String ipAddress = scanner.nextLine();
                String ipAddress = "192.168.86.47";
                System.out.println("Connecting to " + ipAddress + "...");
                socket = new Socket(ipAddress, port);

                // If the connection is successful, break out of the loop.
                attempt = false;
            } catch (IOException e) {
                System.out.println("Invalid IP address. Please try again.");
            }
        }

        try {
            this.socket = socket;
            this.username = username;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Infinite loop to read messages.
            System.out.println("Connection successful!");
            listenForMessage();
            //client.sendMessage();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Listening for a message is blocking so need a separate thread for that.
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                // While there is still a connection with the server, continue to listen for messages on a separate thread.
                while (socket.isConnected()) {
                    try {
                        // Get the messages sent from other users and print it to the console.
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    // Helper method to close everything so you don't have to repeat yourself.
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object message) {
        //send the message to the server
        try {
            // Initially send the username of the client.
//            bufferedWriter.write(username);
//            bufferedWriter.newLine();
//            bufferedWriter.flush();

            System.out.println((String) message + " **** is a message being broadcast to observers ****");

            // While there is still a connection with the server, continue to scan the terminal and then send the message.
            if (socket.isConnected()) {
                String messageToSend = (String) message;
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
}

