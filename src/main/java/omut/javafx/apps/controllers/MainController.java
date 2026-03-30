package omut.javafx.apps.controllers;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
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

    @FXML
    private ResourceBundle resources;

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
    private Menu fileMenu;

    @FXML
    private MenuItem newItem;

    @FXML
    private MenuItem openItem;

    @FXML
    private MenuItem exitItem;

    @FXML
    private Menu gamesMenu;

    @FXML
    private MenuItem tictactoeItem;

    @FXML
    private MenuItem tictactoe2Item;

    @FXML
    private MenuItem bombermanItem;

    @FXML
    private MenuItem checkersItem;

    @FXML
    private MenuItem minesweeperItem;

    @FXML
    private Menu screensaversMenu;

    @FXML
    private MenuItem matrixItem;

    @FXML
    private Menu threeDMenu;

    @FXML
    private MenuItem threeDFirstItem;

    @FXML
    private Menu utilsMenu;

    @FXML
    private MenuItem base64Item;

    @FXML
    private MenuItem jwtItem;

    @FXML
    private Menu settingsMenu;

    @FXML
    private Menu languageMenu;

    @FXML
    private Menu aboutMenu;

    @FXML
    private MenuItem aboutItem;

    @FXML
    private Button pushMeButton;

    @FXML
    private Button startTaskButton;

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
            updateTexts();
        });

        updateLanguageIndicator(languageService.getCurrentLanguage());
    }

    private void updateTexts() {
        resources = languageService.getBundle();

        // Main window title
        Stage stage = (Stage) label.getScene().getWindow();
        stage.setTitle(resources.getString("main.title"));

        // Menus
        fileMenu.setText(resources.getString("menu.file"));
        newItem.setText(resources.getString("menu.file.new"));
        openItem.setText(resources.getString("menu.file.open"));
        exitItem.setText(resources.getString("menu.file.exit"));

        gamesMenu.setText(resources.getString("menu.games"));
        tictactoeItem.setText(resources.getString("menu.games.tictactoe"));
        tictactoe2Item.setText(resources.getString("menu.games.tictactoe2"));
        bombermanItem.setText(resources.getString("menu.games.bomberman"));
        checkersItem.setText(resources.getString("menu.games.checkers"));
        minesweeperItem.setText(resources.getString("menu.games.minesweeper"));

        screensaversMenu.setText(resources.getString("menu.screensavers"));
        matrixItem.setText(resources.getString("menu.screensavers.matrix"));

        threeDMenu.setText(resources.getString("menu.3d"));
        threeDFirstItem.setText(resources.getString("menu.3d.first"));

        utilsMenu.setText(resources.getString("menu.utils"));
        base64Item.setText(resources.getString("menu.utils.base64"));
        jwtItem.setText(resources.getString("menu.utils.jwt"));

        settingsMenu.setText(resources.getString("menu.settings"));
        languageMenu.setText(resources.getString("menu.settings.language"));

        aboutMenu.setText(resources.getString("menu.about"));
        aboutItem.setText(resources.getString("menu.about.about"));

        // Center
        label.setText(resources.getString("label.hello"));
        pushMeButton.setText(resources.getString("button.pushme"));
        startTaskButton.setText(resources.getString("button.starttask"));

        // Status bar
        statusLabel.setText(resources.getString("status.ready"));
        rightStatusLabel.setText(resources.getString("status.idle"));
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
        label.setText(resources.getString("label.hello"));
        statusLabel.setText(resources.getString("status.clicked"));
    }

    @FXML
    public void onNew() {
        label.setText(resources.getString("status.new"));
    }

    @FXML
    public void onOpen() {
        label.setText(resources.getString("status.open"));
    }

    @FXML
    public void onExit() {
        System.exit(0);
    }

    @FXML
    public void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(resources.getString("about.title"));
        alert.setHeaderText(null);
        alert.setContentText(resources.getString("about.content"));
        alert.showAndWait();
    }

    @FXML
    public void openTicTacToe() throws Exception {
        loadFxmlService.load("/views/ticTacToe.fxml", resources.getString("menu.games.tictactoe"), resources);
    }

    @FXML
    public void openTicTacToe2() throws Exception {
        loadFxmlService.load("/views/ticTacToe2.fxml", resources.getString("menu.games.tictactoe2"), resources);
    }

    @FXML
    public void openBomberMan() throws Exception {
        loadFxmlService.load("/views/bomberman.fxml", resources.getString("menu.games.bomberman"), resources);
    }

    @FXML
    public void openCheckers() throws Exception {
        loadFxmlService.load("/views/checkers.fxml", resources.getString("menu.games.checkers"), resources);
    }

    @FXML
    public void openMinesweeper() throws Exception {
        loadFxmlService.load("/views/minesweeper.fxml", resources.getString("menu.games.minesweeper"), resources);
    }

    @FXML
    public void openMatrixEffect() throws Exception {
        loadFxmlService.load("/views/matrixEffect.fxml", resources.getString("menu.screensavers.matrix"), resources);
    }

    @FXML
    public void openJavaFx3DFirstScene() throws Exception {
        loadFxmlService.load("/views/javaFx3DFirstScene.fxml", resources.getString("menu.3d.first"), resources);
    }

    @FXML
    public void openBase64EncoderDecoder() throws Exception {
        loadFxmlService.load("/views/base64EncoderDecoder.fxml", resources.getString("menu.utils.base64"), resources);
    }

    @FXML
    public void openJwtDecoder() throws Exception {
        loadFxmlService.load("/views/jwtDecoder.fxml", resources.getString("menu.utils.jwt"), resources);
    }

    @FXML
    private void startLongTask() {

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                updateMessage(resources.getString("status.processing"));
                updateProgress(0, 100);

                for (int i = 1; i <= 100; i++) {
                    Thread.sleep(30);
                    updateProgress(i, 100);
                }

                updateMessage(resources.getString("status.completed"));
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());

        progressBar.setVisible(true);
        rightStatusLabel.setText(resources.getString("status.working"));

        task.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            progressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();
            rightStatusLabel.setText(resources.getString("status.done"));
        });

        new Thread(task).start();
    }

}
