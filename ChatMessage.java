import java.io.Serializable;

public class ChatMessage implements Serializable {

    private String sender;

    /**
     * Constructor
     * Sets the sender value to "USER"
     */
    public ChatMessage() {
        this.sender = "USER";
    }

    /**
     * Constructor
     * Sets the sender value to the string name of the sender.
     * @param       sender a String containing the name of the sender.
     */
    public ChatMessage(String sender) {
        this.sender = sender;
    }

    /**
     * Returns the name of the sender.
     * @return      sender name as String.
     * @see         ChatMessage
     */
    public String getSender() {
        return this.sender;
    }

    /**
     * Sets the sender's name.
     * @param       sender the name of a sender as String
     */
    public void setSender(String sender) {
        this.sender = sender;
    }
}
