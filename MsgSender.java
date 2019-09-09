import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Observable;
import java.util.Observer;

public class MsgSender implements Observer {

    private ObjectOutputStream output;
    private ChatMessageLog chatMessageLog;
    private String thisUser;
    private Boolean shutdown;


    //----------------------------------------------------------
    // MessageSender has the list of 25 most recent chat messages
    // It starts by adding itself to the log's list of observers
    // and sending each of the 25 messages to the new user
    //----------------------------------------------------------

    /**
     * Constructor
     * It starts by adding itself to the log's list of observers
     * and sending each of the 25 messages to the new user
     *
     * @param       output an ObjectOutputStream for the client
     * @param       chatMessageLog the log of messages between all users
     * @param       thisUser the name of the user
     * @param       shutdown a boolean of to determine if shutdown or not
     * @see         ChatMessageLog
     * @see         Observer
     */
    public MsgSender(ObjectOutputStream output, ChatMessageLog chatMessageLog, String thisUser, Boolean shutdown) {
        this.shutdown = shutdown;
        this.output = output = output;
        this.chatMessageLog = chatMessageLog;
        this.thisUser = thisUser;
        chatMessageLog.addObserver(this);

        // send messages to user
        for (ChatMessage chatMessage : chatMessageLog) {
            try {
                output.writeObject(chatMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //------------------------------------------------------------------
    // Any time ChatMessageLog gets a new message, MsgSender is updated
    // If the message is one of a user entering or leaving, the message is
    // deleted from the message list after it is sent
    // if the user exits the program by clicking the X on the window, an
    // exception is thrown because the socket is closed, and the username is
    // removed from the username Set
    // yes, we realize that exceptions should be exceptional, not routine.
    //------------------------------------------------------------------

    /**
     * Gets a new message, MsgSender is updated.
     * If the message is one of a user entering or leaving, the message is
     * deleted from the message list after it is sent
     * if the user exits the program by clicking the X on the window, an
     * exception is thrown because the socket is closed, and the username is
     * removed from the username Set
     *
     * @param       o an Observable object
     * @param       arg an object
     * @see         ChatMessageLog
     * @see         Observer
     */
    @Override
    public synchronized void update(Observable o, Object arg) {
        if (!shutdown) {
            try {
                ChatMessage chatMessage = chatMessageLog.getNewMessage();
                output.writeObject(chatMessage);

                // Updates chat if a client has terminated or joined
                if (chatMessage instanceof TxtMessage) {
                    if (((TxtMessage)chatMessage).equals(thisUser + " has left the room.")
                            || ((TxtMessage)chatMessage).equals(thisUser + " has joined the chat room"))
                        chatMessageLog.removeMessage(chatMessage);
                }
            } catch (IOException e) {
                System.out.println("Removing " + thisUser + " from Message Log Observers");
                chatMessageLog.deleteObserver(this);
            }
        }
        else {
            // MsgReceiver would have changed the reference to shutdown Boolean
            try {
                TxtMessage goodByeMsg = (TxtMessage)chatMessageLog.getNewMessage();
                output.writeObject(goodByeMsg);
                chatMessageLog.removeMessage(goodByeMsg);
                chatMessageLog.deleteObserver(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
