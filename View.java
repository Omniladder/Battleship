import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.event.ActionListener;

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
    boolean isClicked = false;
    ShipSquare testSquare;
    ObjectOutputStream out; // this is passed in from client, and passed to shipsquare, to send coordinates

    int xa, ya;
    int xb, yb;
    SidePanel gameSide;
    GameGrid gameGrid;
    JLabel scoreLabel;
    int[] cellIndexStarting;
    JButton startButton;

    View(Model gameState, ObjectOutputStream out) {

        int boardSize = 950;

        int sidePanelSize = 300;
        setSize(boardSize + sidePanelSize, boardSize);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                System.out.println("SHIPS SET IN PLACE");
                gameState.setCanMoveShips(false);
                startButton.setEnabled(false);
            }
        });
        add(startButton);

        // Creates test Square to be used as ship
        // testSquare = new ShipSquare(gameGrid, out,);
        // add(testSquare);
        gameGrid = new GameGrid(10, getWidth() - sidePanelSize, getHeight(), new Point(sidePanelSize, 0), gameState);
        // updateView();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (gameState.getYourBoardIndex(x, y) != Model.ShipType.EMPTY) {
                    ShipSquare newSquare = new ShipSquare(gameGrid, out, gameState);
                    newSquare.addMouseListener(new UpdateScoreBar(gameState));
                    newSquare.setCellSquare(x, y);
                    add(newSquare);
                    setVisible(true);
                }
            }
        }

        add(gameGrid);
        setVisible(true);
        this.gameState = gameState;
        gameGrid.addMouseListener(new UpdateScoreBar(gameState));

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
        public void mousePressed(MouseEvent e) {
            // Remove existing top bar
            // get mouse x and y
            Point p = e.getPoint();
            int[] pcoords = gameGrid.getCellInside(p);
            xa = pcoords[0];
            ya = pcoords[1];
            // get cell index
            cellIndexStarting = gameGrid.getCellInside(new Point(xa, ya));
            System.out.println("Mouse Pressed at: " + xa + ", " + ya);
            scoreLabel.setText("Score: " + gameState.getScore());
            System.out.println("Score: " + gameState.getScore());
            repaint(); // Redraw UI
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            Point clickedLocation = event.getPoint();
            System.out.println("Mouse Released at: " + clickedLocation.getX() + ", " + clickedLocation.getY());
            int[] cellIndex = gameGrid.getCellInside(clickedLocation);
            System.out.println("Cell Index: " + cellIndex[0] + ", " + cellIndex[1]);
            if (cellIndex[0] == -1) {
                return;
            }

            // int[] cellLocation = gameGrid.getCellPosition(cellIndex);
            // ships are not placed. we can do stuff
            if (gameState.getCanMoveShips()) {
                gameState.moveShipFromAtoB(xa, ya, cellIndex[0], cellIndex[1]);
                gameState.printBoard();
                renderView();
                repaint();
            }

            try {
                // out.writeObject(new int[] { xPos, yPos });
                // out.flush();
            } catch (Exception e) {
                e.printStackTrace();
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
                System.out.println("SHIPS SET IN PLACE");
                gameState.setCanMoveShips(false);
                startButton.setEnabled(false);
            }
        });
        add(startButton);

        gameGrid = new GameGrid(10, getWidth() - 300, getHeight(), new Point(300, 0), gameState);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                int[] cellIndex = { x, y };
                Model.ShipType yourBoard = gameState.getYourBoardIndex(cellIndex[0], cellIndex[1]);
                if (yourBoard != Model.ShipType.EMPTY) {
                    ShipSquare newSquare = new ShipSquare(gameGrid, out, gameState);
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
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(scoreLabel, BorderLayout.NORTH);

        // Creates Side Panel to Hold initial Ships
        gameSide = new SidePanel(300, getHeight());
        add(gameSide);

        repaint();
        revalidate();

    }

}
