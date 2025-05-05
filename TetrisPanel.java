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
    private static final int PANEL_WIDTH = 600;
    private static final int PANEL_HEIGHT = 710;
    private static final int BOARD_X = 150;
    private static final int BOARD_Y = 100;
    private static final int HOLD_X = 20;
    private static final int HOLD_Y = 150;
    private static final int NEXT_X = 460;
    private static final int NEXT_Y = 150;
    private int[][] board = new int[HEIGHT][WIDTH];
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private Tetromino holdPiece;
    private boolean hasHeld = false;
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
    private JButton restartButton;
    private JButton quitButton;
    
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
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        setLayout(null); // 使用絕對佈局以定位按鈕

        currentPiece = createNewPiece();
        nextPiece = createNewPiece();
        holdPiece = null;

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
                        case KeyEvent.VK_Z:
                            currentPiece.rotateCCW();
                            lastAction = "rotate";
                            break;
                        case KeyEvent.VK_C:
                            hold();
                            lastAction = "hold";
                            break;
                        case KeyEvent.VK_SPACE:
                            hardDrop();
                            lastAction = "hardDrop";
                            break;
                    }
                    repaint();
                }
            }
        });
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
                // 添加 Restart 和 Quit 按鈕
                restartButton = new JButton("Restart");
                restartButton.setForeground(Color.WHITE);
                restartButton.setBackground(Color.LIGHT_GRAY);
                restartButton.setFont(new Font("Arial", Font.BOLD, 20));
                restartButton.setBounds(BOARD_X + 90, BOARD_Y + 250, 120, 50);
                restartButton.setFocusPainted(false);
                restartButton.addActionListener(e -> restartGame());
                add(restartButton);

                quitButton = new JButton("Quit");
                quitButton.setForeground(Color.WHITE);
                quitButton.setBackground(Color.LIGHT_GRAY);
                quitButton.setFont(new Font("Arial", Font.BOLD, 20));
                quitButton.setBounds(BOARD_X + 90, BOARD_Y + 350, 120, 50);
                quitButton.setFocusPainted(false);
                quitButton.addActionListener(e -> System.exit(0));
                add(quitButton);
            } else {
                timer.start();
                // 移除按鈕
                if (restartButton != null) {
                    remove(restartButton);
                    restartButton = null;
                }
                if (quitButton != null) {
                    remove(quitButton);
                    quitButton = null;
                }
                requestFocusInWindow(); // 確保鍵盤焦點

            }
            revalidate();
            repaint();
        }
    }

    private void restartGame() {
        // 重置遊戲狀態
        board = new int[HEIGHT][WIDTH];
        score = 0;
        level = 1;
        linesCleared = 0;
        hasHeld = false;
        lastAction = "";
        isGameOver = false;
        isPaused = false;
        currentPiece = createNewPiece();
        nextPiece = createNewPiece();
        holdPiece = null;
        timer.setDelay(SPEED_TABLE[0]);
        timer.start();
        // 移除按鈕
        if (restartButton != null) {
            remove(restartButton);
            restartButton = null;
        }
        if (quitButton != null) {
            remove(quitButton);
            quitButton = null;
        }
        revalidate();
        repaint();
        requestFocusInWindow(); // 恢復鍵盤焦點

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 繪製遊戲板
        g.setColor(Color.DARK_GRAY);
        g.fillRect(BOARD_X, BOARD_Y, WIDTH * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j] != 0) {
                    g.setColor(new Color(board[i][j]));
                    g.fillRect(BOARD_X + j * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE, BLOCK_SIZE , BLOCK_SIZE );
                    g.setColor(Color.GRAY);
                    g.drawRect(BOARD_X + j * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE, BLOCK_SIZE , BLOCK_SIZE );
                }
            }
        }
        if (currentPiece != null) {
            g.setColor(currentPiece.getColor());
            for (Point p : currentPiece.getAbsolutePoints()) {
                if (p.y >= 0) {
                    g.fillRect(BOARD_X + p.x * BLOCK_SIZE, BOARD_Y + p.y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(Color.GRAY);
                    g.drawRect(BOARD_X + p.x * BLOCK_SIZE, BOARD_Y + p.y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(currentPiece.getColor());
                }
            }
        }
        // 繪製遊戲板格子線
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= WIDTH; i++) {
            g.drawLine(BOARD_X + i * BLOCK_SIZE, BOARD_Y, BOARD_X + i * BLOCK_SIZE, BOARD_Y + HEIGHT * BLOCK_SIZE);
        }
        for (int i = 0; i <= HEIGHT; i++) {
            g.drawLine(BOARD_X, BOARD_Y + i * BLOCK_SIZE, BOARD_X + WIDTH * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE);
        }
        // 繪製分數框
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(50, 20, 150, 50);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 60, 50);
        // 繪製等級框
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(430, 20, 120, 50);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Level: " + level, 440, 50);

        // 繪製 Hold 區域
        g.setColor(Color.DARK_GRAY);
        g.fillRect(HOLD_X, HOLD_Y, 120, 120);
        g.setColor(Color.WHITE);
        g.drawRect(HOLD_X, HOLD_Y, 120, 120);
        for (int i = 0; i <= 4; i++) {
            g.drawLine(HOLD_X + i * BLOCK_SIZE, HOLD_Y, HOLD_X + i * BLOCK_SIZE, HOLD_Y + 120);
            g.drawLine(HOLD_X, HOLD_Y + i * BLOCK_SIZE, HOLD_X + 120, HOLD_Y + i * BLOCK_SIZE);
        }
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Hold", 60, 140);
        if (holdPiece != null) {
            g.setColor(holdPiece.getColor());
            for (Point p : holdPiece.getShape()) {
                int drawX = HOLD_X + 30 + p.x * BLOCK_SIZE;
                int drawY = HOLD_Y + 60 + p.y * BLOCK_SIZE;
                g.fillRect(drawX, drawY, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
            }
        }

        // 繪製 Next 區域
        g.setColor(Color.DARK_GRAY);
        g.fillRect(NEXT_X, NEXT_Y, 120, 120);
        g.setColor(Color.WHITE);
        g.drawRect(NEXT_X, NEXT_Y, 120, 120);
        for (int i = 0; i <= 4; i++) {
            g.drawLine(NEXT_X + i * BLOCK_SIZE, NEXT_Y, NEXT_X + i * BLOCK_SIZE, NEXT_Y + 120);
            g.drawLine(NEXT_X, NEXT_Y + i * BLOCK_SIZE, NEXT_X + 120, NEXT_Y + i * BLOCK_SIZE);
        }
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Next", 480, 140);
        if (nextPiece != null) {
            g.setColor(nextPiece.getColor());
            for (Point p : nextPiece.getShape()) {
                int drawX = NEXT_X + 30 + p.x * BLOCK_SIZE;
                int drawY = NEXT_Y + 60 + p.y * BLOCK_SIZE;
                g.fillRect(drawX, drawY, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
            }
        }


        // 繪製暫停或遊戲結束提示
        if (isPaused) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Paused", BOARD_X + WIDTH * BLOCK_SIZE / 4, BOARD_Y + 150);
        }
        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", BOARD_X + WIDTH * BLOCK_SIZE / 6, BOARD_Y + HEIGHT * BLOCK_SIZE / 2);
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

    private void hold() {
        if (hasHeld) {
            return; // 每回合限 Hold 一次
        }
        if (holdPiece == null) {
            holdPiece = currentPiece;
            currentPiece = nextPiece;
            nextPiece = createNewPiece();
        } else {
            Tetromino temp = currentPiece;
            currentPiece = holdPiece;
            holdPiece = temp;
        }
        // 重置新方塊位置和旋轉狀態
        currentPiece.position.setLocation(WIDTH / 2, 0);
        if (!(currentPiece instanceof TetrominoO)) {
            currentPiece.position.x -= 1;
        }
        currentPiece.rotationState = 0;
        hasHeld = true;
        repaint();
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
            currentPiece = nextPiece;
            nextPiece = createNewPiece();
            if (!canMove(currentPiece)) {
                isGameOver = true;
                timer.stop();
            }
            hasHeld = false; // 新回合重置 Hold 旗標
            repaint();
            return false;
        }
    }
    private void hardDrop() {
        while (currentPiece.moveDown()) {
            // 持續下移直到無法移動
        }
        placePiece();
        clearLines();
        currentPiece = nextPiece;
        nextPiece = createNewPiece();
        if (!canMove(currentPiece)) {
            isGameOver = true;
            timer.stop();
        }
        hasHeld = false; // 新回合重置 Hold 旗標
        repaint();
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
        if (currentPiece instanceof TetrominoT && lastAction.equals("rotate")) {
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