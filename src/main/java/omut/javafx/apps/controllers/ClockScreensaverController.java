package omut.javafx.apps.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClockScreensaverController implements Initializable {

    @FXML
    private Canvas canvas;

    @FXML
    private Label statusLabel;

    @FXML
    private ResourceBundle resources;

    private AnimationTimer animationTimer;
    private boolean showDigital = true;
    private long lastSwitchTime = 0;
    private static final long SWITCH_INTERVAL = 10_000_000_000L; // 10 seconds in nanoseconds

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;

        canvas.setWidth(800);
        canvas.setHeight(600);
        canvas.setStyle("-fx-fill: black;");

        // Make canvas focusable to capture keyboard/mouse events
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::handleKeyPress);
        canvas.setOnMouseClicked(this::handleMouseClick);

        updateStatusLabel();
        startAnimation();
    }

    private void startAnimation() {
        lastSwitchTime = System.nanoTime();
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Check if it's time to switch
                if (now - lastSwitchTime > SWITCH_INTERVAL) {
                    showDigital = !showDigital;
                    lastSwitchTime = now;
                    updateStatusLabel();
                }

                render();
            }
        };
        animationTimer.start();
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case SPACE:
            case ENTER:
                showDigital = !showDigital;
                lastSwitchTime = System.nanoTime();
                updateStatusLabel();
                break;
            case ESCAPE:
                if (animationTimer != null) {
                    animationTimer.stop();
                }
                break;
            default:
                break;
        }
        event.consume();
    }

    private void handleMouseClick(MouseEvent event) {
        showDigital = !showDigital;
        lastSwitchTime = System.nanoTime();
        updateStatusLabel();
    }

    private void updateStatusLabel() {
        if (resources != null) {
            String mode = showDigital
                    ? resources.getString("clock.digital")
                    : resources.getString("clock.analog");
            statusLabel.setText(mode + " - " + resources.getString("clock.switch"));
        }
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Clear background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        LocalTime now = LocalTime.now();

        if (showDigital) {
            drawDigitalClock(gc, now);
        } else {
            drawAnalogClock(gc, now);
        }
    }

    private void drawDigitalClock(GraphicsContext gc, LocalTime time) {
        String timeStr = time.format(timeFormatter);
        ZoneId zoneId = ZoneId.systemDefault();
        String timezoneStr = resources.getString("clock.timezone") + ": " + zoneId.getId();

        gc.setFont(Font.font("Arial", 200));
        gc.setFill(Color.web("#00FF00"));

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;

        // Manually center text
        double textWidth = timeStr.length() * 100; // approximate width for Arial 200
        gc.fillText(timeStr, centerX - textWidth / 2, centerY + 60);

        // Draw timezone below the time
        gc.setFont(Font.font("Arial", 40));
        gc.setFill(Color.web("#00AA00"));
        double tzTextWidth = timezoneStr.length() * 20;
        gc.fillText(timezoneStr, centerX - tzTextWidth / 2, centerY + 120);

        // Draw milliseconds as a moving bar
        int millis = time.getNano() / 1_000_000;
        double barWidth = (millis / 1000.0) * canvas.getWidth();

        gc.setFill(Color.web("#00FF00"));
        gc.fillRect(0, canvas.getHeight() - 10, barWidth, 10);
    }

    private void drawAnalogClock(GraphicsContext gc, LocalTime time) {
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        double radius = 150;

        // Draw clock circle
        gc.setStroke(Color.web("#00FF00"));
        gc.setLineWidth(3);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Draw hour markers
        gc.setStroke(Color.web("#00FF00"));
        gc.setLineWidth(2);
        for (int i = 0; i < 12; i++) {
            double angle = Math.PI / 2 - (i * 2 * Math.PI / 12);
            double x1 = centerX + (radius - 15) * Math.cos(angle);
            double y1 = centerY - (radius - 15) * Math.sin(angle);
            double x2 = centerX + radius * Math.cos(angle);
            double y2 = centerY - radius * Math.sin(angle);
            gc.strokeLine(x1, y1, x2, y2);
        }

        // Draw hour numbers
        gc.setFont(Font.font("Arial", 20));
        gc.setFill(Color.web("#00FF00"));
        for (int i = 1; i <= 12; i++) {
            double angle = Math.PI / 2 - (i * 2 * Math.PI / 12);
            double x = centerX + (radius - 40) * Math.cos(angle);
            double y = centerY - (radius - 40) * Math.sin(angle);
            // Manually center single digit/number
            String numStr = String.valueOf(i);
            gc.fillText(numStr, x - 6, y + 6);
        }

        // Calculate hand angles
        double secondAngle = Math.PI / 2 - (time.getSecond() + time.getNano() / 1_000_000_000.0) * 2 * Math.PI / 60;
        double minuteAngle = Math.PI / 2 - (time.getMinute() + time.getSecond() / 60.0) * 2 * Math.PI / 60;
        double hourAngle = Math.PI / 2 - (time.getHour() % 12 + time.getMinute() / 60.0) * 2 * Math.PI / 12;

        // Draw hour hand
        gc.setStroke(Color.web("#00FF00"));
        gc.setLineWidth(6);
        drawHand(gc, centerX, centerY, hourAngle, radius * 0.5);

        // Draw minute hand
        gc.setStroke(Color.web("#00FF00"));
        gc.setLineWidth(4);
        drawHand(gc, centerX, centerY, minuteAngle, radius * 0.7);

        // Draw second hand
        gc.setStroke(Color.web("#FF0000"));
        gc.setLineWidth(2);
        drawHand(gc, centerX, centerY, secondAngle, radius * 0.8);

        // Draw center dot
        gc.setFill(Color.web("#00FF00"));
        gc.fillOval(centerX - 5, centerY - 5, 10, 10);

        // Draw digital time below
        String timeStr = time.format(timeFormatter);
        gc.setFont(Font.font("Arial", 30));
        gc.setFill(Color.web("#00FF00"));
        // Manually center text
        double textWidth = timeStr.length() * 15;
        gc.fillText(timeStr, centerX - textWidth / 2, centerY + radius + 50);

        // Draw timezone below the time
        ZoneId zoneId = ZoneId.systemDefault();
        String timezoneStr = resources.getString("clock.timezone") + ": " + zoneId.getId();
        gc.setFont(Font.font("Arial", 24));
        gc.setFill(Color.web("#00AA00"));
        double tzTextWidth = timezoneStr.length() * 12;
        gc.fillText(timezoneStr, centerX - tzTextWidth / 2, centerY + radius + 90);
    }

    private void drawHand(GraphicsContext gc, double centerX, double centerY, double angle, double length) {
        double x = centerX + length * Math.cos(angle);
        double y = centerY - length * Math.sin(angle);
        gc.strokeLine(centerX, centerY, x, y);
    }

}
