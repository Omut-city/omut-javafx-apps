package omut.javafx.apps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import omut.javafx.apps.service.LanguageService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ResourceBundle;

public class JavaFxApp extends Application {

    private ConfigurableApplicationContext context;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        context = new SpringApplicationBuilder(DemoApplication.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        LanguageService languageService = context.getBean(LanguageService.class);
        ResourceBundle bundle = languageService.getBundle();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
        loader.setControllerFactory(context::getBean);
        loader.setResources(bundle);

        stage.setScene(new Scene(loader.load()));
        stage.setTitle(bundle.getString("main.title"));
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
    }
}
