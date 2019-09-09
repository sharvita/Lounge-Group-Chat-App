import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class Controller {

    public static boolean sendToServer;
    public static boolean shutdown;
    private String username;

    @FXML
    private ListView outputTextArea;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private Button sendButton;
    @FXML
    private Button imageButton;
    @FXML
    private ScrollPane imagePane;
    @FXML
    private VBox images;

    private String txtMessage;
    private ObjectOutputStream outputStream;

    public Controller() {
        shutdown = false;
        sendToServer = false;
    }

    public void initialize() {
        outputTextArea.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void actionEnter(){
        inputTextArea.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER && inputTextArea.getText() != null) {
                if (sendToServer &&  !inputTextArea.getText().equals("SHUTDOWN_NOW"))
                    sendTxt();
                else
                    printTxt();
            }
        });
    }

    public void actionSendButton() {
        if (inputTextArea.getText() == null) return;
        if (sendToServer &&  !inputTextArea.getText().equals("SHUTDOWN_NOW"))
            sendTxt();
        else
            printTxt();
    }

    public void actionImageButton() {
        if (!ChatClient.enableImages) return;
        FileChooser fileChooser = new FileChooser();
        Stage fileOpenStage = new Stage();
        fileOpenStage.setTitle("Choose an image file (JPG only)");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file == null) return;
        String extension = file.getName();
        int lastIndexOf = extension.lastIndexOf(".");
        if (lastIndexOf == -1) {

        }
        else extension = extension.substring(lastIndexOf).toLowerCase();
        if (!extension.equals(".jpg")) {
            printTxt(new TxtMessage("", "Unsupported file type"));
            return;
        }
        ImageMessage newImageMsg = new ImageMessage(username, file);
        sendImage(newImageMsg);
    }

    private void sendImage(ImageMessage imageMessage) {
        if (outputStream != null) {
            try {
                outputStream.writeObject(imageMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendTxt(new TxtMessage("", username + " just sent an image!"));
        }
    }

    public String getLastLine() {
        return (String)outputTextArea.getItems().get(outputTextArea.getItems().size() - 1);

    }

    public void printTxt(TxtMessage txt) {
            outputTextArea.getItems().add(txt.toString());
    }

    public void sendTxt(TxtMessage txt) {
        if (outputStream != null) {
            try {
                outputStream.writeObject(txt);
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputTextArea.setText(null);
        }
    }

    public void printImage(ImageMessage img) {
        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(img.getImage(), null));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(400);
        images.getChildren().add(imageView);
        imagePane.setContent(images);
    }

    @FXML
    public void printTxt() {
        txtMessage = inputTextArea.getText();
        inputTextArea.setText(null);
        outputTextArea.getItems().add(txtMessage);
    }

    @FXML
    public void sendTxt() {
        if (outputStream != null) {
            try {
                outputStream.writeObject(new TxtMessage(username, inputTextArea.getText().trim()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputTextArea.setText(null);
        }
    }
}
