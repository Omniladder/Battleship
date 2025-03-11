import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;

/**
 * Main Method for Battleship simply creates variables then runs game
 */
public class run {
    public static void main(String[] args) {
        Model gameState = new Model(10);
        // for (int i = 0; i < 50; i++)
        gameState.setShipsRandom();

        File file = new File("./images/waterBackground.jpeg");
        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(file); // This can throw IOException
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception (here we print the error)
        }
        ImageIcon imageIcon = new ImageIcon(bufferedImage);

        View gameInterface = new View(gameState, imageIcon);
        

        Controller gameController = new Controller(gameState, gameInterface);
        gameController.playGame();
    }
}