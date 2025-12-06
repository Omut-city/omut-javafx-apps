package omut.javafx.apps.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
public class Base64EncoderDecoderController {

    @FXML
    private TextArea inputArea;
    @FXML private TextArea outputArea;

    @FXML
    private void encodeInput() {
        String text = inputArea.getText();
        if (text == null) text = "";
        String b64 = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
        outputArea.setText(b64);
    }

    @FXML
    private void decodeInput() {
        String b64 = inputArea.getText();
        if (b64 == null) b64 = "";
        try {
            byte[] decoded = Base64.getDecoder().decode(b64.trim());
            outputArea.setText(new String(decoded, StandardCharsets.UTF_8));
        } catch (IllegalArgumentException ex) {
            showError("Decoding error: the entered text is not valid Base64.");
        }
    }

    @FXML
    private void swapAreas() {
        String inp = inputArea.getText();
        inputArea.setText(outputArea.getText());
        outputArea.setText(inp);
    }

    @FXML
    private void copyOutput() {
        String t = outputArea.getText();
        if (t == null || t.isEmpty()) return;
        ClipboardContent c = new ClipboardContent();
        c.putString(t);
        Clipboard.getSystemClipboard().setContent(c);
    }

    @FXML
    private void clearAll() {
        inputArea.clear();
        outputArea.clear();
    }

    @FXML
    private void onExit() {
        Stage stage = (Stage) inputArea.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("About");
        a.setHeaderText("Base64 Encoder / Decoder");
        a.setContentText("""
                JavaFX application for encoding/decoding Base64 (UTF-8).
                """);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
