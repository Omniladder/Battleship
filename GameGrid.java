import javax.swing.*;

import java.awt.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

class GameGrid extends JComponent {
    int numOfCells;
    int boardWidth, boardHeight;
    int top, left;
    int cellWidth, cellHeight;
    int strokeSize = 5;
    Model gameState;

    GameGrid(int numOfCells, int boardWidth, int boardHeight, Point topLeft, Model gameState) {
        this.numOfCells = numOfCells;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        left = topLeft.x + strokeSize;
        top = topLeft.y + strokeSize;
        this.cellHeight = boardHeight / numOfCells;
        this.cellWidth = boardWidth / numOfCells;
        this.addMouseListener(new ClickListener());
        this.gameState = gameState;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call the super method first

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(strokeSize));
        // Draws Grid rows
        for (int i = 0; i <= numOfCells; i++) {
            g2d.drawLine(left + i * cellWidth, top, left + i * cellWidth, boardHeight + top);
        }

        // Draws Grid Columns
        for (int i = 0; i <= numOfCells; i++) {
            g2d.drawLine(left, top + i * cellHeight, boardWidth + left, top + i * cellHeight);
        }

    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int[] getCellInside(Point mousePosition) {
        int[] index = new int[2];
        index[0] = (int) (mousePosition.x - left) / cellWidth;
        index[1] = (int) (mousePosition.y - top) / cellHeight;

        // Validates if inside a cell
        if (index[0] > numOfCells || index[1] > numOfCells || (mousePosition.x - left) < 0
                || (mousePosition.y - top) < 0) {
            int[] dummyArr = new int[2];
            dummyArr[0] = -1;
            dummyArr[1] = -1;
            return dummyArr;
        }
        return index;
    }

    public int[] getCellPosition(int[] cellIndex) {
        // Input Validatipn
        if (cellIndex[0] > numOfCells || cellIndex[1] > numOfCells || cellIndex[0] < 0 || cellIndex[1] < 0) {
            int[] dummyArr = new int[2];
            dummyArr[0] = -1;
            dummyArr[1] = -1;
            return dummyArr;
        }

        // Gets Cell Posisiton
        int[] index = new int[2];
        index[0] = cellIndex[0] * cellWidth + left;
        index[1] = cellIndex[1] * cellHeight + top;
        return index;
    }

    private class ClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent event) {

            int[] cellIndex = getCellInside(event.getPoint());
            if (cellIndex[0] == -1 || !gameState.isPlayersTurn()) {
                return;
            }

            gameState.shoot(cellIndex[0], cellIndex[1]);
            // System.out.println(gameState.getTheirBoardIndex(cellIndex[0], cellIndex[1]));

            // int[] cellPos = getCellPosition(cellIndex);
            Shot newShot = new Shot(gameState.getTheirBoardIndex(cellIndex[0], cellIndex[1]), cellIndex,
                    GameGrid.this);
            add(newShot);

            // setOpaque(false);
            setVisible(true);
            repaint();
            gameState.waitForOpponent();
        }
    }
}