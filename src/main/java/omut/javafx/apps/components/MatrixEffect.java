package omut.javafx.apps.components;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Random;

public class MatrixEffect {
    private static final int FONT_SIZE = 20;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%^&*";

    private final Canvas canvas;
    private int columns;
    private int[] drops;
    private final Random random = new Random();
    private Timeline timeline;

    public MatrixEffect(Canvas canvas) {
        this.canvas = canvas;
        setupDrops(canvas.getWidth(), canvas.getHeight());

        ChangeListener<Number> sizeListener = (obs, o, n) -> {
            setupDrops(canvas.getWidth(), canvas.getHeight());
        };

        canvas.widthProperty().addListener(sizeListener);
        canvas.heightProperty().addListener(sizeListener);

        timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> draw()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void start() {
        timeline.play();
    }

    private void setupDrops(double width, double height) {
        columns = (int) (width / FONT_SIZE);
        drops = new int[columns];

        int maxDrop = Math.max((int) (height / FONT_SIZE), 1);
        for (int i = 0; i < columns; i++) {
            drops[i] = random.nextInt(maxDrop);
        }
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.setFill(Color.rgb(0, 0, 0, 0.1));
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.LIME);
        gc.setFont(javafx.scene.text.Font.font(FONT_SIZE));

        for (int i = 0; i < columns; i++) {
            String c = String.valueOf(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            gc.fillText(c, i * FONT_SIZE, drops[i] * FONT_SIZE);

            if (drops[i] * FONT_SIZE > height && random.nextDouble() > 0.975) {
                drops[i] = 0;
            } else {
                drops[i]++;
            }
        }
    }
}