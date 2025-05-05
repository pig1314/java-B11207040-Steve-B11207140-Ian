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
    private boolean isGameOver = false;
    private boolean isPaused = false;
    private int level = 1; // 初始等級
    private int linesCleared = 0; // 總清除行數
    private static final int LINES_PER_LEVEL = 10; // 每10行升一級
    private int score = 0;
    private String lastAction = "";
    private boolean isPlayingMusic = true;
    private Thread musicThread;

    
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
    
    
    
    

    public TetrisPanel() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    togglePause();
                }
                if (!isGameOver && !isPaused) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            currentPiece.move(-1, 0);
                            lastAction = "left";
                            break;
                        case KeyEvent.VK_RIGHT:
                            currentPiece.move(1, 0);
                            lastAction = "right";
                            break;
                        case KeyEvent.VK_DOWN:
                            moveDown();
                            lastAction = "down";
                            break;
                        case KeyEvent.VK_UP:
                            currentPiece.rotateCW();
                            
                            lastAction = "rotate";
                            break;
                        case KeyEvent.VK_NUMPAD1:
                            currentPiece.rotateCCW();
                            
                            lastAction = "rotate";
                            break;
                    }
                    repaint();
                }
            }
        });
        currentPiece = createNewPiece();
        timer = new Timer(800, e -> {
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
        //timer = new Timer(SPEED_TABLE[0], e -> moveDown());
        //timer.start();
    }

    private void togglePause() {
        if (!isGameOver) {
            isPaused = !isPaused;
            if (isPaused) {
                timer.stop();
            } else {
                timer.start();
            }
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j] != 0) {
                    g.setColor(new Color(board[i][j]));
                    g.fillRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE , BLOCK_SIZE );
                    g.setColor(Color.GRAY);
                    g.drawRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
        if (currentPiece != null) {
            g.setColor(currentPiece.getColor());
            for (Point p : currentPiece.getAbsolutePoints()) {
                if (p.y >= 0) {
                    g.fillRect(p.x * BLOCK_SIZE, p.y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(Color.GRAY);
                    g.drawRect(p.x * BLOCK_SIZE, p.y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(currentPiece.getColor());
                }
            }
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Level: " + level, 10, 50);
        if (isPaused) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Paused", WIDTH * BLOCK_SIZE / 4, HEIGHT * BLOCK_SIZE / 2);
        }
        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", WIDTH * BLOCK_SIZE / 4, HEIGHT * BLOCK_SIZE / 2);
        }
    }
    

    
    private Tetromino createNewPiece() {
        Random random = new Random();
        int type = random.nextInt(7);
        switch (type) {
            case 0: return new TetrominoI(board);
            case 1: return new TetrominoO(board);
            case 2: return new TetrominoT(board);
            case 3: return new TetrominoL(board);
            case 4: return new TetrominoJ(board);
            case 5: return new TetrominoS(board);
            case 6: return new TetrominoZ(board);
            default: throw new IllegalStateException("Invalid tetromino type");
        }
    }



    private boolean moveDown() {
        if (isPaused) {
            return false;
        }
        if (currentPiece != null && currentPiece.moveDown()) {
            return true;
        } else {
            placePiece();
            clearLines();
            currentPiece = createNewPiece();
            if (!canMove(currentPiece)) {
                isGameOver = true;
                timer.stop();
            }
            repaint();
            return false;
        }
    }
    private boolean canMove(Tetromino piece) {
        return piece.canMove(0, 0, piece.getShape());
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


    private void placePiece() {
        for (Point p : currentPiece.getAbsolutePoints()) {
            int boardX = p.x;
            int boardY = p.y;
            if (boardY >= 0) {
                board[boardY][boardX] = currentPiece.getColor().getRGB();
            }
        }
        // T-spin 檢測（針對 T 方塊）
        if (currentPiece instanceof TetrominoT && (lastAction.equals("rotate") || lastAction.equals("rotateBack"))) {
            Point center = new Point(currentPiece.getPosition().x + 1, currentPiece.getPosition().y + 1);
            int occupiedCount = 0;
            int[] dx = {-1, 1, -1, 1};
            int[] dy = {-1, -1, 1, 1};
            for (int i = 0; i < 4; i++) {
                int checkX = center.x + dx[i];
                int checkY = center.y + dy[i];
                if (checkX < 0 || checkX >= WIDTH || checkY < 0 || checkY >= HEIGHT || (checkY >= 0 && board[checkY][checkX] != 0)) {
                    occupiedCount++;
                }
            }
            if (occupiedCount >= 3) {
                score += 100 * level; // T-spin 獎勵分數
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
        timer.setDelay(delay);
    }

}