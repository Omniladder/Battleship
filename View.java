import javax.swing.*;
import java.awt.*;

/**
 * View object works as front end to the game takes User Inputs and runs
 */

public class View extends JFrame {
    Model gameState;
    ShipSquare testSquare;

    View(Model gameState, ImageIcon backgroundImage) {
        setSize(1000, 1000);
        testSquare = new ShipSquare();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(testSquare);
        setVisible(true);
    }
}