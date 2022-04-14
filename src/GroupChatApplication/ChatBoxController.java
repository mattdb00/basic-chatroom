package GroupChatApplication;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * The ChatBox controller class for this application.
 */
public class ChatBoxController implements Initializable {

    @FXML
    public Button button_send;
    @FXML
    public TextField tf_message;
    @FXML
    public VBox vbox_messages;

    public Client clientObj;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {}

    public void setClient(Client client) {
        this.clientObj = client;
    }

    /**
     * When the button is pressed, it puts it in the window.
     * @param event
     */
    public void buttonSendPressed(ActionEvent event) {

        Label label;
        if(clientObj == null) {
            label = new Label("[N/A]: " + tf_message.getText());
        } else {
            label = new Label("["+ clientObj.getUsername() +"]: " + tf_message.getText());
        }

        clientObj.sendMessage(tf_message.getText(), null);
        tf_message.clear();
        vbox_messages.getChildren().add(label);
    }

    /**
     * Displays a Message object to the terminal.
     * @param Message
     */
    public void displayMessage(Message msg) {

        Platform.runLater(() -> {
            Label label = new Label("[" + msg.username + "]: " + msg.message);
            vbox_messages.getChildren().add(label);
        });
    }

    /**
     * Display an internal message to the chat box.
     * @param msg
     */
    public void displayInternalMessage(String message) {
        Label label = new Label("[INTERNAL]: " + message);
        vbox_messages.getChildren().add(label);
    }
}