package omut.javafx.apps.service;

import javafx.fxml.FXMLLoader;

public interface LoadFxmlService {
    FXMLLoader load(String resource, String title) throws Exception;
}
