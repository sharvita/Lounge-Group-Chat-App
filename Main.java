import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent login = FXMLLoader.load(getClass().getResource("login.fxml"));
//        primaryStage.setTitle("");
//        primaryStage.setScene(new Scene(login,600,400));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatWindow.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        ChatClient theClient = new ChatClient(controller); // update with controller
        Thread chatThread = new Thread(theClient);
        chatThread.start();
        primaryStage.setTitle("");
//        primaryStage.show();
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }


    public static void main(String[] args)
    {
        launch(args);
        Controller.shutdown = true;
        System.exit(0);
    }
}
