import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller {
    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    class TestButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            model.handleTestButtonClick();
        }
    }

    public void playGame() {
        // gameState.printBoard();
    }
}