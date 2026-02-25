package omut.javafx.apps.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CheckersController implements Initializable {

    private static final int SIZE = 8;
    private static final int TILE_SIZE = 70;

    @FXML
    private GridPane withLabels;

    private final Cell[][] board = new Cell[SIZE][SIZE];

    private boolean whiteTurn = true;
    private Cell selectedCell = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        buildBoard();
        setupPieces();
    }

    private void buildBoard() {

        GridPane boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Cell cell = new Cell(r, c);
                board[r][c] = cell;
                boardGrid.add(cell, c, r);
            }
        }

        for (int i = 0; i < SIZE + 2; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPrefWidth(i == 0 || i == SIZE + 1 ? 20 : TILE_SIZE);
            withLabels.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < SIZE + 2; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPrefHeight(i == 0 || i == SIZE + 1 ? 20 : TILE_SIZE);
            withLabels.getRowConstraints().add(rc);
        }

        for (int col = 0; col < SIZE; col++) {
            Label top = new Label(String.valueOf((char) ('A' + col)));
            Label bottom = new Label(String.valueOf((char) ('A' + col)));
            top.setStyle("-fx-font-weight:bold;");
            bottom.setStyle("-fx-font-weight:bold;");
            withLabels.add(top, col + 1, 0);
            withLabels.add(bottom, col + 1, SIZE + 1);
            align(top);
            align(bottom);
        }

        for (int row = 0; row < SIZE; row++) {
            Label left = new Label(String.valueOf(SIZE - row));
            Label right = new Label(String.valueOf(SIZE - row));
            left.setStyle("-fx-font-weight:bold;");
            right.setStyle("-fx-font-weight:bold;");
            withLabels.add(left, 0, row + 1);
            withLabels.add(right, SIZE + 1, row + 1);
            align(left);
            align(right);
        }

        withLabels.add(boardGrid, 1, 1, SIZE, SIZE);
    }

    private void align(Label label) {
        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setValignment(label, VPos.CENTER);
    }

    private void setupPieces() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < SIZE; c++)
                if ((r + c) % 2 == 1)
                    board[r][c].setPiece(new Piece(false));

        for (int r = SIZE - 3; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if ((r + c) % 2 == 1)
                    board[r][c].setPiece(new Piece(true));
    }

    private class Cell extends StackPane {

        final int row, col;
        Piece piece;
        final Rectangle base;
        final Rectangle highlight;

        Cell(int r, int c) {
            row = r;
            col = c;

            setPrefSize(TILE_SIZE, TILE_SIZE);

            base = new Rectangle(TILE_SIZE, TILE_SIZE);
            base.setFill((r + c) % 2 == 0 ?
                    Color.web("#EEEED2") :
                    Color.web("#769656"));

            highlight = new Rectangle(TILE_SIZE, TILE_SIZE);
            highlight.setOpacity(0);

            getChildren().addAll(base, highlight);

            setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY)
                    onClick();
            });
        }

        void setPiece(Piece p) {
            piece = p;
            update();
        }

        void removePiece() {
            piece = null;
            update();
        }

        void update() {
            getChildren().removeIf(n -> n instanceof Circle);
            if (piece != null) {
                Circle circle = new Circle(TILE_SIZE * 0.35);
                circle.setFill(piece.isWhite ? Color.WHITE : Color.BLACK);
                getChildren().add(circle);
            }
        }

        void onClick() {

            if (selectedCell == null) {
                if (piece != null && piece.isWhite == whiteTurn) {
                    selectCell(this);
                }
                return;
            }

            if (this == selectedCell) {
                clearSelection();
                return;
            }

            if (piece != null && piece.isWhite == whiteTurn) {
                clearSelection();
                selectCell(this);
                return;
            }

            if (tryMove(selectedCell, this)) {
                whiteTurn = !whiteTurn;
            }

            clearSelection();
        }
    }

    private void selectCell(Cell cell) {
        selectedCell = cell;
        cell.highlight.setFill(Color.LIGHTGREEN);
        cell.highlight.setOpacity(0.8);
    }

    private void clearSelection() {
        if (selectedCell != null) {
            selectedCell.highlight.setOpacity(0);
        }
        selectedCell = null;
    }

    private boolean tryMove(Cell from, Cell to) {

        if (to.piece != null) return false;

        Piece p = from.piece;
        if (p == null) return false;

        int dr = to.row - from.row;
        int dc = to.col - from.col;

        int direction = p.isWhite ? -1 : 1;

        if (Math.abs(dr) == 1 && Math.abs(dc) == 1) {
            if (p.isKing || dr == direction) {
                movePiece(from, to);
                return true;
            }
            return false;
        }

        if (Math.abs(dr) == 2 && Math.abs(dc) == 2) {
            int midRow = (from.row + to.row) / 2;
            int midCol = (from.col + to.col) / 2;
            Cell middle = board[midRow][midCol];

            if (middle.piece != null && middle.piece.isWhite != p.isWhite) {
                movePiece(from, to);
                middle.removePiece();
                return true;
            }
        }

        return false;
    }

    private void movePiece(Cell from, Cell to) {
        to.setPiece(from.piece);
        from.removePiece();
    }

    private static class Piece {
        boolean isWhite;
        boolean isKing = false;

        Piece(boolean w) {
            isWhite = w;
        }
    }
}