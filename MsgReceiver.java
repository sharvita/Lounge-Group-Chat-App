import java.io.ObjectInputStream;
import java.net.SocketException;

public class MsgReceiver implements Runnable {

    private ObjectInputStream input;
    private ChatMessageLog chatMessageLog;
    private String thisUser;
    private Boolean shutdown;


    /**
     * Constructor
     * Starts by getting the new user's socket, the list of user names,
     * the list of 25 recent messages, and the thread pool that the entire program is using
     *
     * @param       input an ObjectInputStream for the client
     * @param       chatMessageLog the log of messages between all users
     * @param       thisUser the name of the user
     * @param       shutdown a boolean of to determine if shutdown or not
     * @see         ChatMessageLog
     */
    public MsgReceiver(ObjectInputStream input, ChatMessageLog chatMessageLog, String thisUser, Boolean shutdown) {
        this.shutdown = shutdown;
        this.input = input;
        this.chatMessageLog = chatMessageLog;
        this.thisUser = thisUser;
    }


    //------------------------------------------------------------------------
    // When MsgReceiver run()
    // Description: first checks to make sure the server hasn't shutdown
    // and that it's corresponding MsgSender and ChatUserHandler haven't shut down
    // It checks to make sure the incoming message isn't a message that the user is signing off ("SHUTDOWN_NOW")
    // If it is, the sign off message is sent and the loop terminates
    //------------------------------------------------------------------------

    /**
     * First checks to make sure the server hasn't shutdown
     * and that it's corresponding MsgSender and ChatUserHandler haven't shut down
     * It checks to make sure the incoming message isn't a message that the user is signing off ("SHUTDOWN_NOW")
     * If it is, the sign off message is sent and the loop terminates
     *
     * @see         ChatUserHandler
     */
    @Override
    public void run() {
        // the first Boolean is user specific, the other would terminate all threads
        while(!shutdown && !ChatServer.shutdown) {
            try {
                ChatMessage chatMessage = (ChatMessage) input.readObject();

                // Other Client wants to terminate
                if (chatMessage instanceof TxtMessage && ((TxtMessage) chatMessage).equals("SHUTDOWN_NOW")) {
                    chatMessage.setSender("");
                    ((TxtMessage) chatMessage).setMessage(thisUser + " has left the room.");
                    chatMessageLog.addMessage(chatMessage);
                    ChatServer.userNames.remove(thisUser);
                    this.shutdown = true;
                }
                // Other Client's message
                else
                    chatMessageLog.addMessage(chatMessage);
            } catch (SocketException se) {
                this.shutdown = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
