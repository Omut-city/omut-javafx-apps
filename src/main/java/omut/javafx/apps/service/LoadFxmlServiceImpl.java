package omut.javafx.apps.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LoadFxmlServiceImpl implements LoadFxmlService {

    private final ConfigurableApplicationContext context;

    public LoadFxmlServiceImpl(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @Override
    public FXMLLoader load(String resource, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(LoadFxmlServiceImpl.class.getResource(resource));
        loader.setControllerFactory(context::getBean);
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(title);
        stage.show();
        return loader;
    }
}
