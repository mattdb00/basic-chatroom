package GroupChatApplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Observable;


public class ChatGUI extends Observable implements Initializable, Observer {

    public static int userCount = 0;

    @FXML
    public TextArea allMessages;

    @FXML
    public TextArea currentMessage;

    @FXML
    public void sendTheMessage(KeyEvent keyEvent) throws IOException {
        String message = currentMessage.getText();
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {
            if(currentMessage.getLength() > 0) {
                currentMessage.clear();

                if(allMessages.getLength() > 0) {
                    StringBuilder fieldContent = new StringBuilder(allMessages.getText());
                    fieldContent.append(message);
                    allMessages.setText(fieldContent.toString());
                }
                else {
                    allMessages.setText(message);
                }
            }
            passMessageToClient(message);
        }
    }

    public void btSendMessage(MouseEvent mouseEvent) {
        String message = currentMessage.getText();
        if(currentMessage.getLength() > 0) {
            currentMessage.clear();

            if(allMessages.getLength() > 0) {
                StringBuilder fieldContent = new StringBuilder(allMessages.getText());
                fieldContent.append(message + "\n");
                allMessages.setText(fieldContent.toString());
            }
            else {
                allMessages.setText(message + "\n");
            }
        }
        passMessageToClient(message);
    }

    private void passMessageToClient(String message)
    {
        setChanged();
        notifyObservers(message);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // instantiate client
        String username = "User #" + ++userCount;  // FIX THIS LATER
        Client client = new Client(username);

        // make chat gui observable to client
        addObserver(client);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
