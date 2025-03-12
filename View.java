import javax.swing.*;

//import Model.ShipType;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
    ObjectOutputStream out; // this is passed in from client, and passed to shipsquare, to send coordinates

    SidePanel gameSide;
    GameGrid gameGrid;
    JLabel scoreLabel;

    View(Model gameState, ImageIcon backgroundImage, ObjectOutputStream out) {

        int boardSize = 950;

        int sidePanelSize = 300;
        setSize(boardSize + sidePanelSize, boardSize);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameGrid = new GameGrid(10, getWidth() - sidePanelSize, getHeight(), new Point(sidePanelSize, 0), gameState);
        add(gameGrid);
        setVisible(true);

        gameGrid.addMouseListener(new UpdateScoreBar(gameState));

        // Creates test Square to be used as ship
        // testSquare = new ShipSquare(gameGrid, out,);
        // add(testSquare);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (gameState.getYourBoardIndex(x, y) != Model.ShipType.EMPTY) {
                    ShipSquare newSquare = new ShipSquare(gameGrid, out, gameState);
                    newSquare.setCellSquare(x, y);
                    add(newSquare);
                    setVisible(true);
                }
            }
        }

        scoreLabel = new JLabel("Score: " + gameState.getScore());
        scoreLabel.setForeground(Color.BLACK); // Makes the text stand out on the dark background
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(scoreLabel, BorderLayout.NORTH);

        // Creates Side Panel to Hold initial Ships
        gameSide = new SidePanel(sidePanelSize, getHeight());
        add(gameSide);
    }

    private class UpdateScoreBar extends MouseAdapter {
        Model gameState;

        UpdateScoreBar(Model gameState) {
            this.gameState = gameState;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // Remove existing top bar
            scoreLabel.setText("Score: " + gameState.getScore());
            System.out.println("Score: " + gameState.getScore());
            repaint(); // Redraw UI
        }
    }

    // Method for client to get current position
    public int[] getShipSquarePosition() {
        return new int[] { testSquare.getXPosition(), testSquare.getYPosition() };
    }

    // Method for view to be updated by client
    public void updateShipSquarePosition(int x, int y) {
        testSquare.setPosition(x, y);
    }
}
