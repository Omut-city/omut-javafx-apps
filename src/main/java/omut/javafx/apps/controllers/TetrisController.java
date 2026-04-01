package omut.javafx.apps.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TetrisController implements Initializable {

    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 20;
    private static final int CELL_SIZE = 30;
    private static final long DROP_DELAY = 500_000_000; // 500ms in nanoseconds

    @FXML
    private Canvas canvas;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label levelLabel;

    @FXML
    private Label linesLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Label controlsLabel;

    @FXML
    private Label leftLabel;

    @FXML
    private Label rightLabel;

    @FXML
    private Label downLabel;

    @FXML
    private Label dropLabel;

    @FXML
    private Label rotateLabel;

    @FXML
    private ResourceBundle resources;

    private int[][] grid;
    private Tetromino currentTetromino;
    private AnimationTimer gameTimer;
    private long lastDropTime;
    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;
    private boolean gameRunning = false;
    private boolean gamePaused = false;

    private static final Color[] TETROMINO_COLORS = {
            Color.web("#FF0000"), // Red
            Color.web("#00FF00"), // Green
            Color.web("#0000FF"), // Blue
            Color.web("#FFFF00"), // Yellow
            Color.web("#FF00FF"), // Magenta
            Color.web("#00FFFF"), // Cyan
            Color.web("#FFA500"), // Orange
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        grid = new int[GRID_HEIGHT][GRID_WIDTH];
        canvas.setWidth(GRID_WIDTH * CELL_SIZE);
        canvas.setHeight(GRID_HEIGHT * CELL_SIZE);
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::handleKeyPress);

        updateUI();
        updateButtonTexts();
    }

    @FXML
    private void startGame() {
        if (!gameRunning) {
            gameRunning = true;
            gamePaused = false;
            grid = new int[GRID_HEIGHT][GRID_WIDTH];
            score = 0;
            linesCleared = 0;
            level = 1;
            currentTetromino = new Tetromino();
            updateUI();
            startGameLoop();
            startButton.setText(resources.getString("tetris.newgame"));
            pauseButton.setDisable(false);
            canvas.requestFocus();
        }
    }

    @FXML
    private void togglePause() {
        if (gameRunning) {
            gamePaused = !gamePaused;
            pauseButton.setText(gamePaused ? resources.getString("tetris.resume") : resources.getString("tetris.pause"));
            canvas.requestFocus();
        }
    }

    private void startGameLoop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        lastDropTime = System.nanoTime();
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameRunning && !gamePaused) {
                    if (now - lastDropTime > DROP_DELAY - (level - 1) * 20_000_000) {
                        dropTetromino();
                        lastDropTime = now;
                    }
                    render();
                }
            }
        };
        gameTimer.start();
    }

    private void dropTetromino() {
        if (currentTetromino == null) {
            currentTetromino = new Tetromino();
        }

        if (!canMove(currentTetromino.x, currentTetromino.y + 1, currentTetromino.shape)) {
            placeTetromino();
            clearLines();
            currentTetromino = new Tetromino();

            if (!canMove(currentTetromino.x, currentTetromino.y, currentTetromino.shape)) {
                gameOver();
            }
        } else {
            currentTetromino.y++;
        }
    }

    private void placeTetromino() {
        int[][] shape = currentTetromino.shape;
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int gridY = currentTetromino.y + row;
                    int gridX = currentTetromino.x + col;
                    if (gridY >= 0 && gridY < GRID_HEIGHT && gridX >= 0 && gridX < GRID_WIDTH) {
                        grid[gridY][gridX] = currentTetromino.type;
                    }
                }
            }
        }
    }

    private void clearLines() {
        int linesThisRound = 0;
        for (int row = GRID_HEIGHT - 1; row >= 0; row--) {
            if (isLineFull(row)) {
                removeLine(row);
                linesThisRound++;
            }
        }

        if (linesThisRound > 0) {
            linesCleared += linesThisRound;
            score += linesThisRound * linesThisRound * 100;
            level = 1 + linesCleared / 10;
            updateUI();
        }
    }

    private boolean isLineFull(int row) {
        for (int col = 0; col < GRID_WIDTH; col++) {
            if (grid[row][col] == 0) {
                return false;
            }
        }
        return true;
    }

    private void removeLine(int row) {
        for (int r = row; r > 0; r--) {
            grid[r] = grid[r - 1].clone();
        }
        grid[0] = new int[GRID_WIDTH];
    }

    private boolean canMove(int x, int y, int[][] shape) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col;
                    int newY = y + row;

                    if (newX < 0 || newX >= GRID_WIDTH || newY >= GRID_HEIGHT) {
                        return false;
                    }

                    if (newY >= 0 && grid[newY][newX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void gameOver() {
        gameRunning = false;
        gameTimer.stop();
        pauseButton.setDisable(true);
        startButton.setText(resources.getString("tetris.newgame"));
    }

    private void handleKeyPress(KeyEvent event) {
        if (!gameRunning || gamePaused || currentTetromino == null) {
            return;
        }

        KeyCode code = event.getCode();
        switch (code) {
            case LEFT:
                if (canMove(currentTetromino.x - 1, currentTetromino.y, currentTetromino.shape)) {
                    currentTetromino.x--;
                }
                break;
            case RIGHT:
                if (canMove(currentTetromino.x + 1, currentTetromino.y, currentTetromino.shape)) {
                    currentTetromino.x++;
                }
                break;
            case DOWN:
                if (canMove(currentTetromino.x, currentTetromino.y + 1, currentTetromino.shape)) {
                    currentTetromino.y++;
                    score += 1;
                }
                break;
            case SPACE:
                while (canMove(currentTetromino.x, currentTetromino.y + 1, currentTetromino.shape)) {
                    currentTetromino.y++;
                    score += 2;
                }
                dropTetromino();
                break;
            case UP:
                rotate();
                break;
            default:
                return;
        }
        event.consume();
    }

    private void rotate() {
        int[][] rotated = rotateShape(currentTetromino.shape);
        if (canMove(currentTetromino.x, currentTetromino.y, rotated)) {
            currentTetromino.shape = rotated;
        }
    }

    private int[][] rotateShape(int[][] shape) {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                rotated[col][rows - 1 - row] = shape[row][col];
            }
        }

        return rotated;
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw grid lines
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(0.5);
        for (int row = 0; row <= GRID_HEIGHT; row++) {
            gc.strokeLine(0, row * CELL_SIZE, canvas.getWidth(), row * CELL_SIZE);
        }
        for (int col = 0; col <= GRID_WIDTH; col++) {
            gc.strokeLine(col * CELL_SIZE, 0, col * CELL_SIZE, canvas.getHeight());
        }

        // Draw placed blocks
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                if (grid[row][col] != 0) {
                    drawBlock(gc, col, row, TETROMINO_COLORS[grid[row][col] - 1]);
                }
            }
        }

        // Draw current tetromino
        if (currentTetromino != null) {
            int[][] shape = currentTetromino.shape;
            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    if (shape[row][col] != 0) {
                        int gridX = currentTetromino.x + col;
                        int gridY = currentTetromino.y + row;
                        if (gridY >= 0 && gridY < GRID_HEIGHT && gridX >= 0 && gridX < GRID_WIDTH) {
                            drawBlock(gc, gridX, gridY, TETROMINO_COLORS[currentTetromino.type - 1]);
                        }
                    }
                }
            }
        }
    }

    private void drawBlock(GraphicsContext gc, int col, int row, Color color) {
        int x = col * CELL_SIZE;
        int y = row * CELL_SIZE;

        gc.setFill(color);
        gc.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
    }

    private void updateUI() {
        scoreLabel.setText(resources.getString("tetris.score") + ": " + score);
        levelLabel.setText(resources.getString("tetris.level") + ": " + level);
        linesLabel.setText(resources.getString("tetris.lines") + ": " + linesCleared);
    }

    private void updateButtonTexts() {
        if (resources != null) {
            startButton.setText(resources.getString("tetris.newgame"));
            pauseButton.setText(resources.getString("tetris.pause"));

            controlsLabel.setText(resources.getString("tetris.controls"));
            leftLabel.setText(resources.getString("tetris.left"));
            rightLabel.setText(resources.getString("tetris.right"));
            downLabel.setText(resources.getString("tetris.down"));
            dropLabel.setText(resources.getString("tetris.drop"));
            rotateLabel.setText(resources.getString("tetris.rotate"));
        }
    }

    private static class Tetromino {
        private static final int[][][] SHAPES = {
                // I (Cyan)
                {
                        {1, 1, 1, 1}
                },
                // O (Yellow)
                {
                        {1, 1},
                        {1, 1}
                },
                // T (Magenta)
                {
                        {0, 1, 0},
                        {1, 1, 1}
                },
                // S (Green)
                {
                        {0, 1, 1},
                        {1, 1, 0}
                },
                // Z (Red)
                {
                        {1, 1, 0},
                        {0, 1, 1}
                },
                // J (Blue)
                {
                        {1, 0, 0},
                        {1, 1, 1}
                },
                // L (Orange)
                {
                        {0, 0, 1},
                        {1, 1, 1}
                }
        };

        int x = 3;
        int y = 0;
        int type;
        int[][] shape;

        public Tetromino() {
            type = (int) (Math.random() * SHAPES.length) + 1;
            shape = SHAPES[type - 1].clone();
        }
    }
}

