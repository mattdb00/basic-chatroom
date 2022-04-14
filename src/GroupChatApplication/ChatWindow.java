package GroupChatApplication;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.net.Socket;

public class ChatWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ChatGUI.fxml"));
        primaryStage.setTitle("Group Chat Application");
        primaryStage.setScene(new Scene(root, 830, 554));
        primaryStage.show();
    }
}
