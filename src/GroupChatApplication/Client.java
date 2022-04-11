package GroupChatApplication;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Each client sends messages to the server & the server spawns a thread to communicate with the client.
// Each communication with a client is added to an array list so any message sent gets sent to every other client
// by looping through it.

public class Client {

    // A client has a socket to connect to the server and a reader and writer to receive and send messages
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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

    // Sending a message isn't blocking and can be done without spawning a thread, unlike waiting for a message.
    public void sendMessageToServer(String messageToServer) {
        try {
            // Initially send the username of the client.
            bufferedWriter.write(messageToServer);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            // Create a scanner for user input.
            Scanner scanner = new Scanner(System.in);
            // While there is still a connection with the server, continue to scan the terminal and then send the message.
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // receiveMessageFromClient
    //**************************************
    public void receiveMessageFromServer(VBox vBox) {
        new Thread(new Runnable() {
            @Override
            public void run() { // listen to messages while client is connected
                while (socket.isConnected()){
                    try{
                        String messageFromServer = bufferedReader.readLine();
                        ServerController.addLabel(messageFromServer, vBox);
                    } catch(IOException e){
                        e.printStackTrace();
                        System.out.println("Error receiving mesasge from client");
                        break;
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

    // Run the client.
    public static void main(String[] args) throws IOException {

        // Get a username for the user and a socket connection.
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        // Create a socket to connect to the server.

        // Keep prompting for a valid IP address, continue when successfully connected.
        boolean attempt = true;
        Socket socket = null;
        while(attempt) {
            try {
                System.out.print("Enter the IP address of the server: ");
            String ipAddress = scanner.nextLine();

            System.out.println("Connecting to " + ipAddress + "...");
            socket = new Socket(ipAddress, 1234);

            // If the connection is successful, break out of the loop.
            attempt = false;

            } catch (IOException e) {
                System.out.println("Invalid IP address. Please try again.");
            }
        }

        // Pass the socket and give the client a username.
        Client client = new Client(socket, username);
        // Infinite loop to read and send messages.
        System.out.println("Connection successful!");

    }
}
