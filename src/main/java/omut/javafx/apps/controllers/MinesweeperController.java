package omut.javafx.apps.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import omut.javafx.apps.model.Cell;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MinesweeperController implements Initializable {

    private static final int ROWS = 14;
    private static final int COLS = 14;
    private static final int MINES = 40;

    private static final String flag = "⚑";
    private static final String bomb = "💥";

    @FXML private GridPane gridPane;
    @FXML private Label minesLabel;
    @FXML private Label timerLabel;
    @FXML private Button resetButton;

    @FXML
    private void handleReset() {
        newGame();
    }

    private Cell[][] cells;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private int remainingMines;
    private int revealedCells = 0;
    private Timeline timeline;
    private int secondsElapsed = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTimer();
        newGame();
    }

    private void setupTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (gameStarted && !gameOver) {
                secondsElapsed++;
                updateTimerLabel();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void newGame() {
        resetGame();
        createBoard();
        placeMines();
        calculateNeighbors();
        setupGrid();
        timeline.play();
    }

    private void resetGame() {
        gameStarted = false;
        gameOver = false;
        revealedCells = 0;
        remainingMines = MINES;
        secondsElapsed = 0;
        updateMinesLabel();
        updateTimerLabel();
        timeline.stop();
    }

    private void createBoard() {
        cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cells[row][col] = new Cell(row, col);
            }
        }
    }

    private void placeMines() {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < MINES) {
            int row = random.nextInt(ROWS);
            int col = random.nextInt(COLS);

            if (!cells[row][col].isMine()) {
                cells[row][col].setMine(true);
                minesPlaced++;
            }
        }
    }

    private void calculateNeighbors() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (!cells[row][col].isMine()) {
                    int count = countMinesAround(row, col);
                    cells[row][col].setNeighborMines(count);
                }
            }
        }
    }

    private int countMinesAround(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS) {
                    if (cells[newRow][newCol].isMine()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void setupGrid() {
        gridPane.getChildren().clear();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Cell cell = cells[row][col];
                Button button = createCellButton(cell);
                gridPane.add(button, col, row);
            }
        }
    }

    private Button createCellButton(Cell cell) {
        Button button = new Button();
        button.getStyleClass().add("game-button");
        updateButtonStyle(button, cell);

        button.setOnMouseClicked(event -> {
            if (gameOver) return;

            if (event.getButton() == MouseButton.PRIMARY) {
                handleLeftClick(cell, button);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                handleRightClick(cell, button);
            }
        });

        return button;
    }

    private void handleLeftClick(Cell cell, Button button) {
        if (cell.isFlagged() || cell.isRevealed()) return;

        if (!gameStarted) {
            gameStarted = true;
        }

        if (cell.isMine()) {
            gameOver = true;
            revealAllMines();
            showGameOverMessage();
        } else {
            revealCell(cell, button);
            checkWin();
        }
    }

    private void handleRightClick(Cell cell, Button button) {
        if (cell.isRevealed() || gameOver) return;

        if (!cell.isFlagged()) {
            cell.setFlagged(true);
            remainingMines--;
            button.setText(flag);
            updateButtonStyle(button, cell);
        } else {
            cell.setFlagged(false);
            remainingMines++;
            updateButtonStyle(button, cell);
        }

        updateMinesLabel();
    }

    private void revealCell(Cell cell, Button button) {
        if (cell.isRevealed() || cell.isFlagged()) return;

        cell.setRevealed(true);
        revealedCells++;

        if (cell.getNeighborMines() > 0) {
            button.setText(String.valueOf(cell.getNeighborMines()));
            updateButtonStyle(button, cell);
        } else {
            button.setText("");
            updateButtonStyle(button, cell);

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newRow = cell.getRow() + i;
                    int newCol = cell.getCol() + j;

                    if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS) {
                        Cell neighbor = cells[newRow][newCol];
                        if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                            Button neighborButton = getButtonAt(newRow, newCol);
                            if (neighborButton != null) {
                                revealCell(neighbor, neighborButton);
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateButtonStyle(Button button, Cell cell) {
        button.getStyleClass().clear();
        button.getStyleClass().add("game-button");

        if (cell.isRevealed()) {
            if (cell.getNeighborMines() > 0) {
                button.getStyleClass().add("revealed-number");
                setNumberColorClass(button, cell.getNeighborMines());
            } else {
                button.getStyleClass().add("revealed-empty");
            }
        } else if (cell.isFlagged()) {
            button.getStyleClass().add("flagged-cell");
        }
    }

    private void setNumberColorClass(Button button, int number) {
        switch (number) {
            case 1: button.getStyleClass().add("number-1"); break;
            case 2: button.getStyleClass().add("number-2"); break;
            case 3: button.getStyleClass().add("number-3"); break;
            case 4: button.getStyleClass().add("number-4"); break;
            case 5: button.getStyleClass().add("number-5"); break;
            case 6: button.getStyleClass().add("number-6"); break;
            case 7: button.getStyleClass().add("number-7"); break;
            case 8: button.getStyleClass().add("number-8"); break;
        }
    }

    private Button getButtonAt(int row, int col) {
        for (var node : gridPane.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer colIndex = GridPane.getColumnIndex(node);
            if (rowIndex != null && colIndex != null &&
                    rowIndex == row && colIndex == col) {
                return (Button) node;
            }
        }
        return null;
    }

    private void revealAllMines() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Cell cell = cells[row][col];
                if (cell.isMine()) {
                    Button button = getButtonAt(row, col);
                    if (button != null) {
                        button.setText(bomb);
                        button.getStyleClass().add("mine-cell");
                    }
                }
            }
        }
    }

    private void checkWin() {
        if (revealedCells == ROWS * COLS - MINES) {
            gameOver = true;
            timeline.stop();
            showWinMessage();
        }
    }

    private void showGameOverMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("Boom! You're dead!");
        alert.showAndWait();
    }

    private void showWinMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Win!");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations! You win!");
        alert.showAndWait();
    }

    private void updateMinesLabel() {
        minesLabel.setText("Mines: " + remainingMines);
    }

    private void updateTimerLabel() {
        timerLabel.setText(String.format("Time: %02d:%02d", secondsElapsed / 60, secondsElapsed % 60));
    }
}