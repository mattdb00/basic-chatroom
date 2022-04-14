package GroupChatApplication;

import java.io.*;
import java.net.Socket;

import javafx.scene.image.Image;

// Each client sends messages to the server & the server spawns a thread to communicate with the client.
// Each communication with a client is added to an array list so any message sent gets sent to every other client
// by looping through it.

public class Client {

    private Socket socket; // Socket for the client to connect to
    private ObjectOutputStream dOut; // Output stream
    private ObjectInputStream dIn; // Input stream
    private String username; // Username
    private ChatBoxController controller; // Applicable controller.

    /**
     * Creates a client with a specified controller, socket, and username.
     * 
     * @param controller JavaFX controller to send and display messages.
     * @param socket     A socket
     * @param username   A username for the client.
     */
    public Client(ChatBoxController controller, Socket socket, String username) {
        try {
            this.controller = controller;
            this.socket = socket;
            this.username = username;
            this.dOut = new ObjectOutputStream(socket.getOutputStream());
            this.dIn = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            closeEverything(socket, dIn, dOut);
        }
    }

    /**
     * Send a message with optional image over the ObjectOutputStream.
     * 
     * @param msg A message to be sent.
     */
    public void sendMessage(String msg, Image image) {
        try {
            Message messageToSend = new Message(username, msg, image);
            dOut.writeObject(messageToSend);
            dOut.flush();

        } catch (IOException e) {
            closeEverything(socket, dIn, dOut);
        }
    }

    /**
     * Listen for a message over the socket to display within the JavaFX
     * application.
     */
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                // While there is still a connection with the server, continue to listen for
                // messages on a separate thread.
                while (socket.isConnected()) {
                    try {
                        Message receivedMsg = (Message) dIn.readObject();

                        if (receivedMsg instanceof Message) {
                            controller.displayMessage(receivedMsg);
                        }

                    } catch (Exception e) {
                        //closeEverything(socket, dIn, dOut);
                    }
                }
            }
        }).start();
    }

    /**
     * Helper method to close everything.
     * 
     * @param socket
     * @param dIn
     * @param dOut
     */
    public void closeEverything(Socket socket, ObjectInputStream dIn, ObjectOutputStream dOut) {
        try {
            if (dIn != null) {
                dIn.close();
            }
            if (dOut != null) {
                dOut.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the username associated with the client.
     * 
     * @return String
     */
    public String getUsername() {
        return username;
    }
}
