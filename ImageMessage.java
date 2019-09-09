import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageMessage extends ChatMessage implements Serializable {

    private transient BufferedImage image;

    /**
     * Constructor
     * Gets the sender and the file being sent.
     *
     * @param       sender a string containing the name of the client.
     * @param       file the image to be sent.
     * @see         ChatMessage
     */
    public ImageMessage(String sender, File file) {
        super(sender);
        if (file == null) this.image = null;
        else {
            try {
                this.image = ImageIO.read(file);
            } catch (IOException e) {
                System.out.println("Error reading in image");
                this.image = null;
            }
        }
    }

    /**
     * Gets the image.
     * @return      the image.
     * @see         ChatMessage
     */
    public BufferedImage getImage() {
        return image;
    }


    /**
     * Writes the image to the server.
     * @param       out an ObjectOutputStream of the image.
     * @see         ChatMessage
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();// Create a buffered image with transparency
        ImageIO.write(this.image, "png", out); // png is lossless
    }

    /**
     * Reads an image from the server.
     * @param       in an ObjectInputStream of the image.
     * @see         ChatMessage
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.image = ImageIO.read(in);
    }
}
