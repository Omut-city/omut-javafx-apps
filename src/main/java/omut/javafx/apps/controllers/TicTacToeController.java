package omut.javafx.apps.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Controller;

@Controller
public class TicTacToeController {

    private boolean xTurn = true;

    @FXML private Button b00, b01, b02, b10, b11, b12, b20, b21, b22;

    @FXML
    public void handleButton(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        if (!btn.getText().isEmpty()) return;

        btn.setText(xTurn ? "X" : "O");
        xTurn = !xTurn;
    }
}
