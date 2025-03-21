import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.event.ActionListener;

//import Model.ShipType;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.io.File;
import java.io.ObjectInputStream;
import java.awt.image.BufferedImage;
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
    boolean isClicked = false;
    ShipSquare testSquare;
    ObjectOutputStream out; // this is passed in from client, and passed to shipsquare, to send coordinates
    String logMessage = "Place Your Ships";

    int xa, ya;
    int xb, yb;
    int[] cellIndexStarting;

    SidePanel gameSide;
    GameGrid gameGrid;
    JLabel scoreLabel;
    JLabel logBox;
    
    JButton startButton;

    int boardSize;
    int sidePanelSize;

    View(Model gameState, ObjectOutputStream out) {

        boardSize = 1000;

        sidePanelSize = 300;
        setSize(boardSize + sidePanelSize + 12, boardSize + sidePanelSize);

        String hexColor = "#003399";
        Color backgroundColor = Color.decode(hexColor);
        getContentPane().setBackground(backgroundColor);
        this.gameState = gameState;
        gameState.setLog("Place Your Ships");
        renderView();
    }

    private class UpdateScoreBar extends MouseAdapter {
        Model gameState;

        UpdateScoreBar(Model gameState) {
            this.gameState = gameState;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // Remove existing top bar
            // get mouse x and y
            Point p = e.getPoint();
            int[] pcoords = gameGrid.getCellInside(p);
            xa = pcoords[0];
            ya = pcoords[1];
            // get cell index
            cellIndexStarting = gameGrid.getCellInside(new Point(xa, ya));
            scoreLabel.setText("Score: " + gameState.getScore());
            repaint(); // Redraw UI
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            Point clickedLocation = event.getPoint();
            int[] cellIndex = gameGrid.getCellInside(clickedLocation);
            if (cellIndex[0] == -1) {
                return;
            }

            // ships are not placed. we can do stuff
            if (gameState.getCanMoveShips()) {
                gameState.moveShipFromAtoB(xa, ya, cellIndex[0], cellIndex[1]);
                renderView();
                repaint();
            } else {

                Model.CellStatus clickedType = gameState.getTheirBoardIndex(cellIndex[0], cellIndex[1]);

                if (!gameState.isPlayersTurn() || clickedType != Model.CellStatus.DONTKNOW) {
                    return;
                }

                gameState.shoot(cellIndex[0], cellIndex[1]);
                renderView();
                setVisible(true);
                repaint();
                gameState.waitForOpponent();
            }

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

    public void updateLogMessage(String message){
        logMessage = message;
        renderView();
    }

    public void renderView() {
        // removeAll();
        getContentPane().removeAll();

        startButton = new JButton("Play Game");
        startButton.setPreferredSize(new Dimension(300, 150));
        startButton.setBounds(50, 450, 150, 75);
        startButton.setBackground(new Color(30, 144, 255)); // Button background
        startButton.setForeground(Color.WHITE); // Text color
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBorder(new LineBorder(Color.WHITE, 2, true)); // White border, 2px, rounded
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                gameState.setCanMoveShips(false);
                startButton.setEnabled(false);
            }
        });
        add(startButton);

        gameGrid = new GameGrid(10, boardSize, boardSize, new Point(sidePanelSize, 0), gameState);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                int[] cellIndex = { x, y };
                Model.ShipType yourBoard = gameState.getYourBoardIndex(cellIndex[0], cellIndex[1]);
                if (yourBoard != Model.ShipType.EMPTY) {
                    ShipSquare newSquare = new ShipSquare(gameGrid, out, gameState, true);
                    try {
                        newSquare.assignImage(gameState.getShipPic(x, y));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    newSquare.addMouseListener(new UpdateScoreBar(gameState));
                    newSquare.setCellSquare(x, y);
                    add(newSquare);
                    setVisible(true);
                }
            }
        }

        add(gameGrid);
        setVisible(true);
        gameGrid.addMouseListener(new UpdateScoreBar(gameState));

        scoreLabel = new JLabel("Score: " + gameState.getScore());
        scoreLabel.setForeground(Color.BLACK); // Makes the text stand out on the dark background
        scoreLabel.setBackground(Color.WHITE);
        // scoreLabel.setMaximumSize(new Dimension(300, 50));
        scoreLabel.setOpaque(true);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(scoreLabel, BorderLayout.NORTH);

        logBox = new JLabel(gameState.getLog());
        logBox.setForeground(Color.BLACK); // Makes the text stand out on the dark background
        logBox.setBackground(Color.WHITE);
        logBox.setOpaque(true);
        logBox.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(logBox, BorderLayout.SOUTH);

        // Creates Side Panel to Hold initial Ships
        gameSide = new SidePanel(300, getHeight());
        add(gameSide);

        repaint();
        revalidate();

    }

}
