
public class Controller {
    View gameView;
    Model gameState;

    public Controller(Model initialState, View userInterface) {
        gameState = initialState;
        gameView = userInterface;
    }

    public void playGame() {

        // gameState.printBoard();
    }
}