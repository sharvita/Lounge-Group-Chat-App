import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.LocalTime.now;

public class TxtMessage extends ChatMessage implements Serializable {

    private String message;


    /**
     * Constructor
     * Accepts the sender name and the sender's message.
     * @param       sender a string holding the name of the sender.
     * @param       message a string holding the message.
     *
     * @see         ChatMessage
     */
    public TxtMessage(String sender, String message) {
        super(sender);
        this.message = message;
    }


    /**
     * Accepts a message to set.
     * @param       message a string holding the message.
     *
     * @see         ChatMessage
     */
    public void setMessage(String message) {
        this.message = message;
    }


    /**
     * Gets the message.
     * @return      a String consisting of the message.
     * @see         ChatMessage
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "@" + dtf.format(now())+ " " + getSender() +  ": " + message;
    }


    /**
     * Converts the message to lower case.
     * @return      a String consisting of the message but now lower case.
     * @see         ChatMessage
     */
    public String toLowerCase() {
        return message.toLowerCase();
    }


    // TxtMessage can "equal" another message or a String
    public boolean equals(TxtMessage obj) {
        return this.message.equals((obj).message);
    }

    public boolean equals(String obj) {
        return this.message.equals(obj);
    }

    public boolean contains(String obj) {
        return this.message.contains(obj);
    }
}
