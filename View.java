import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    JLabel scoreLabel;

    View(Model gameState, ImageIcon backgroundImage) {

        int boardSize = 950;

        int sidePanelSize = 300;
        setSize(boardSize + sidePanelSize, boardSize);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameGrid = new GameGrid(10, getWidth() - sidePanelSize, getHeight(), new Point(sidePanelSize, 0), gameState);
        add(gameGrid);
        setVisible(true);

        gameGrid.addMouseListener(new UpdateScoreBar(gameState));

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
            System.out.println(gameState.getScore());
            repaint(); // Redraw UI
        }
    }

}
