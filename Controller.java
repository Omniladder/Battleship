
public class Controller {
    View gameView;
    Model gameState;

    Controller(Model initialState, View userInterface) {
        gameState = initialState;
        gameView = userInterface;
    }

    public void playGame() {

    }
}