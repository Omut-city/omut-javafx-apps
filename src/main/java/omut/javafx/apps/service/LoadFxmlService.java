package omut.javafx.apps.service;

import javafx.fxml.FXMLLoader;
import java.util.ResourceBundle;

public interface LoadFxmlService {
    FXMLLoader load(String resource, String title) throws Exception;
    FXMLLoader load(String resource, String title, ResourceBundle bundle) throws Exception;
}
