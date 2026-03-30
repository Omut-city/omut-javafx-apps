package omut.javafx.apps.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@Component
public class LoadFxmlServiceImpl implements LoadFxmlService {

    private final ConfigurableApplicationContext context;

    public LoadFxmlServiceImpl(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @Override
    public FXMLLoader load(String resource, String title) throws Exception {
        return load(resource, title, null);
    }

    @Override
    public FXMLLoader load(String resource, String title, ResourceBundle bundle) throws Exception {
        FXMLLoader loader = new FXMLLoader(LoadFxmlServiceImpl.class.getResource(resource));
        loader.setControllerFactory(context::getBean);
        if (bundle != null) {
            loader.setResources(bundle);
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(title);
        stage.show();
        return loader;
    }
}
