import javax.swing.*;

import javazoom.jl.player.Player;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Random;

public class TetrisPanel extends JPanel {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    private int[][] board = new int[HEIGHT][WIDTH];
    private Tetromino currentPiece;
    private Timer timer;
    private Timer timer2;
    private boolean isGameOver = false;
    // ... 其他變量如 SHAPES, COLORS 等
    private int level = 1; // 初始等級
    private int linesCleared = 0; // 總清除行數
    private static final int LINES_PER_LEVEL = 10; // 每10行升一級
    private int score = 0;
    private String lastAction = "";
    private boolean isPlayingMusic = true;
    private Thread musicThread;
    //shapes of blocks
    private final int[][][] SHAPES = {
        {{1, 1, 1, 1}}, // I
        {{1, 1}, {1, 1}}, // O
        {{1, 1, 1}, {0, 1, 0}}, // T
        {{1, 1, 1}, {1, 0, 0}}, // L
        {{1, 1, 1}, {0, 0, 1}}, // J
        {{1, 1, 0}, {0, 1, 1}}, // S
        {{0, 1, 1}, {1, 1, 0}}  // Z
    };
    private static final int[] SPEED_TABLE = {
        800,  // 等級 1: ~53幀 (800ms)
        716,  // 等級 2: ~48幀
        633,  // 等級 3: ~43幀
        550,  // 等級 4: ~38幀
        466,  // 等級 5: ~33幀
        383,  // 等級 6: ~28幀
        300,  // 等級 7: ~23幀
        216,  // 等級 8: ~18幀
        133,  // 等級 9: ~13幀
        100,  // 等級 10: ~10幀
        83,   // 等級 11: ~8幀
        83,   // 等級 12: ~8幀
        83,   // 等級 13: ~8幀
        66,   // 等級 14: ~7幀
        50    // 等級 15: ~6幀
    };
    
    private final Color[] COLORS = {
        Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.ORANGE,
        Color.BLUE, Color.GREEN, Color.RED
    };
    
    class Tetromino {
        int[][] shape;
        Color color;
        int x, y;
        int type;
    
        Tetromino(int[][] shape, Color color, int type) {
            this.shape = shape;
            this.color = color;
            this.type = type;
            this.x = WIDTH / 2 - shape[0].length / 2;
            this.y = 0;
        }
    }

    public TetrisPanel() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isGameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            moveLeft();
                            lastAction = "left";
                            break;
                        case KeyEvent.VK_RIGHT:
                            moveRight();
                            lastAction = "right";
                            break;
                        case KeyEvent.VK_DOWN:
                            moveDown();
                            lastAction = "down";
                            break;
                        case KeyEvent.VK_UP:
                            rotate();
                            lastAction = "rotate";
                            break;
                    }
                    repaint();
                }
            }
        });
        currentPiece = createNewPiece();
        timer = new Timer(500, e -> {
            if (!isGameOver) {
                moveDown();
                repaint();
            }
        });
        timer.start();
        musicThread = new Thread(new Runnable() {
            public void run() {
                playMusic();
            }
        });
        musicThread.start();
        timer2 = new Timer(SPEED_TABLE[0], e -> moveDown());
        timer2.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 繪製遊戲板
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j] != 0) {
                    g.setColor(new Color(board[i][j]));
                    g.fillRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(Color.GRAY);
                    g.drawRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
        // 繪製當前方塊
        g.setColor(currentPiece.color);
        for (int i = 0; i < currentPiece.shape.length; i++) {
            for (int j = 0; j < currentPiece.shape[i].length; j++) {
                if (currentPiece.shape[i][j] == 1) {
                    int drawX = (currentPiece.x + j) * BLOCK_SIZE;
                    int drawY = (currentPiece.y + i) * BLOCK_SIZE;
                    if (drawY >= 0) {
                        g.fillRect(drawX, drawY, BLOCK_SIZE, BLOCK_SIZE);
                        g.setColor(Color.GRAY);
                        g.drawRect(drawX, drawY, BLOCK_SIZE, BLOCK_SIZE);
                        g.setColor(currentPiece.color);
                    }
                }
            }
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20); // 在 (10, 20) 位置顯示分數
        g.drawString("Level: " + level, 10, 50); // 在 (10, 50) 位置顯示等級

        // 繪製遊戲結束提示
        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over", WIDTH * BLOCK_SIZE / 4, HEIGHT * BLOCK_SIZE / 2);
        }
    }

    // 其他方法：createNewPiece, isValidMove, moveLeft, moveRight, moveDown, rotate, placePiece, clearLines 等
        private Tetromino createNewPiece() {
        Random random = new Random();
        int index = random.nextInt(SHAPES.length);
        return new Tetromino(SHAPES[index], COLORS[index], index);
    }

    private boolean isValidMove(int newX, int newY, int[][] shape) {
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] == 1) {
                    int boardX = newX + j;
                    int boardY = newY + i;
                    if (boardX < 0 || boardX >= WIDTH || boardY >= HEIGHT ||
                        (boardY >= 0 && board[boardY][boardX] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void moveLeft() {
        if (isValidMove(currentPiece.x - 1, currentPiece.y, currentPiece.shape)) {
            currentPiece.x--;
        }
    }

    private void moveRight() {
        if (isValidMove(currentPiece.x + 1, currentPiece.y, currentPiece.shape)) {
            currentPiece.x++;
        }
    }

    private boolean moveDown() {
        if (isValidMove(currentPiece.x, currentPiece.y + 1, currentPiece.shape)) {
            currentPiece.y++;
            return true;
        } else {
            placePiece();
            clearLines();
            currentPiece = createNewPiece();
            if (!isValidMove(currentPiece.x, currentPiece.y, currentPiece.shape)) {
                isGameOver = true;
                isPlayingMusic = false;
                timer.stop();
            }
            return false;
        }
    }
    
    private void playMusic() {
        try {
            while (isPlayingMusic) {
                FileInputStream fis = new FileInputStream("tetris.mp3");
                BufferedInputStream bis = new BufferedInputStream(fis);
                Player player = new Player(bis);
                player.play();
            }
        } catch (Exception e) {
            System.out.println("Problem playing sound file: " + e.getMessage());
        }
    }

    private void rotate() {
        int[][] rotated = new int[currentPiece.shape[0].length][currentPiece.shape.length];
        for (int i = 0; i < currentPiece.shape.length; i++) {
            for (int j = 0; j < currentPiece.shape[i].length; j++) {
                rotated[j][currentPiece.shape.length - 1 - i] = currentPiece.shape[i][j];
            }
        }
        if (isValidMove(currentPiece.x, currentPiece.y, rotated)) {
            currentPiece.shape = rotated;
        }
    }

    private void placePiece() {
        for (int i = 0; i < currentPiece.shape.length; i++) {
            for (int j = 0; j < currentPiece.shape[i].length; j++) {
                if (currentPiece.shape[i][j] == 1) {
                    int boardY = currentPiece.y + i;
                    int boardX = currentPiece.x + j;
                    if (boardY >= 0) {
                        board[boardY][boardX] = currentPiece.color.getRGB();
                    }
                }
            }
        }
    
        // T-spin 檢測
        if (currentPiece.type == 2 && lastAction.equals("rotate")) {
            int centerX = currentPiece.x + 1;
            int centerY = currentPiece.y + 1;
            int occupiedCount = 0;
    
            // 檢查四個對角位置
            int[] dx = {-1, 1, -1, 1};
            int[] dy = {-1, -1, 1, 1};
            for (int i = 0; i < 4; i++) {
                int checkX = centerX + dx[i];
                int checkY = centerY + dy[i];
                if (checkX < 0 || checkX >= WIDTH || checkY < 0 || checkY >= HEIGHT || (checkY >= 0 && board[checkY][checkX] != 0)) {
                    occupiedCount++;
                }
            }
    
            if (occupiedCount >= 3) {
                // T-spin 檢測成功
                System.out.println("T-spin detected!");
                // 可以在此處添加分數獎勵，例如 score += 100;
            }
        }
    }

    private void clearLines() {
        int linesClearedThisTurn = 0;
        for (int i = HEIGHT - 1; i >= 0; i--) {
            boolean isFull = true;
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                // 移除滿行並將上方行下移
                for (int k = i; k > 0; k--) {
                    System.arraycopy(board[k-1], 0, board[k], 0, WIDTH);
                }
                for (int j = 0; j < WIDTH; j++) {
                    board[0][j] = 0;
                }
                linesClearedThisTurn++;
                i++; // 重新檢查當前行
            }
        }
        // 更新總清除行數和分數
        linesCleared += linesClearedThisTurn;
        switch (linesClearedThisTurn) {
            case 1: score += 100 * level; break;
            case 2: score += 300 * level; break;
            case 3: score += 500 * level; break;
            case 4: score += 800 * level; break;
        }
        // 檢查是否升級
        int newLevel = linesCleared / LINES_PER_LEVEL + 1;
        if (newLevel > level) {
            level = newLevel;
            adjustGameSpeed(); // 調整速度
        }
    }

    private void adjustGameSpeed() {
        int delay = SPEED_TABLE[Math.min(level - 1, SPEED_TABLE.length - 1)];
        timer2.setDelay(delay);
    }

}