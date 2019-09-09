import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private ExecutorService executorService;        // threads
    private ServerSocket greeter;                   // server
    public static Set<String> userNames;            // client names
    private ChatMessageLog chatMessageLog;          // shared messages
    public static boolean shutdown;                 // server shutdown


    public ChatServer() {
        shutdown = false;
        this.chatMessageLog = new ChatMessageLog();
        userNames = Collections.synchronizedSet(new HashSet<>());

        // Using executor service to manage threads
        this.executorService = Executors.newCachedThreadPool();

        // Open a socket for the server
        try {
            this.greeter = new ServerSocket(7777, 50);
            System.out.println("Binding to port " + greeter.getLocalPort() + ", please wait ...");
        } catch (IOException e) {
            System.out.println("Error binding server to socket" + e.getMessage());
            e.printStackTrace();
        }
    }


    //------------------------------------------------------
    // Main
    // Description: Creates server, accepts incoming connections.
    // when a connection is made, it creates a new ChatUserHandler
    // passing in the new socket, list of messages, and starting
    // a new thread with the executorService. New thread is a ChatUserHandler
    //------------------------------------------------------
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        System.out.println("Server ready, waiting for client... ");

        while (!shutdown) {

            try {
                // Set up a new client
                Socket newClient = chatServer.greeter.accept();
                System.out.println("Client accepted: " + newClient.getPort());

                // Set up new client threads
                chatServer.executorService.execute(new ChatUserHandler(
                        newClient, chatServer.chatMessageLog, chatServer.executorService));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}