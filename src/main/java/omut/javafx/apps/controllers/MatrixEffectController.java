package omut.javafx.apps.controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import omut.javafx.apps.components.MatrixEffect;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MatrixEffectController {

    @FXML private Canvas canvas;
    @FXML private AnchorPane rootPane;

    private MatrixEffect effect;

    @FXML
    public void initialize() {

        canvas.widthProperty().bind(rootPane.widthProperty());
        canvas.heightProperty().bind(rootPane.heightProperty());

        effect = new MatrixEffect(canvas);
        effect.start();
    }
}
