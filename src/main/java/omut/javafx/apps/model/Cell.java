package omut.javafx.apps.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

public class Cell {
    private final IntegerProperty row = new SimpleIntegerProperty();
    private final IntegerProperty col = new SimpleIntegerProperty();
    private final BooleanProperty isMine = new SimpleBooleanProperty(false);
    private final BooleanProperty isRevealed = new SimpleBooleanProperty(false);
    private final BooleanProperty isFlagged = new SimpleBooleanProperty(false);
    private final IntegerProperty neighborMines = new SimpleIntegerProperty(0);

    public Cell(int row, int col) {
        this.row.set(row);
        this.col.set(col);
    }

    public int getRow() { return row.get(); }
    public int getCol() { return col.get(); }
    public boolean isMine() { return isMine.get(); }
    public boolean isRevealed() { return isRevealed.get(); }
    public boolean isFlagged() { return isFlagged.get(); }
    public int getNeighborMines() { return neighborMines.get(); }

    public void setMine(boolean mine) { isMine.set(mine); }
    public void setRevealed(boolean revealed) { isRevealed.set(revealed); }
    public void setFlagged(boolean flagged) { isFlagged.set(flagged); }
    public void setNeighborMines(int count) { neighborMines.set(count); }

    public BooleanProperty isMineProperty() { return isMine; }
    public BooleanProperty isRevealedProperty() { return isRevealed; }
    public BooleanProperty isFlaggedProperty() { return isFlagged; }
    public IntegerProperty neighborMinesProperty() { return neighborMines; }
}