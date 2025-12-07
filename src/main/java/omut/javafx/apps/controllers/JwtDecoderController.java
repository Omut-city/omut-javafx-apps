package omut.javafx.apps.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import java.util.Base64;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

/**
 * Example input:
 * {
 *   "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTYifQ.dummy_signature",
 *   "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0eXBlIjoicmVmcmVzaCJ9.dummy_signature"
 * }
 */
@Controller
public class JwtDecoderController {

    @FXML
    private TextArea inputArea;

    @FXML
    private TextArea outputArea;

    @FXML
    public void onDecodeAccess() {
        decodeToken("access_token");
    }

    @FXML
    public void onDecodeRefresh() {
        decodeToken("refresh_token");
    }

    private void decodeToken(String tokenKey) {
        try {
            JSONObject inputJson = new JSONObject(inputArea.getText());
            String token = inputJson.getString(tokenKey);
            String payload = decodeJwtPayload(token);
            JSONObject payloadJson = new JSONObject(payload);
            outputArea.setText(payloadJson.toString(4));
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private String decodeJwtPayload(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length < 2) throw new Exception("Wrong JWT format");

        String payload = parts[1];

        int padding = 4 - (payload.length() % 4);
        if (padding < 4) {
            payload += "=".repeat(padding);
        }

        byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
        return new String(decodedBytes);
    }
}
