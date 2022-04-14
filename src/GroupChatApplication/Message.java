package GroupChatApplication;

import java.io.Serializable;
import javafx.scene.image.Image;

/**
 * This class is used to store the message that is sent to the server.
 * 
 * @author Matt De Binion
 */
public class Message implements Serializable {
    
    public String username;         // Username of the sender
    public String message;          // The message content of the sender
    public Image image;             // Image data from the sender (will be null if no image attached).

    /**
     * Create a message object given parameters.
     * @param username
     * @param message
     * @param image
     */
    public Message(String username, String message, Image image) {
        this.username = username;
        this.message = message;
        this.image = image;
    }
}
