import java.lang.ModuleLayer.Controller;

/**
 * Main Method for Battleship simply creates variables then runs game
 */
public class run {
    public static void main(String[] args) {
        Model gameState = new Model();
        View gameInterface = new View(gameState);
        Controller gameController = new Controller(gameState, gameInterface);
        gameController.playGame();
    }
}