/**
 * Battleship model class holds all data relating to current state of the game
 * as well as manages aspects of functionality such as server validation etc.
 */
import java.util.Random;
public class Model 
{
    public enum CellStatus //theirBoard
    {
        EMPTY,
        SHIP,
        HIT,
        MISS,
        DONTKNOW
    }
    public enum ShipType { //yourBoard
        EMPTY,
        CARRIER,
        BATTLESHIP,
        CRUISER,
        SUBMARINE,
        DESTROYER
    }
    private ShipType[][] yourBoard = new ShipType[10][10]; //this is where your ships go
    private CellStatus[][] theirBoard = new CellStatus[10][10]; //this is where you track hits and misses
    private int CarrierLife = 5;
    private int BattleshipLife = 4;
    private int CruiserLife = 3;
    private int SubmarineLife = 3;
    private int DestroyerLife = 2;
    int score = 0;

    public void placeShip(int size, ShipType s, boolean isHorizontal)
    {
        Random rand = new Random();
        boolean canPlace = false;
        
        int row =0,col=0;

        while(!canPlace)
        {
            if(isHorizontal)
            {
                row = rand.nextInt(10); // Random row
                col = rand.nextInt(10 - size); // Ensure ship fits within bounds
            }
            else
            {
                row = rand.nextInt(10 - size); // Ensure ship fits within bounds
                col = rand.nextInt(10); // Random column
            }

            canPlace = true;
            for (int i = 0; i < size; i++) 
            {
                if (isHorizontal) 
                {
                    if (yourBoard[row][col + i] != ShipType.EMPTY) 
                    {
                        canPlace = false;
                        break;
                    }
                } 
                else 
                {
                    if (yourBoard[row + i][col] != ShipType.EMPTY) 
                    {
                        canPlace = false;
                        break;
                    }
                }
            }
        }
        if(canPlace)
        {
            for(int i = 0;i<size;i++)
            {
                if(isHorizontal)
                    yourBoard[row][col+i] = s;
                else
                    yourBoard[row+i][col] = s;
            }
            
        }
    }
    //this is if they hit you
    public int checkForHit(int row, int col) //returns -1 for miss, 0 for hit with no sink, and 1-5 for a hit+sink on a ship
    {
        int toReturn = -1;
        switch (yourBoard[row][col]) 
            {
                case CARRIER:
                    if(--CarrierLife == 0)
                        toReturn = 5;
                    break;
                case BATTLESHIP:
                    if(--BattleshipLife == 0)
                        toReturn = 4;
                    break;
                case CRUISER:
                    if(--CruiserLife == 0)
                        toReturn = 3;
                    break;
                case SUBMARINE:
                    if(--SubmarineLife == 0)
                        toReturn = 2;
                    break;
                case DESTROYER:
                    if(--DestroyerLife == 0)
                        toReturn = 1;
                    break;
                case EMPTY:
                    toReturn = -1;
                    break;
                default:
                    toReturn = 1;
            }
        return toReturn;  
    }

    public void printSinkMessage(int hitResult) { //pass in hit data from checkForHit to get correct message to display
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

    public void receiveHitOrMiss(int row, int col, int hitData)
    {
        if(hitData>0)
        {
            score++;
            theirBoard[row][col] = CellStatus.HIT;
        }
        else
        {
            theirBoard[row][col] = CellStatus.MISS;
        }

        printSinkMessage(hitData); // doesnt necessarily require sink, but prints hit, miss, sink stuff
    }

    public void setHit(int row, int col)
    {
        theirBoard[row][col] = CellStatus.HIT;
    }
    public void setMiss(int row, int col)
    {
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
    public void setYourBoard() 
    {
        placeShip(CarrierLife, ShipType.CARRIER, getRandomOrientation());
        placeShip(BattleshipLife, ShipType.BATTLESHIP, getRandomOrientation());
        placeShip(CruiserLife, ShipType.CRUISER, getRandomOrientation());
        placeShip(SubmarineLife, ShipType.SUBMARINE, getRandomOrientation());
        placeShip(DestroyerLife, ShipType.DESTROYER, getRandomOrientation());
    }

    public void setYourBoardTest()
    {
        DestroyerLife = 1;
        emptyYourBoard();
        yourBoard[0][0] = ShipType.DESTROYER;
    }
    
    public void sinkOneShipTest()
    {
        int hitIdentifier = checkForHit(0, 0);
        printSinkMessage(hitIdentifier);
    }

    public String getYourBoardData() {
        StringBuilder boardData = new StringBuilder();
    
        // Loop through each row
        for (int i = 0; i < 10; i++) {
            // Loop through each column in the row
            for (int j = 0; j < 10; j++) {
                boardData.append(yourBoard[i][j].toString().charAt(0));  // Append the character (e.g., 'S' or '.')
                if (j < 10 - 1) {
                    boardData.append(" ");  // Add space between columns (optional)
                }
            }
            boardData.append("\n");  // Add a newline at the end of each row
        }
    
        return boardData.toString();  // Return the board data as a string
    }

    public String getTheirBoardData() {
        StringBuilder boardData = new StringBuilder();
    
        // Loop through each row
        for (int i = 0; i < 10; i++) {
            // Loop through each column in the row
            for (int j = 0; j < 10; j++) {
                boardData.append(theirBoard[i][j].toString().charAt(0));  // Append the character (e.g., 'S' or '.')
                if (j < 10 - 1) {
                    boardData.append(" ");  // Add space between columns (optional)
                }
            }
            boardData.append("\n");  // Add a newline at the end of each row
        }
    
        return boardData.toString();  // Return the board data as a string
    }
    

    
    
    public static void main(String[] args) 
    {
        Model game = new Model(); // Create an instance of Model
        game.setYourBoardTest();  // Set up a board with a single shipcell at (0,0) - only one move to win to save time
        game.sinkOneShipTest();   // Hit the Destroyer and print the sink message
    }
    


}