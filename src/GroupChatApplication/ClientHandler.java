package GroupChatApplication;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ClientHandler implements Runnable {

    // Array list of all the threads handling clients so each message can be sent to the client the thread is handling.
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    // Id that will increment with each new client.

    // Socket for a connection, buffer reader and writer for receiving and sending data respectively.
    private Socket socket;
    private ObjectOutputStream dOut;
    private ObjectInputStream dIn;
    private String clientUsername;

    /**
     * Create the client handler from the socket the server passes.
     * @param socket Socket containing IP address and port number.
     * @throws ClassNotFoundException
     */
    public ClientHandler(Socket socket) throws ClassNotFoundException {
        try {
            this.socket = socket;
            this.dOut = new ObjectOutputStream(socket.getOutputStream());
            this.dIn = new ObjectInputStream(socket.getInputStream());
            this.clientUsername = ((Message) dIn.readObject()).username;

            // Add the new client handler to the array so they can receive messages from others.
            clientHandlers.add(this);
            
            broadcastMessage(new Message("Server", clientUsername + " has entered the chat!", null));
        } catch (IOException e) {
            // Close everything more gracefully.
            closeEverything(socket, dIn, dOut);
        }
    }

    /**
     * Everything in this method is run on a separate thread. We want to listen for messages
     * on a separate thread because listening (bufferedReader.readLine()) is a blocking operation.
     * A blocking operation means the caller waits for the callee to finish its operation.
     */
    @Override
    public void run() {
        // Continue to listen for messages while a connection with the client is still established.
        while (socket.isConnected()) {
            try {
                // Read what the client sent and then send it to every other client.
                Message receivedMsg = (Message) dIn.readObject();

                if(receivedMsg instanceof Message)
                    broadcastMessage(receivedMsg);
            } catch (IOException e) {
                // Close everything gracefully.
                closeEverything(socket, dIn, dOut);
                break;
            } catch (ClassNotFoundException e) {
                // Close everything gracefully.
                closeEverything(socket, dIn, dOut);
                break;
            }
        }
    }

    /**
     * Send a message through each client handler thread so that everyone gets the message.
     * Basically each client handler is a connection to the client. So far any message that
     * is received, loop through each connection and send it down it.
     * @param messageToSend
     */
    public void broadcastMessage(Message messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // You don't want to broadcast the message to the user who sent it.
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.dOut.writeObject(messageToSend);
                    clientHandler.dOut.flush();
                }
            } catch (IOException e) {
                // Gracefully close everything.
                closeEverything(socket, dIn, dOut);
            }
        }
    }

    // If the client disconnects for any reason remove them from the list so a message isn't sent down a broken connection.
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage(new Message("Server", clientUsername + " has left the chat!", null));
    }

    // Helper method to close everything so you don't have to repeat yourself.
    public void closeEverything(Socket socket, ObjectInputStream dIn, ObjectOutputStream dOut) {
        // The client disconnected or an error occurred so remove them from the list so no message is broadcasted.
        removeClientHandler();
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
}
