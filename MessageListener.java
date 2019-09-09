import javafx.application.Platform;

import java.io.ObjectInputStream;

public class MessageListener implements Runnable {

    private ObjectInputStream objectInputStream;
    private Controller controller;

    /**
     * Constructor
     * @param objectInputStream
     * @param controller
     */
    public MessageListener(ObjectInputStream objectInputStream, Controller controller) {
        this.objectInputStream = objectInputStream;
        this.controller = controller;
    }

    /**
     * reads and prints messages
     */
    public void run() {
        ChatMessage chatMessage = null;
        while (!ChatClient.shutdown) {
            try {
                chatMessage = (ChatMessage)objectInputStream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            printNewMessage(chatMessage);
        }
    }

    /**
     * Checks the type of the incoming ChatMessage to decide which output method to call
     * @param chatMessage
     */
    private void printNewMessage(ChatMessage chatMessage) {
        if (chatMessage instanceof TxtMessage) {
            TxtMessage txt = (TxtMessage)chatMessage;
            Platform.runLater(() -> controller.printTxt(txt)); // avoids exeptions from accessing controller outside main thread
        }
        else if (chatMessage instanceof  ImageMessage) {
            ImageMessage img = (ImageMessage)chatMessage;
            Platform.runLater(() -> controller.printImage(img));
            // test for other types of messages
        }
    }
}
