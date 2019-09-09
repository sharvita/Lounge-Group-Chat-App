import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatClient implements Runnable {
    public static boolean enableImages;
    public static boolean shutdown;
    private String userName;
    private Socket serverConnection;
    private ObjectOutputStream output;
    private Controller controller;

    /**
     * Opens the socket to server, sets output stream to server
     * @param controller
     */
    public ChatClient(Controller controller) {
        enableImages = false;
        this.controller = controller;
        try {
            this.serverConnection = new Socket("localhost", 7777); // change to public ip of server, ensure port forwarding active
            this.output = new ObjectOutputStream(serverConnection.getOutputStream());
            controller.setOutputStream(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        shutdown = false;
    }

    @Override
    public void run() {
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(serverConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // give the scene time to appear
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TxtMessage serverMsg = new TxtMessage("Server"," already exists ");
        printTxtMessage(new TxtMessage("Server","Welcome user! Please enter a username."));
        while (serverMsg.contains("already exists")) {
            String confirm = "n";
            Controller.sendToServer = false;
            while (!confirm.equals("y\n")) {
                String lastLine = controller.getLastLine();
                while (controller.getLastLine().equals(lastLine)) { // wait for user to input text
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.userName = controller.getLastLine();
                this.userName = userName.trim();
                printTxtMessage(new TxtMessage("Server", "Are you satisfied with the username " +
                        this.userName + "? (y or n)"));
                lastLine = controller.getLastLine();
                while (controller.getLastLine().equals(lastLine)) { // wait for user to input text
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                confirm = controller.getLastLine();
                if (!confirm.toLowerCase().equals("y\n")) {
                    printTxtMessage(new TxtMessage("Server", "Please enter a new username"));
                }
            }
            controller.setUsername(userName);
            Controller.sendToServer = true; // start having enter send txt to server
            try {
                output.writeObject(new TxtMessage("UnnamedUser", userName));
                serverMsg = (TxtMessage) input.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            printTxtMessage(serverMsg);
        }
        Thread listenThread = new Thread(new MessageListener(input, controller));
        listenThread.start();
        enableImages = true;
        ChatMessage userMsg = new TxtMessage("", this.userName + " has joined the chat room");
        try {
            output.writeObject(userMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userMsg.setSender(this.userName);

        // This is the bulk of ChatClient's life, just waits for the controller to shut down
        // then sends a message to the server when it does so that the server is not stuck waiting to read a message
        while (!ChatClient.shutdown) {
            if (Controller.shutdown) {
                try {
                    output.writeObject(new TxtMessage(userName, "SHUTDOWN_NOW"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ChatClient.shutdown = true;
            }
        }
        return;
    }

    /**
     * Sends a pre-constructed TxtMessage to the controller's output method on the fx thread
     * This is only for username setup
     * @param chatMessage
     * @see ChatMessage
     */
    private void printTxtMessage(TxtMessage chatMessage) {
        Platform.runLater(() -> controller.printTxt(chatMessage)); // avoid exception from operating on non fx thread
        try {
            Thread.sleep(250); // wait for action to be complete before allowing program to continue
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
