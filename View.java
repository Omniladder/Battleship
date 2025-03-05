import javax.swing.*;
import java.awt.*;

/**
 * View object works as front end to the game takes User Inputs and runs
 */

class SidePanel extends JComponent {
    int width, height = 0;
    Color backgroundColor;

    SidePanel(int width, int height) {
        setSize(width, height);

        this.width = width;
        this.height = height;

        String hexColor = "#003399";
        backgroundColor = Color.decode(hexColor);
        setBackground(backgroundColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call the super method first

        // Set the color for the square
        g.setColor(backgroundColor);

        // Draw a square (x, y, width, height)
        g.fillRect(0, 0, width, height);
    }
}

public class View extends JFrame {
    Model gameState;
    ShipSquare testSquare;

    SidePanel gameSide;
    GameGrid gameGrid;
    JPanel topBar;

    View(Model gameState, ImageIcon backgroundImage) {

        int boardSize = 950;

        int sidePanelSize = 300;
        setSize(boardSize + sidePanelSize, boardSize);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        topBar = new JPanel();
        topBar.setBackground(new Color(50, 50, 50)); // Dark gray color
        topBar.setBounds(0, 0, getWidth(), 50); // x, y, width, height
        add(topBar);

        gameGrid = new GameGrid(10, getWidth() - sidePanelSize, getHeight(), new Point(sidePanelSize, 0), gameState);
        add(gameGrid);
        setVisible(true);

        // Creates test Square to be used as ship
        testSquare = new ShipSquare(gameGrid);
        add(testSquare);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (gameState.getCellState(x, y) == 'S') {
                    ShipSquare newSquare = new ShipSquare(gameGrid);
                    newSquare.setCellSquare(x, y);
                    add(newSquare);
                    setVisible(true);
                }
            }
        }

        // Creates Side Panel to Hold initial Ships
        gameSide = new SidePanel(sidePanelSize, getHeight());
        add(gameSide);

    }

}