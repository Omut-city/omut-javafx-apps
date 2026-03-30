package omut.javafx.apps.controllers;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import omut.javafx.apps.model.Language;
import omut.javafx.apps.service.LanguageService;
import omut.javafx.apps.service.LoadFxmlService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MainController {

    private final LoadFxmlService loadFxmlService;
    private final LanguageService languageService;

    public MainController(
            LoadFxmlService loadFxmlService,
            LanguageService languageService
    ) {
        this.loadFxmlService = loadFxmlService;
        this.languageService = languageService;
    }
    @FXML private Label label;

    @FXML
    private Label statusLabel;

    @FXML
    private Label rightStatusLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label languageLabel;

    @FXML
    private Menu languageMenu;

    @FXML
    private ProgressBar progressBar;

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    public void initialize() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> updateTime()),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        setupLanguageMenu();
        setupLanguageIndicator();
    }

    private void setupLanguageMenu() {
        for (Language lang : Language.values()) {
            MenuItem item = new MenuItem(lang.getDisplayName());
            item.setOnAction(e -> languageService.setCurrentLanguage(lang));
            languageMenu.getItems().add(item);
        }
    }

    private void setupLanguageIndicator() {
        ContextMenu contextMenu = new ContextMenu();
        for (Language lang : Language.values()) {
            MenuItem item = new MenuItem(lang.getDisplayName());
            item.setOnAction(e -> languageService.setCurrentLanguage(lang));
            contextMenu.getItems().add(item);
        }

        languageLabel.setOnMouseClicked(event -> {
            contextMenu.show(languageLabel, event.getScreenX(), event.getScreenY());
        });

        languageService.currentLanguageProperty().addListener((obs, oldLang, newLang) -> {
            updateLanguageIndicator(newLang);
        });

        updateLanguageIndicator(languageService.getCurrentLanguage());
    }

    private void updateLanguageIndicator(Language lang) {
        languageLabel.setText("🌐 " + lang.getLocale().getLanguage().toUpperCase());
        Tooltip.install(languageLabel, new Tooltip(lang.getDisplayName()));
    }

    private void updateTime() {
        timeLabel.setText(ZonedDateTime.now(ZoneId.systemDefault()).format(timeFormatter));
    }

    @FXML
    public void onClick() {
        label.setText("Spring + JavaFX are works!");
        statusLabel.setText("Button clicked!");
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
        loadFxmlService.load("/views/ticTacToe.fxml", "Tic Tac Toe");
    }

    @FXML
    public void openTicTacToe2() throws Exception {
        loadFxmlService.load("/views/ticTacToe2.fxml", "Tic Tac Toe 2");
    }

    @FXML
    public void openBomberMan() throws Exception {
        loadFxmlService.load("/views/bomberman.fxml", "Bomber Man");
    }

    @FXML
    public void openCheckers() throws Exception {
        loadFxmlService.load("/views/checkers.fxml", "Checkers");
    }

    @FXML
    public void openMinesweeper() throws Exception {
        loadFxmlService.load("/views/minesweeper.fxml", "Minesweeper");
    }

    @FXML
    public void openMatrixEffect() throws Exception {
        loadFxmlService.load("/views/matrixEffect.fxml", "Matrix Effect");
    }

    @FXML
    public void openJavaFx3DFirstScene() throws Exception {
        loadFxmlService.load("/views/javaFx3DFirstScene.fxml", "JavaFX 3D First Scene");
    }

    @FXML
    public void openBase64EncoderDecoder() throws Exception {
        loadFxmlService.load("/views/base64EncoderDecoder.fxml", "Base64 Encoder/Decoder");
    }

    @FXML
    public void openJwtDecoder() throws Exception {
        loadFxmlService.load("/views/jwtDecoder.fxml", "Jwt Decoder");
    }

    @FXML
    private void startLongTask() {

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                updateMessage("Processing...");
                updateProgress(0, 100);

                for (int i = 1; i <= 100; i++) {
                    Thread.sleep(30);
                    updateProgress(i, 100);
                }

                updateMessage("Completed!");
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());

        progressBar.setVisible(true);
        rightStatusLabel.setText("Working...");

        task.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            progressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();
            rightStatusLabel.setText("Done");
        });

        new Thread(task).start();
    }

}
