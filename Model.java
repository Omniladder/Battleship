
/**
 * Battleship model class holds all data relating to current state of the game
 * as well as manages aspects of functionality such as server validation etc.
 */
import java.util.ArrayList;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.File;
//import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Random;

import java.awt.image.AffineTransformOp;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;

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

    private Image[][] shipPics = new Image[10][10];

    private Image[] carrierPics = new Image[5];
    private Image[] battleshipPics = new Image[4];
    private Image[] submarinePics = new Image[3];
    private Image[] cruiserPics = new Image[3];
    private Image[] destroyerPics = new Image[2];

    private ShipType[][] yourBoard = new ShipType[10][10]; // this is where your ships go
    private CellStatus[][] theirBoard = new CellStatus[10][10]; // this is where you track hits and misses
    private CellStatus[][] yourHits = new CellStatus[10][10];
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

        carrierPics[0] = getImage("images/Carrier1.png");
        carrierPics[1] = getImage("images/Carrier2.png");
        carrierPics[2] = getImage("images/Carrier3.png");
        carrierPics[3] = getImage("images/Carrier4.png");
        carrierPics[4] = getImage("images/Carrier5.png");

        battleshipPics[0] = getImage("images/Battleship1.png");
        battleshipPics[1] = getImage("images/Battleship2.png");
        battleshipPics[2] = getImage("images/Battleship3.png");
        battleshipPics[3] = getImage("images/Battleship4.png");

        submarinePics[0] = getImage("images/Submarine1.png");
        submarinePics[1] = getImage("images/Submarine2.png");
        submarinePics[2] = getImage("images/Submarine3.png");

        cruiserPics[0] = getImage("images/Cruiser1.png");
        cruiserPics[1] = getImage("images/Cruiser2.png");
        cruiserPics[2] = getImage("images/Cruiser3.png");

        destroyerPics[0] = getImage("images/Destroyer1.png");
        destroyerPics[1] = getImage("images/Destroyer2.png");

        setTheirBoard();
        emptyYourBoard();
        setYourBoard();

    }

    public boolean getCanMoveShips() {
        return canMoveShips;
    }

    public void setCanMoveShips(boolean canMoveShips) {
        this.canMoveShips = canMoveShips;
    }

    public int getScore() {
        return score;
    }

    public CellStatus getHitIndex(int x, int y) {
        return yourHits[x][y];
    }

    private Image getShipImage(ShipType shipType, int index) {
        switch (shipType) {
            case CARRIER:
                return carrierPics[index];
            case BATTLESHIP:
                return battleshipPics[index];
            case CRUISER:
                return cruiserPics[index];
            case SUBMARINE:
                return submarinePics[index];
            case DESTROYER:
                return destroyerPics[index];
            default:
                return null;
        }
    }

    private Image getImage(String dir) {
        BufferedImage pic;
        try {
            File file = new File(dir);
            pic = ImageIO.read(file);
            return pic;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    public Image getShipPic(int x, int y) {
        return shipPics[x][y];
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
                    shipPics[row][col + i] = ((BufferedImage) getShipImage(s, i));
                } else {
                    BattleShips.get(shipListIndex).isHorizontal = false;
                    BattleShips.get(shipListIndex).setShipPoint(row + i, col, i);
                    // BattleShips.get(shipListIndex).x_coords.set(i,row+i);
                    // BattleShips.get(shipListIndex).y_coords.set(i,col);
                    System.out.println("Placed " + s + " at (" + (row + i) + ", " + col + ")");
                    yourBoard[row + i][col] = s;
                    shipPics[row + i][col] = rotateImage((BufferedImage) getShipImage(s, i));
                }
            }

        }
    }

    private BufferedImage rotateImage(BufferedImage originalImage) {
        // Create an AffineTransform for the rotation
        // Rotation information
        double rotationRequired = Math.toRadians(-90);
        double locationX = originalImage.getWidth() / 2;
        double locationY = originalImage.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired,
                locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BILINEAR);

        return op.filter(originalImage, null);
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

            if (!(BattleShips.get(BattleShipsIndex).new_xy_coords.get(0)
                    .getX() < BattleShips.get(BattleShipsIndex).new_xy_coords.get(1).getX()))
                shipPics[(int) BattleShips.get(BattleShipsIndex).new_xy_coords.get(i)
                        .getX()][(int) BattleShips.get(BattleShipsIndex).new_xy_coords.get(i).getY()] = getShipImage(
                                translatedShipType, i);
            else {
                shipPics[(int) BattleShips.get(BattleShipsIndex).new_xy_coords.get(i)
                        .getX()][(int) BattleShips.get(BattleShipsIndex).new_xy_coords.get(i).getY()] = rotateImage(
                                (BufferedImage) getShipImage(translatedShipType, i));
            }
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
        System.out.println("Fired Shot Row: " + row + " Column: " + col);
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
                    if (result[0] >= 0) {
                        yourHits[firePosition[0]][firePosition[1]] = Model.CellStatus.HIT;
                    } else {
                        yourHits[firePosition[0]][firePosition[1]] = Model.CellStatus.MISS;
                    }
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
                yourHits[i][j] = CellStatus.DONTKNOW;
            }
        }
    }

    public void emptyYourBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                yourBoard[i][j] = ShipType.EMPTY;
                shipPics[i][j] = null;
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
        return yourBoard;
    }

    public CellStatus getTheirBoardIndex(int x, int y) {
        return theirBoard[x][y];
    }

    public CellStatus[][] getTheirBoardData() {
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
