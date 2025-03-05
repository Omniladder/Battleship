
/**
 * Battleship model class holds all data relating to current state of the game
 * as well as manages aspects of functionality such as server validation etc.
 */

import java.util.Random;

public class Model {
    char[][] gameBoard;
    int boardSize;
    boolean playerMove = true;
    int score;

    Model(int boardSize) {
        gameBoard = new char[boardSize][boardSize];
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                gameBoard[x][y] = ' '; // ' ' Indicated No Ships
            }
        }
        this.boardSize = boardSize;
        score = 0;
    }

    private void addShipSquare(int x, int y) {
        if (x > 0 && x < boardSize && y > 0 && y < boardSize) {
            gameBoard[x][y] = 'S'; // 'S' Indicates Ship
        }
    }

    private void removeShipSquare(int x, int y) {
        if (x > 0 && x < boardSize && y > 0 && y < boardSize) {
            gameBoard[x][y] = ' ';
        }
    }

    private boolean isValidShipLocation(int shipTop, int shipLeft, int shipSize, boolean direction) {
        if (direction) {
            for (int i = 0; i < shipSize; i++) {
                if (gameBoard[shipLeft + i][shipLeft] != ' ') {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < shipSize; i++) {
                if (gameBoard[shipLeft][shipLeft + i] != ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private void addShipRandom(int shipSize) {
        int shipTop, shipLeft;
        boolean direction; // 1 is left to right 0 is top down
        Random rand = new Random();

        do {
            // Randomly choose direction
            direction = rand.nextBoolean();

            if (direction) {
                // Horizontal placement
                shipTop = rand.nextInt(boardSize + 1);
                shipLeft = rand.nextInt(boardSize + 1 - shipSize);
            } else {
                // Vertical placement
                shipTop = rand.nextInt(boardSize + 1);

                shipLeft = rand.nextInt(boardSize + 1 - shipSize);
            }

        } while (!isValidShipLocation(shipTop, shipLeft, shipSize, direction));

        if (direction) {
            for (int i = 0; i < shipSize; i++) {
                gameBoard[shipLeft + i][shipLeft] = 'S';
            }
        } else {
            for (int i = 0; i < shipSize; i++) {
                gameBoard[shipLeft][shipLeft + i] = 'S';
            }
        }
    }

    public char getCellState(int x, int y) {
        return gameBoard[x][y];
    }

    public void fireShot(int x, int y) {
        if (gameBoard[x][y] == 'S') {
            gameBoard[x][y] = 'H'; // H is for Hit
            score += 1;
        } else {
            gameBoard[x][y] = 'M'; // M is for Miss
        }
    }

    public int getScore() {
        return score;
    }

    public void setShipsRandom() {
        int[] shipsSize = { 2, 3, 3, 4, 5 };
        for (int i = 0; i < shipsSize.length; i++) {
            addShipRandom(shipsSize[i]);
        }
    }

    public void printBoard() {
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                System.out.print("+-");
            }
            System.out.println("+");
            System.out.print("|");
            for (int y = 0; y < boardSize; y++) {

                System.out.print(gameBoard[x][y] + "|");
            }
            System.out.println("");
        }
        for (int y = 0; y < boardSize; y++) {
            System.out.print("+-");
        }
        System.out.println("+");
    }
}