import java.util.concurrent.ConcurrentLinkedDeque;

//----------------------------------------------------------------------------
// Checks Twice a second to see if there are more messages in the log
// than MAX.  If so, deletes the difference that was recorded at that instant
//----------------------------------------------------------------------------

public class MessageDeleter implements Runnable {

    private ConcurrentLinkedDeque<ChatMessage> chatMessages;
    static final int MAX = 25; // maximum messages that will be saved and sent to new users


    /**
     * Constructor
     * Accepts the list of ChatMessages and removes the last one.
     * @param       chatMessages a ConcurrentLinkedDeque made up of ChatMessage
     *
     * @see         ChatMessage
     */
    public MessageDeleter(ConcurrentLinkedDeque<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public void run() {
        while (!ChatServer.shutdown) {
            int currentSize = chatMessages.size(); // avoid overhead of repeated calls to size()
            if (currentSize > MAX) {
                while (currentSize > MAX) {
                    chatMessages.removeLast();
                    currentSize--;
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
