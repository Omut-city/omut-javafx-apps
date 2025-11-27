
package com.example.demo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {

    @FXML private Label label;

    @FXML
    public void onClick() {
        label.setText("Spring + JavaFX are works!");
    }
}
