package omut.javafx.apps.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {

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
        load("/views/ticTacToe.fxml", "Tic Tac Toe");
    }

    @FXML
    public void openMatrixEffect() throws Exception {
        load("/views/matrixEffect.fxml", "Matrix Effect");
    }

    private void load(String resource, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(title);
        stage.show();
    }
}
