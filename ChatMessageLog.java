import java.util.Iterator;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatMessageLog extends Observable implements Iterable<ChatMessage> {

    private ConcurrentLinkedDeque<ChatMessage> chatMessages;
    private Thread messageDeleter;
    private ChatMessage newestMessage;

    /**
     * Constructor initializes the ConcurrentLinkedDeque and the MessageDeleter
     */
    public ChatMessageLog() {
        this.chatMessages = new ConcurrentLinkedDeque<>();
        this.messageDeleter = new Thread(new MessageDeleter(chatMessages));
        messageDeleter.start();
    }

    /**
     * Returns a new message object.
     *
     * @return      the newest message
     * @see         ChatMessage
     */
    public ChatMessage getNewMessage() {
        return this.newestMessage;
    }

    /**
     * Returns a new message object.
     * @param       message a ChatMessage to remove
     * @see         ChatMessage
     */
    public void removeMessage(ChatMessage message) {
        chatMessages.remove(message);
    }

    // threadsafe method only allows 5 incoming messages per second,
    // but doesn't lock the entire instance so that Observers can use getNewMessage()
    /**
     * Adds a message to the log.
     * @param       aMessage a ChatMessage to add
     * @see         ChatMessage
     */
    public void addMessage(ChatMessage aMessage) {
        synchronized (chatMessages) {
            this.newestMessage = aMessage;
            chatMessages.push(aMessage);
            setChanged();
            notifyObservers(aMessage.getSender());

            // only add 5 messages per second
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // when using the foreach loop on ChatMessageLog, you will automatically get oldest messages first
    @Override
    public Iterator<ChatMessage> iterator() {
        return chatMessages.descendingIterator(); // because it returns a descending iterator
    }
}
