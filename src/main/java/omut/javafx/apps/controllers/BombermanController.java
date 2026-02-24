package omut.javafx.apps.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BombermanController {

    static final int TILE = 40;
    static final int COLS = 15;
    static final int ROWS = 11;

    private GraphicsContext g;

    int[][] map = new int[ROWS][COLS];

    Player player;
    List<Bomb> bombs = new ArrayList<>();
    List<Enemy> enemies = new ArrayList<>();
    List<Explosion> explosions = new ArrayList<>();
    Set<KeyCode> keys = new HashSet<>();

    private Random rnd = new Random();
    private AnimationTimer gameLoop;

    private boolean gameOver = false;
    private boolean win = false;

    @FXML private Canvas canvas;
    @FXML private StackPane overlay;
    @FXML private Label gameOverLabel;

    @FXML
    public void initialize() {
        g = canvas.getGraphicsContext2D();
        setupInput();
        restartGame();
    }

    private void setupInput() {
        canvas.sceneProperty()
                .addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        newScene.setOnKeyPressed(e -> keys.add(e.getCode()));
                        newScene.setOnKeyReleased(e -> keys.remove(e.getCode()));
                    }
                });
    }

    @FXML
    private void restartGame() {
        gameOver = false;
        win = false;

        bombs.clear();
        enemies.clear();
        explosions.clear();
        keys.clear();

        initMap();
        player = new Player(1, 1);
        enemies.add(new Enemy(COLS - 2, ROWS - 2));
        enemies.add(new Enemy(COLS - 2, 1));

        overlay.setVisible(false);

        startGameLoop();
    }

    private void startGameLoop() {
        if (gameLoop != null) gameLoop.stop();

        gameLoop = new AnimationTimer() {
            long last = 0;
            @Override
            public void handle(long now) {
                if (last == 0) last = now;
                double dt = (now - last) / 1e9;
                last = now;

                update(dt);
                render();
            }
        };
        gameLoop.start();
    }

    void initMap() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                map[r][c] = 0;

        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++) {
                if (r == 0 || r == ROWS - 1 || c == 0 || c == COLS - 1)
                    map[r][c] = 1;
                else if (r % 2 == 0 && c % 2 == 0)
                    map[r][c] = 1;
            }

        for (int r = 1; r < ROWS - 1; r++)
            for (int c = 1; c < COLS - 1; c++) {
                if (map[r][c] == 0 && !((r == 1 && c <= 2) || (r <= 2 && c == 1))) {
                    if (rnd.nextDouble() < 0.6) map[r][c] = 2;
                }
            }
    }

    void update(double dt) {
        if (gameOver) return;

        int dx = 0, dy = 0;
        if (keys.contains(KeyCode.LEFT)) dx = -1;
        if (keys.contains(KeyCode.RIGHT)) dx = 1;
        if (keys.contains(KeyCode.UP)) dy = -1;
        if (keys.contains(KeyCode.DOWN)) dy = 1;
        if (dx != 0 && dy != 0) dy = 0;

        if ((dx != 0 || dy != 0) && player.moveCooldown <= 0) {
            player.tryMove(dx, dy);
            player.moveCooldown = 0.12;
        }
        if (player.moveCooldown > 0) player.moveCooldown -= dt;

        if (keys.contains(KeyCode.SPACE)) player.tryPlaceBomb();

        bombs.removeIf(b -> {
            b.update(dt);
            if (b.shouldExplode) {
                applyExplosion(b.col, b.row, b.range);
                player.onBombExploded();
                return true;
            }
            return false;
        });

        explosions.removeIf(e -> e.updateAndCheck(dt));

        enemies.forEach(e -> e.update(dt));

        boolean hitByEnemy = enemies.stream().
                anyMatch(e -> e.col == player.col && e.row == player.row);
        boolean hitByExplosion = explosions.stream()
                .anyMatch(ex -> ex.col == player.col && ex.row == player.row);
        if (!gameOver && (hitByEnemy || hitByExplosion)) {
            gameOver = true;
            win = false;
            showGameOver();
            return;
        }

        enemies.removeIf(e ->
                explosions.stream()
                        .anyMatch(ex -> ex.col == e.col && ex.row == e.row)
        );

        if (!gameOver && enemies.isEmpty()) {
            gameOver = true;
            win = true;
            showGameOver();
        }
    }

    private void showGameOver() {
        if (gameLoop != null) gameLoop.stop();
        gameOverLabel.setText(win ? "YOU WIN!" : "GAME OVER");
        overlay.setVisible(true);
    }

    void applyExplosion(int col, int row, int range) {
        createExplosion(col, row);

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            for (int i = 1; i <= range; i++) {
                int c = col + d[0] * i;
                int r = row + d[1] * i;
                if (map[r][c] == 1) break;
                createExplosion(c, r);
                if (map[r][c] == 2) {
                    map[r][c] = 0;
                    break;
                }
            }
        }
    }

    private void createExplosion(int col, int row) {
        explosions.add(new Explosion(col, row));
    }

    void render() {
        g.setFill(Color.LIGHTBLUE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++) {
                double x = c * TILE, y = r * TILE;
                if (map[r][c] == 1) g.setFill(Color.DARKGRAY);
                else if (map[r][c] == 2) g.setFill(Color.BROWN);

                if(map[r][c] == 1) g.fillRect(x, y, TILE, TILE);
                else if(map[r][c] == 2) g.fillRect(x + 4, y + 4, TILE - 8, TILE - 8);
            }

        g.setFill(Color.BLACK);
        for(Bomb b: bombs) g.fillOval(b.col * TILE + TILE / 4.0, b.row * TILE + TILE / 4.0, TILE / 2.0, TILE / 2.0);

        g.setFill(Color.PURPLE);
        for(Enemy e: enemies) g.fillOval(e.col * TILE + 6, e.row * TILE + 6, TILE - 12, TILE - 12);

        g.setFill(Color.ORANGE);
        for(Explosion e: explosions) g.fillRect(e.col * TILE + 8, e.row * TILE + 8, TILE - 16, TILE - 16);

        g.setFill(Color.BLUE);
        g.fillOval(player.col * TILE + 6, player.row * TILE + 6, TILE - 12, TILE - 12);
    }

    class Player {
        int col, row;
        int availableBombs = 1, bombRange = 2;
        double moveCooldown = 0;
        Player(int c, int r) { col = c; row = r; }
        void tryMove(int dx, int dy) {
            int nc = col + dx, nr = row + dy;
            if(nc > 0 && nc < COLS - 1 && nr > 0 && nr < ROWS - 1 && map[nr][nc] == 0) {
                col = nc; row = nr;
            }
        }
        void tryPlaceBomb() {
            if(availableBombs > 0) {
                bombs.add(new Bomb(col, row, bombRange));
                availableBombs--;
            }
        }
        void onBombExploded(){ availableBombs++; }
    }

    class Bomb {
        int col, row, range;
        double timer = 2;
        boolean shouldExplode = false;
        Bomb(int c, int r, int range) { col = c; row = r; this.range = range; }
        void update(double dt){ timer -= dt; if(timer <= 0) shouldExplode = true; }
    }

    class Enemy {
        int col,row;
        double moveTimer=0;
        Enemy(int c, int r) { col = c; row = r; }

        void update(double dt) {
            moveTimer -= dt;
            if(moveTimer <= 0) {
                boolean horizontal = rnd.nextBoolean();
                if(horizontal) {
                    int newCol = col + (rnd.nextBoolean()? 1: -1);
                    if(newCol > 0 && newCol < COLS - 1 && map[row][newCol] == 0) col = newCol;
                } else {
                    int newRow = row + (rnd.nextBoolean()? 1: -1);
                    if(newRow > 0 && newRow < ROWS - 1 && map[newRow][col] == 0) row = newRow;
                }
                moveTimer=0.5;
            }
        }
    }

    class Explosion {
        public int col;
        public int row;
        private double timer = 0.4;

        public Explosion(int c, int r) {
            col = c;
            row = r;
        }

        public void update(double dt) {
            timer -= dt;
        }

        public boolean isFinished() {
            return timer <= 0;
        }

        public boolean updateAndCheck(double dt) {
            update(dt);
            return isFinished();
        }
    }
}