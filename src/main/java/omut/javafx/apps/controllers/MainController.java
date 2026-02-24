package omut.javafx.apps.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import omut.javafx.apps.service.LoadFxmlService;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {

    private final LoadFxmlService loadFxmlService;

    public MainController(
            LoadFxmlService loadFxmlService
    ) {
        this.loadFxmlService = loadFxmlService;
    }
    @FXML private Label label;

    @FXML
    public void onClick() {
        label.setText("Spring + JavaFX are works!");
    }

    @FXML
    public void onNew() {
        label.setText("New document!");
    }

    @FXML
    public void onOpen() {
        label.setText("Open document!");
    }

    @FXML
    public void onExit() {
        System.exit(0);
    }

    @FXML
    public void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Application JavaFX + Spring Boot");
        alert.showAndWait();
    }

    @FXML
    public void openTicTacToe() throws Exception {
        loadFxmlService.load("/views/ticTacToe.fxml", "Tic Tac Toe");
    }

    @FXML
    public void openTicTacToe2() throws Exception {
        loadFxmlService.load("/views/ticTacToe2.fxml", "Tic Tac Toe 2");
    }

    @FXML
    public void openBomberMan() throws Exception {
        loadFxmlService.load("/views/bomberman.fxml", "Bomber Man");
    }

    @FXML
    public void openMatrixEffect() throws Exception {
        loadFxmlService.load("/views/matrixEffect.fxml", "Matrix Effect");
    }

    @FXML
    public void openJavaFx3DFirstScene() throws Exception {
        loadFxmlService.load("/views/javaFx3DFirstScene.fxml", "JavaFX 3D First Scene");
    }

    @FXML
    public void openBase64EncoderDecoder() throws Exception {
        loadFxmlService.load("/views/base64EncoderDecoder.fxml", "Base64 Encoder/Decoder");
    }

    @FXML
    public void openJwtDecoder() throws Exception {
        loadFxmlService.load("/views/jwtDecoder.fxml", "Jwt Decoder");
    }

}
