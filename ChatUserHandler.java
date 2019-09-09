import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class ChatUserHandler implements Runnable {

    private Socket aClient;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private ChatMessageLog chatMessageLog;
    private String thisUser;
    private ExecutorService executorService;
    private MsgSender msgSender;
    private MsgReceiver msgReceiver;
    public Boolean shutdown;


    //------------------------------------------------------
    // ChatUserHandler Constructor
    // Description: starts by getting the new user's socket, the list of user names,
    // the list of 25 recent messages, and the thread pool that the entire program is using
    //------------------------------------------------------
    /**
     * Constructor
     * Starts by getting the new user's socket, the list of user names,
     * the list of 25 recent messages, and the thread pool that the entire program is using
     *
     * @param       aClient a socket for the client
     * @param       chatMessageLog the log of messages between all users
     * @param       executorService the executor service to handle the thread pool
     * @see         ChatMessageLog
     */
    public ChatUserHandler(Socket aClient, ChatMessageLog chatMessageLog, ExecutorService executorService) {

        this.shutdown = false;
        this.aClient = aClient;
        this.chatMessageLog = chatMessageLog;
        this.executorService = executorService;

        try {
            input = new ObjectInputStream(aClient.getInputStream());
            output = new ObjectOutputStream(aClient.getOutputStream());

        } catch (IOException e) {
            System.out.println("Exception thrown while attempting to open Object Streams");
            try {
                aClient.close();
            } catch (IOException e1) {
                System.out.println("Exception thrown while trying to close client Socket");
            }
        }
    }


    //------------------------------------------------------
    // getChatUserName()
    // Description: Used once to get username from client.
    // Returns: username as TxtMessage
    //------------------------------------------------------
    /**
     * Used once to get username from client.
     *
     * @return      a TxtMessage holding the user's name
     * @see         TxtMessage
     */
    public TxtMessage getChatUserName() {
        // Gets username
        TxtMessage nameMsg = new TxtMessage("user","");

        // loops until an original name is received
        while (nameMsg.equals("")) {
            try {
                nameMsg = (TxtMessage) this.input.readObject();
                for (String existingName : ChatServer.userNames) {
                    if (existingName.toLowerCase().equals(nameMsg.toLowerCase())) {
                        output.writeObject(new TxtMessage("Server", existingName +
                                " already exists. Choose a different name."));
                        nameMsg.setMessage("");
                        break;
                    }
                }
            } catch (SocketException se) {
                this.shutdown = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return nameMsg;
    }

    /**
     * Sends welcome message to new client.
     *
     * @see         TxtMessage
     */
    public void welcomeMessage() {
        // welcome the user to the ChatRoom, send list of active users
        try {
            output.writeObject(new TxtMessage("Server","Welcome " + thisUser));

            if (ChatServer.userNames.size() > 1) {
                TxtMessage userList = new TxtMessage("", "Active Users: ");

                for (String user : ChatServer.userNames)
                    userList.setMessage(userList.getMessage() + user + ", ");

                userList.setMessage(userList.getMessage().substring(0,userList.getMessage().length() - 2));
                output.writeObject(userList);
            }
        } catch (IOException e) {
            System.out.println("Exception thrown while sending welcome message to user");
        }
    }


    //------------------------------------------------------
    // ChatUserHandler run() first gets the username. At this point, only text
    // messages will be received, which is the responsibility of the client to ensure
    //------------------------------------------------------
    /**
     * First gets the username. At this point, only text
     * messages will be received, which is the responsibility of the client to ensure
     *
     * Then creates MsgSender and MsgReceiver objects for the client.
     *
     * @see         MsgReceiver
     * @see         MsgSender
     */
    @Override
    public void run() {

        if (this.output == null)
            return; // exception thrown in constructor

        TxtMessage nameMsg = getChatUserName();

        // add the username to the set on the server
        ChatServer.userNames.add(nameMsg.getMessage());
        this.thisUser = nameMsg.getMessage();

        welcomeMessage();

        // create new MsgSender with output stream to new user and reference to shutdown Boolean
        // execute new MsgReceiver Runnable with input stream from new client and shutdown Boolean
        this.msgSender = new MsgSender(output, chatMessageLog, thisUser, this.shutdown);
        this.msgReceiver = new MsgReceiver(input, chatMessageLog, thisUser, this.shutdown);

        executorService.execute(msgReceiver);
    }
}
