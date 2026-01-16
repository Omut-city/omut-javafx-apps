package omut.javafx.apps.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
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
