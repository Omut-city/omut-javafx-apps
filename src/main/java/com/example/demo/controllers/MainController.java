
package com.example.demo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
}
