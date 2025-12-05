package omut.javafx.apps.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Controller;

@Controller
public class TicTacToeController {

    private boolean xTurn = true;

    @FXML
    public void handleButton(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        if (!btn.getText().isEmpty()) return;

        btn.setText(xTurn ? "X" : "O");
        xTurn = !xTurn;
    }
}
