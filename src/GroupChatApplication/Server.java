package GroupChatApplication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

// A server listens for and establishes connections between clients using a server socket.

public class Server {

    private final ServerSocket serverSocket;
    private final static int port = 6000;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            // Infinite loop to continuously look for clients wanting to establish connection
            while (!serverSocket.isClosed()) {
                System.out.println("Waiting on a connection to a client...");
                // This socket is closed in ClientHandler class
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                // Call run() method of ClientHandler to start a new thread to handle each client
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    // Close the server socket.
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Run the server.
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);     // Assuming ServerSocket gets IP address of local machine
        Server server = new Server(serverSocket);
        server.startServer();
    }

}
