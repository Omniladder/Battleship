
/**
 * Battleship model class holds all data relating to current state of the game
 * as well as manages aspects of functionality such as server validation etc.
 */
import java.util.ArrayList;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.awt.Point;
//import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Random;

public class Model {
    public enum CellStatus // theirBoard
    {
        HIT,
        MISS,
        DONTKNOW
    }

    public enum ShipType { // yourBoard
        EMPTY,
        CARRIER,
        BATTLESHIP,
        CRUISER,
        SUBMARINE,
        DESTROYER
    }

    private ShipType[][] yourBoard = new ShipType[10][10]; // this is where your ships go
    private CellStatus[][] theirBoard = new CellStatus[10][10]; // this is where you track hits and misses
    private int CarrierLife = 5;
    private int BattleshipLife = 4;
    private int CruiserLife = 3;
    private int SubmarineLife = 3;
    private int DestroyerLife = 2;
    private boolean canMoveShips = true;
    ArrayList<Ship> BattleShips = new ArrayList<>(); // this list of battleships contains ship classes
                                                     // these contain x and y coords for translating ship stuff

    int score = 0;

    ObjectOutputStream out;
    ObjectInputStream in;

    int boardSize;
    boolean playerMove;

    Model(int boardSize, int playerNumber, ObjectOutputStream out, ObjectInputStream in) {
        this.boardSize = boardSize;
        playerMove = (playerNumber == 1);
        score = 0;
        this.out = out;
        this.in = in;
        setTheirBoard();
        emptyYourBoard();
        setYourBoard();
    }

    public boolean getCanMoveShips() {
        return canMoveShips;
    }

    public int getScore() {
        return score;
    }

    public void placeShip(int size, ShipType s, boolean isHorizontal, int shipListIndex) {
        Random rand = new Random();
        boolean canPlace = false;

        int row = 0, col = 0;

        while (!canPlace) {
            if (isHorizontal) {
                row = rand.nextInt(10); // Random row
                col = rand.nextInt(10 - size); // Ensure ship fits within bounds
            } else {
                row = rand.nextInt(10 - size); // Ensure ship fits within bounds
                col = rand.nextInt(10); // Random column
            }
            System.out.println("Row: " + row + " Col: " + col);

            canPlace = true;
            for (int i = 0; i < size; i++) {
                if (isHorizontal) {
                    if (yourBoard[row][col + i] != ShipType.EMPTY) {
                        canPlace = false;
                        break;
                    }
                } else {
                    if (yourBoard[row + i][col] != ShipType.EMPTY) {
                        canPlace = false;
                        break;
                    }
                }
            }
        }
        if (canPlace) {
            for (int i = 0; i < size; i++) {
                if (isHorizontal) {
                    BattleShips.get(shipListIndex).isHorizontal = true;
                    // get correct ship --> access coords list --> get specific coord --> set it
                    BattleShips.get(shipListIndex).setShipPoint(row, col + i, i);
                    System.out.println("Placed " + s + " at (" + row + ", " + (col + i) + ")");
                    yourBoard[row][col + i] = s;
                } else {
                    BattleShips.get(shipListIndex).isHorizontal = false;
                    BattleShips.get(shipListIndex).setShipPoint(row + i, col, i);
                    // BattleShips.get(shipListIndex).x_coords.set(i,row+i);
                    // BattleShips.get(shipListIndex).y_coords.set(i,col);
                    System.out.println("Placed " + s + " at (" + (row + i) + ", " + col + ")");
                    yourBoard[row + i][col] = s;
                }
            }

        }
    }

    // either return false, or <-1,-1> to show bad placement
    // clicked section doesnt matter. all points translated the same
    // points might be doubles, not ints. if thats the case:
    // cast double --> int --> string
    public boolean moveShipFromAtoB(int xa, int ya, int xb, int yb) // index coordinates
    {
        // if game has started, dont allow ships to move
        if (!canMoveShips) {
            return false;
        }
        ShipType translatedShipType = yourBoard[xa][ya];
        int BattleShipsIndex = -1;
        // find the index in BattleShips (model arraylist) based on shipytype
        switch (translatedShipType) {
            case CARRIER:
                BattleShipsIndex = 0;
                break;
            case BATTLESHIP:
                BattleShipsIndex = 1;
                break;
            case CRUISER:
                BattleShipsIndex = 2;
                break;
            case SUBMARINE:
                BattleShipsIndex = 3;
                break;
            case DESTROYER:
                BattleShipsIndex = 4;
                break;
            case EMPTY:
                return false;
            default:
                System.out.println("Something aint right");
                break;
        }

        // difference in points. where you dragged from doesnt matter
        int dx = xb - xa;
        int dy = yb - ya;

        // check to see if points are valid

        for (int j = 0; j < BattleShips.get(BattleShipsIndex).xy_coords.size(); j++) {
            Point p = BattleShips.get(BattleShipsIndex).xy_coords.get(j);
            int new_x = (int) p.getX() + dx;
            int new_y = (int) p.getY() + dy;
            if ((yourBoard[new_x][new_y] != ShipType.EMPTY && yourBoard[new_x][new_y] != translatedShipType)) // cannot
                                                                                                              // translate
                                                                                                              // point.
                                                                                                              // space
                                                                                                              // already
                                                                                                              // occupied
            {
                // System.out.println("SHIP CANT MOVE THERE. REMOVE THIS MESSAGE WHEN DONE, or
                // make it go somewhere else");
                return false;
            }
            System.out.println(new_x + " " + new_y);
            BattleShips.get(BattleShipsIndex).setNewCoords(j, new Point(new_x, new_y));
            // check if new points are valid. no index out of bounds, and no ship already
            // there
            if (new_x > yourBoard.length || new_y > yourBoard.length || new_x < 0 || new_y < 0
                    || (yourBoard[new_x][new_y] != ShipType.EMPTY && yourBoard[new_x][new_y] != translatedShipType)) // cannot
                                                                                                                     // translate
                                                                                                                     // point.
                                                                                                                     // space
                                                                                                                     // already
                                                                                                                     // occupied
            {
                // clear transferred points
                BattleShips.get(BattleShipsIndex).new_xy_coords.clear();
                System.out.println("SHIP CANT MOVE THERE. REMOVE THIS MESSAGE WHEN DONE, or make it go somewhere else");
                return false;
            }
        }

        for (int i = 0; i < BattleShips.get(BattleShipsIndex).xy_coords.size(); i++) {
            // set old points to empty
            yourBoard[(int) BattleShips.get(BattleShipsIndex).xy_coords.get(i)
                    .getX()][(int) BattleShips.get(BattleShipsIndex).xy_coords.get(i).getY()] = ShipType.EMPTY;
            // SET THE MODEL TO NEW POINT
        }
        // if we're all good with the new points, then we can swap
        for (int i = 0; i < BattleShips.get(BattleShipsIndex).xy_coords.size(); i++) {
            // SET THE MODEL TO NEW POINT
            yourBoard[(int) BattleShips.get(BattleShipsIndex).new_xy_coords.get(i)
                    .getX()][(int) BattleShips.get(BattleShipsIndex).new_xy_coords.get(i).getY()] = translatedShipType;
            // set the old point with the new point
            BattleShips.get(BattleShipsIndex).xy_coords.set(i, BattleShips.get(BattleShipsIndex).new_xy_coords.get(i));
        }
        BattleShips.get(BattleShipsIndex).new_xy_coords.clear();
        return true;

        // how should we move ships?
        // ships down randomly --> click on shipsquare --> records position --> when
        // released, check if valid
        // if valid, iterate through points list and translate coordinates, if not
        // return to square
        // maybe make image (square,arrow,etc...) follow cursor to show user is doing
        // something
    }

    public boolean isPlayersTurn() {
        return playerMove;
    }

    public void shoot(int row, int col) {
        int[] firePosition = { row, col };
        try {
            out.writeObject(firePosition);
            out.flush();
            System.out.println("Before Read IN");
            int[] result = (int[]) in.readObject();
            System.out.println("After Read IN");
            printSinkMessage(result[0]);
            if (result[0] >= 0) {
                theirBoard[row][col] = Model.CellStatus.HIT;
            } else {
                theirBoard[row][col] = Model.CellStatus.MISS;
            }
            playerMove = !playerMove;
            System.out.println("Player Move: " + playerMove);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e);
        }
    }

    public void waitForOpponent() {
        new Thread(() -> {
            System.out.println("Player Move: " + playerMove);
            while (!playerMove) {
                try {
                    int[] firePosition = (int[]) in.readObject();
                    for (int i = 0; i < firePosition.length; i++)
                        System.out.println("Posistion " + i + " , " + firePosition[i]);
                    System.out.println(firePosition[0] + " , " + firePosition[1]); // ::ERROR:: Reading in enum
                    int[] result = { checkForHit(firePosition[0], firePosition[1]) };
                    out.writeObject(result);
                    out.flush();
                    playerMove = !playerMove;
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error: " + e);
                }
            }
        }).start();
    }

    // this is if they hit you
    public int checkForHit(int row, int col) // returns -1 for miss, 0 for hit with no sink, and 1-5 for a hit+sink on
                                             // a ship
    {
        // System.out.println("Check Hit:" + yourBoard[row][col]);
        int toReturn = 0;
        switch (yourBoard[row][col]) {
            case CARRIER:
                if (--CarrierLife == 0)
                    toReturn = 5;
                break;
            case BATTLESHIP:
                if (--BattleshipLife == 0)
                    toReturn = 4;
                break;
            case CRUISER:
                if (--CruiserLife == 0)
                    toReturn = 3;
                break;
            case SUBMARINE:
                if (--SubmarineLife == 0)
                    toReturn = 2;
                break;
            case DESTROYER:
                if (--DestroyerLife == 0)
                    toReturn = 1;
                break;
            case EMPTY:
                toReturn = -1;
                break;
            default:
                toReturn = 0;
        }
        printSinkMessage(toReturn);
        return toReturn;
    }

    private void printSinkMessage(int hitResult) { // pass in hit data from checkForHit to get correct message to
                                                   // display
        switch (hitResult) {
            case -1:
                System.out.println("Miss!");
                break;
            case 0:
                System.out.println("Hit!");
                break;
            case 1:
                System.out.println("You sunk the Destroyer!");
                break;
            case 2:
                System.out.println("You sunk the Submarine!");
                break;
            case 3:
                System.out.println("You sunk the Cruiser!");
                break;
            case 4:
                System.out.println("You sunk the Battleship!");
                break;
            case 5:
                System.out.println("You sunk the Carrier!");
                break;
            default:
                System.out.println("Invalid hit result.");
                break;
        }
    }

    public void processScoreData(int row, int col, int hitData) // when you find out you got a hit, this is how you
                                                                // process that and change your board.
    { // there should be a subsequent call in controller to send boardState to view
        System.out.println(hitData);
        if (hitData >= 0) {
            score++;
            theirBoard[row][col] = CellStatus.HIT;
        } else {
            theirBoard[row][col] = CellStatus.MISS;
        }

        // printSinkMessage(hitData); // doesnt necessarily require sink, but prints
        // hit, miss, sink stuff
    }

    public void setHit(int row, int col) {
        theirBoard[row][col] = CellStatus.HIT;
    }

    public void setMiss(int row, int col) {
        theirBoard[row][col] = CellStatus.MISS;
    }

    public void setTheirBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                theirBoard[i][j] = CellStatus.DONTKNOW;
            }
        }
    }

    public void emptyYourBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                yourBoard[i][j] = ShipType.EMPTY;
            }
        }
    }

    private boolean getRandomOrientation() {
        Random rand = new Random();
        return rand.nextBoolean(); // Returns true for horizontal, false for vertical
    }

    public void setYourBoard() {

        // Create ships to avoid IOB exception
        Ship Carrier = new Ship();
        for (int i = 0; i < 5; i++) {
            Carrier.xy_coords.add(new Point(777, 777));
            Carrier.new_xy_coords.add(new Point(777, 777));
        }
        BattleShips.add(Carrier);

        Ship Battleship = new Ship();
        for (int i = 0; i < 4; i++) {
            Battleship.xy_coords.add(new Point(777, 777));
            Battleship.new_xy_coords.add(new Point(777, 777));
        }
        BattleShips.add(Battleship);

        Ship Cruiser = new Ship();
        for (int i = 0; i < 3; i++) {
            Cruiser.xy_coords.add(new Point(777, 777));
            Cruiser.new_xy_coords.add(new Point(777, 777));
        }
        BattleShips.add(Cruiser);

        Ship Submarine = new Ship();
        for (int i = 0; i < 3; i++) {
            Submarine.xy_coords.add(new Point(777, 777));
            Submarine.new_xy_coords.add(new Point(777, 777));
        }
        BattleShips.add(Submarine);

        Ship Destroyer = new Ship();
        for (int i = 0; i < 2; i++) {
            Destroyer.xy_coords.add(new Point(777, 777));
            Destroyer.new_xy_coords.add(new Point(777, 777));
        }
        BattleShips.add(Destroyer);

        BattleShips.add(new Ship());
        placeShip(CarrierLife, ShipType.CARRIER, getRandomOrientation(), 0);
        placeShip(BattleshipLife, ShipType.BATTLESHIP, getRandomOrientation(), 1);
        placeShip(CruiserLife, ShipType.CRUISER, getRandomOrientation(), 2);
        placeShip(SubmarineLife, ShipType.SUBMARINE, getRandomOrientation(), 3);
        placeShip(DestroyerLife, ShipType.DESTROYER, getRandomOrientation(), 4);
    }

    /*
     * Functions relating to getting the current state of the board relative to the
     * player and the opponent
     */
    public ShipType getYourBoardIndex(int x, int y) {
        return yourBoard[x][y];
    }

    public ShipType[][] getYourBoardData() {
        /*
         * StringBuilder boardData = new StringBuilder();
         * 
         * // Loop through each row
         * for (int i = 0; i < 10; i++) {
         * // Loop through each column in the row
         * for (int j = 0; j < 10; j++) {
         * boardData.append(yourBoard[i][j].toString().charAt(0)); // Append the
         * character (e.g., 'S' or '.')
         * if (j < 10 - 1) {
         * boardData.append(" "); // Add space between columns (optional)
         * }
         * }
         * boardData.append("\n"); // Add a newline at the end of each row
         * }
         * 
         * return boardData.toString(); // Return the board data as a string
         */
        return yourBoard;
    }

    public CellStatus getTheirBoardIndex(int x, int y) {
        return theirBoard[x][y];
    }

    public CellStatus[][] getTheirBoardData() {
        /*
         * StringBuilder boardData = new StringBuilder();
         * 
         * // Loop through each row
         * for (int i = 0; i < 10; i++) {
         * // Loop through each column in the row
         * for (int j = 0; j < 10; j++) {
         * boardData.append(theirBoard[i][j].toString().charAt(0)); // Append the
         * character (e.g., 'S' or '.')
         * if (j < 10 - 1) {
         * boardData.append(" "); // Add space between columns (optional)
         * }
         * }
         * boardData.append("\n"); // Add a newline at the end of each row
         * }
         * 
         * return boardData.toString(); // Return the board data as a string
         */
        return theirBoard;
    }

    /*
     * Debugging code used for outputting current state of the board to Console
     */
    public void printBoard() {
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                System.out.print("+-+");
            }
            System.out.println("");
            for (int y = 0; y < boardSize; y++) {
                if (yourBoard[y][x] != ShipType.EMPTY) {
                    System.out.print("|X|"); // Marks the location of a ship
                } else {
                    System.out.print("| |"); // Marker for water
                }
            }
            System.out.println("");
        }
        for (int y = 0; y < boardSize; y++) {
            System.out.print("+-+");
        }
        System.out.println("");
    }

    public void printTestMessage() {
        System.out.println("Test button clicked!");
    }

    public void handleTestButtonClick() {
        System.out.println("Test button clicked in Model!");
        // Add any additional logic you want to execute when the button is clicked
    }
}

class Ship {
    ArrayList<java.awt.Point> xy_coords = new ArrayList<>();
    ArrayList<java.awt.Point> new_xy_coords = new ArrayList<>();
    Boolean isHorizontal;
    Model.ShipType type;

    void setNewCoords(int index, Point p) {
        if (index >= new_xy_coords.size()) {
            new_xy_coords.add(p);
        } else {
            new_xy_coords.set(index, p);
        }
    }

    void setShipPoint(int x, int y, int index) {
        xy_coords.set(index, (new Point(x, y)));
    }
}
