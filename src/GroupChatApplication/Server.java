package GroupChatApplication;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

// A server listens for and establishes connections between clients using a server socket.

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Server(ServerSocket serverSocket) {
        try {
            this.serverSocket = serverSocket;
            this.socket = serverSocket.accept();
            //wraps input and ouptut stream
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("ERROR creating server");
            e.printStackTrace();
        }
    }

    public void startServer() {
        try {
            // Infinite loop to continuously look for clients wanting to establish connection
            while (!serverSocket.isClosed()) {

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

    // sendMessageToClient
    //**************************************
    public void sendMessageToClient(String messageToClient){
        try{
            bufferedWriter.write(messageToClient);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch(IOException e){
            e.printStackTrace();
            System.out.println("Error sending message to the client");
            closeEverything(socket, bufferedReader,bufferedWriter);
        }
    }


    // receiveMessageFromClient
    //**************************************
    public void receiveMessageFromClient(VBox vBox) {
        new Thread(new Runnable() {
            @Override
            public void run() { // listen to messages while client is connected
                while (socket.isConnected()){
                    try{
                        String messageFromClient = bufferedReader.readLine();
                        ServerController.addLabel(messageFromClient, vBox);
                    } catch(IOException e){
                        e.printStackTrace();
                        System.out.println("Error receiving mesasge from client");
                        break;
                    }
                }
            }
        }).start();
    }

    // closeEverything
    //**************************************
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if (bufferedReader != null)
                bufferedReader.close();
            if(bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    // Run the server.
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5555);     // Assuming ServerSocket gets IP address of local machine
        Server server = new Server(serverSocket);
        server.startServer();
    }

}
