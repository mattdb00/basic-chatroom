package GroupChatApplication;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class run extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ChatBox.fxml"));
        Parent root = loader.load();
        ChatBoxController handler = loader.getController();
        Scene scene = new Scene(root);



        stage.setTitle("Chat Box");
        stage.setScene(scene);
        stage.show();

        handler.displayInternalMessage("You are not connected to a server!");
        handler.button_send.setDisable(true);
        handler.tf_message.setDisable(true);

        Dialog<Pair<String, String>> connectionInfo = new Dialog<>();
        connectionInfo.setTitle("Connect");
        connectionInfo.setHeaderText("Connect to a chat server");

        ButtonType connectButton = new ButtonType("Connect", ButtonData.OK_DONE);
        connectionInfo.getDialogPane().getButtonTypes().addAll(connectButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        TextField fullIP = new TextField();
        fullIP.setPromptText("IP Address");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("IP Address and Port:"), 0, 1);
        grid.add(fullIP, 1, 1);

        Node connectBtn = connectionInfo.getDialogPane().lookupButton(connectButton);
        connectBtn.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) -> {
            connectBtn.setDisable(newValue.trim().isEmpty());
        });

        connectionInfo.getDialogPane().setContent(grid);

        connectionInfo.setResultConverter(dialogButton -> {
            if (dialogButton == connectButton) {
                return new Pair<>(username.getText(), fullIP.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = connectionInfo.showAndWait();

        result.ifPresent(userIP -> {
            String displayName = userIP.getKey();
            String[] split = userIP.getValue().split(":");
            String ip = split[0];
            int port = Integer.parseInt(split[1]);

            handler.displayInternalMessage("Connecting to " + ip + ":" + port + "...");

            try {
                Socket socket = new Socket(ip, port);

                Client client = new Client(handler, socket, displayName);
                handler.setClient(client);
                handler.displayInternalMessage("Connected to server!");
                handler.button_send.setDisable(false);
                handler.tf_message.setDisable(false);

                client.sendMessage("", null);   // empty message notifies server that client is connected.
            } catch (IOException e) {
                handler.displayInternalMessage("Could not connect to server!");
            }
        });

        // CLIENT CONNECTION STUFF
        Thread running = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.clientObj.listenForMessage();
            }
        });

        running.setDaemon(true);
        running.start();
    }

}