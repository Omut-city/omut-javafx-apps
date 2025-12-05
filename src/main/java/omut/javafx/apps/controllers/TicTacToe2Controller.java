package omut.javafx.apps.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import omut.javafx.apps.components.TicTacToe2;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Supplier;

@Controller
public class TicTacToe2Controller implements Initializable {

    private static final int SIZE = 3;

    @FXML
    private GridPane grid;

    @FXML
    private Label statusLabel;

    @FXML
    private Button resetButton;

    private Button[][] cells = new Button[SIZE][SIZE];
    private char[][] board = new char[SIZE][SIZE];
    private char currentPlayer = 'X';
    private boolean gameOver = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {

                final int rr = r;
                final int cc = c;

                Button btn = TicTacToe2.createButton();
                btn.setOnAction(e -> handleMove(rr, cc));
                cells[r][c] = btn;
                grid.add(btn, c, r);

                board[r][c] = ' ';
            }
        }

        resetButton.setOnAction(e -> resetGame());
        statusLabel.setText("Ход: X");
    }

    private void handleMove(int row, int col) {
        if (gameOver) return;
        if (board[row][col] != ' ') return;

        board[row][col] = currentPlayer;
        cells[row][col].setText(String.valueOf(currentPlayer));
        cells[row][col].setDisable(true);

        if (checkWin(currentPlayer)) {
            statusLabel.setText("Победил: " + currentPlayer);
            gameOver = true;
            highlightWinningCells(currentPlayer);
            return;
        }

        if (isBoardFull()) {
            statusLabel.setText("Ничья");
            gameOver = true;
            return;
        }

        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        statusLabel.setText("Ход: " + currentPlayer);
    }

    private boolean checkWin(char player) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
        }

        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true;

        return false;
    }

    private void highlightWinningCells(char player) {
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                TicTacToe2.styleWin(cells[i][0], cells[i][1], cells[i][2]);
                return;
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                TicTacToe2.styleWin(cells[0][i], cells[1][i], cells[2][i]);
                return;
            }
        }

        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            TicTacToe2.styleWin(cells[0][0], cells[1][1], cells[2][2]);
            return;
        }

        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            TicTacToe2.styleWin(cells[0][2], cells[1][1], cells[2][0]);
        }
    }


    private boolean isBoardFull() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == ' ') return false;
            }
        }
        return true;
    }

    private void resetGame() {
        currentPlayer = 'X';
        statusLabel.setText("Ход: X");
        gameOver = false;

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                board[r][c] = ' ';
                Button btn = cells[r][c];
                btn.setText(" ");
                btn.setDisable(false);
                btn.setStyle("");
            }
        }
    }
}
