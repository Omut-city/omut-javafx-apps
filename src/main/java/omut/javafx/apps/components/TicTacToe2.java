package omut.javafx.apps.components;

import javafx.scene.control.Button;
import javafx.scene.text.Font;

public class TicTacToe2 {

    private static final Font FONT = Font.font(32);

    public static Button createButton() {
        Button btn = new Button(" ");
        btn.setMinSize(100, 100);
        btn.setFont(FONT);
        btn.setFocusTraversable(false);
        return btn;
    }

    public static void styleWin(Button a, Button b, Button c) {
        String style = "-fx-font-weight: bold; -fx-border-color: green; -fx-border-width: 2px;";
        a.setStyle(style);
        b.setStyle(style);
        c.setStyle(style);

        a.setDisable(true);
        b.setDisable(true);
        c.setDisable(true);
    }

}
