import javax.swing.*;
import javazoom.jl.player.Player;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Random;

public abstract class AbstractTetrisPanel extends JPanel {
    protected static final int WIDTH = 10;
    protected static final int HEIGHT = 20;
    protected static final int BLOCK_SIZE = 30;
    protected static final int PANEL_WIDTH = 600;
    protected static final int PANEL_HEIGHT = 710;
    protected static final int BOARD_X = 150;
    protected static final int BOARD_Y = 100;
    protected static final int HOLD_X = 20;
    protected static final int HOLD_Y = 150;
    protected static final int NEXT_X = 460;
    protected static final int NEXT_Y = 150;
    protected static final int[] SPEED_TABLE = {
        800, 716, 633, 550, 466, 383, 300, 216, 133, 100, 83, 83, 83, 66, 50
    };
    protected static final int LINES_PER_LEVEL = 10;

    protected int[][] board = new int[HEIGHT][WIDTH];
    protected Tetromino currentPiece;
    protected Tetromino nextPiece;
    protected Tetromino holdPiece;
    protected boolean hasHeld = false;
    protected Timer timer;
    protected boolean isGameOver = false;
    protected boolean isPaused = false;
    protected boolean isPlayingMusic = true;
    protected int score = 0;
    protected int level = 1;
    protected int linesCleared = 0;
    protected String lastAction = "";
    protected Thread musicThread;
    protected JButton restartButton;
    protected JButton quitButton;

    public AbstractTetrisPanel(JFrame frame) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        setLayout(null); // 使用絕對佈局以定位按鈕

        // 初始化遊戲模式特定邏輯
        initializeGameMode(frame);

        // 初始化共用遊戲狀態
        currentPiece = createNewPiece();
        nextPiece = createNewPiece();
        holdPiece = null;
        timer = new Timer(SPEED_TABLE[0], e -> {
               if(isGameOver)
               {
                   timer.stop();
                   restartButton = new JButton("Restart");
                   restartButton.setForeground(Color.WHITE);
                   restartButton.setBackground(Color.LIGHT_GRAY);
                   restartButton.setFont(new Font("Arial", Font.BOLD, 20));
                   restartButton.setBounds(BOARD_X + 90, BOARD_Y + 250, 120, 50);
                   restartButton.setFocusPainted(false);
                   restartButton.addActionListener(eE -> restartGame());
                   add(restartButton);
         
                   quitButton = new JButton("Menu");
                   quitButton.setForeground(Color.WHITE);
                   quitButton.setBackground(Color.LIGHT_GRAY);
                   quitButton.setFont(new Font("Arial", Font.BOLD, 20));
                   quitButton.setBounds(BOARD_X + 90, BOARD_Y + 350, 120, 50);
                   quitButton.setFocusPainted(false);
                   quitButton.addActionListener(eE -> {
                     frame.getContentPane().removeAll();
                     MainMenuPanel MainPanel = new MainMenuPanel(frame);
                     frame.add(MainPanel);
                     frame.pack();
                     MainPanel.requestFocusInWindow();
                     frame.revalidate();
                     frame.repaint();
                  });
                   add(quitButton);
                   repaint();
                }
                else
                {
                   moveDown();
                   repaint();
                }
        });
        /*musicThread = new Thread(new Runnable() {
            public void run() {
                playMusic();
            }
        });
        musicThread.start();*/
        timer.start();

        // 設置按鍵監聽器
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    togglePause(frame);
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
                            currentPiece.moveDown();
                            lastAction = "down";
                            break;
                        case KeyEvent.VK_UP:
                            currentPiece.rotateCW();
                            lastAction = "rotate";
                            break;
                        case KeyEvent.VK_Z:
                            currentPiece.rotateCCW();
                            lastAction = "rotateBack";
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
    }

    // 抽象方法，子類必須實現模式特定初始化
    protected abstract void initializeGameMode(JFrame frame);

    // 重置遊戲狀態
    protected void restartGame() {
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
        requestFocusInWindow();
    }

    // 切換暫停狀態
    protected void togglePause(JFrame frame) {
        if (!isGameOver) {
            isPaused = !isPaused;
            if (isPaused) {
                timer.stop();
                restartButton = new JButton("Restart");
                restartButton.setForeground(Color.WHITE);
                restartButton.setBackground(Color.LIGHT_GRAY);
                restartButton.setFont(new Font("Arial", Font.BOLD, 20));
                restartButton.setBounds(BOARD_X + 90, BOARD_Y + 250, 120, 50);
                restartButton.setFocusPainted(false);
                restartButton.addActionListener(e -> restartGame());
                add(restartButton);

                quitButton = new JButton("Menu");
                quitButton.setForeground(Color.WHITE);
                quitButton.setBackground(Color.LIGHT_GRAY);
                quitButton.setFont(new Font("Arial", Font.BOLD, 20));
                quitButton.setBounds(BOARD_X + 90, BOARD_Y + 350, 120, 50);
                quitButton.setFocusPainted(false);
                quitButton.addActionListener(e -> {
                  frame.getContentPane().removeAll();
                  MainMenuPanel MainPanel = new MainMenuPanel(frame);
                  frame.add(MainPanel);
                  frame.pack();
                  MainPanel.requestFocusInWindow();
                  frame.revalidate();
                  frame.repaint();
                });
                add(quitButton);
            } else {
                timer.start();
                if (restartButton != null) {
                    remove(restartButton);
                    restartButton = null;
                }
                if (quitButton != null) {
                    remove(quitButton);
                    quitButton = null;
                }
                requestFocusInWindow();
            }
            revalidate();
            repaint();
        }
    }

    // 生成新方塊
    protected Tetromino createNewPiece() {
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

    // Hold 功能
    protected void hold() {
        if (hasHeld) {
            return;
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
        currentPiece.position.setLocation(WIDTH / 2, 0);
        if (!(currentPiece instanceof TetrominoO)) {
            currentPiece.position.x -= 1;
        }
        currentPiece.rotationState = 0;
        hasHeld = true;
        repaint();
    }

    // 硬降（Hard Drop）
    protected void hardDrop() {
        while (currentPiece.moveDown()) {
            // 持續下移直到無法移動
        }
        placePiece();
        clearLines();
        currentPiece = nextPiece;
        nextPiece = createNewPiece();
        if (!canMove(currentPiece)) {
            isGameOver = true;
            //timer.stop();
        }
        hasHeld = false;
        repaint();
    }

    // 方塊自動下移
    protected boolean moveDown() {
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
                //timer.stop();
            }
            hasHeld = false;
            repaint();
            return false;
        }
    }

    // 檢查方塊是否可移動
    protected boolean canMove(Tetromino piece) {
        return piece.canMove(0, 0, piece.getShape());
    }

    // 放置方塊到遊戲板
    protected void placePiece() {
        for (Point p : currentPiece.getAbsolutePoints()) {
            int boardX = p.x;
            int boardY = p.y;
            if (boardY >= 0) {
                board[boardY][boardX] = currentPiece.getColor().getRGB();
            }
        }
        // T-spin 檢測
        if (currentPiece instanceof TetrominoT && (lastAction.equals("rotate") || lastAction.equals("rotateBack"))) {
            Point center = new Point(currentPiece.getPosition().x + 1, currentPiece.getPosition().y);
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
                score += 100 * level;
            }
        }
    }

    // 消除完成的行並計分
    protected void clearLines() {
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
                for (int k = i; k > 0; k--) {
                    System.arraycopy(board[k-1], 0, board[k], 0, WIDTH);
                }
                for (int j = 0; j < WIDTH; j++) {
                    board[0][j] = 0;
                }
                linesClearedThisTurn++;
                i++;
            }
        }
        linesCleared += linesClearedThisTurn;
        switch (linesClearedThisTurn) {
            case 1: score += 100 * level; break;
            case 2: score += 300 * level; break;
            case 3: score += 500 * level; break;
            case 4: score += 800 * level; break;
        }
        int newLevel = linesCleared / LINES_PER_LEVEL + 1;
        if (newLevel > level) {
            level = newLevel;
            adjustGameSpeed();
        }
    }

    // 調整遊戲速度
    protected void adjustGameSpeed() {
        int delay = SPEED_TABLE[Math.min(level - 1, SPEED_TABLE.length - 1)];
        timer.setDelay(delay);
    }

    // 繪製 UI
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 繪製遊戲板背景
        g.setColor(Color.DARK_GRAY);
        g.fillRect(BOARD_X, BOARD_Y, WIDTH * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);

        // 繪製遊戲板方塊
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j] != 0) {
                    g.setColor(new Color(board[i][j]));
                    g.fillRect(BOARD_X + j * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }
        if (currentPiece != null) {
            g.setColor(currentPiece.getColor());
            for (Point p : currentPiece.getAbsolutePoints()) {
                if (p.y >= 0) {
                    g.fillRect(BOARD_X + p.x * BLOCK_SIZE, BOARD_Y + p.y * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }

        // 繪製遊戲板格子線
        g.setColor(Color.WHITE);
        for (int i = 0; i <= WIDTH; i++) {
            g.drawLine(BOARD_X + i * BLOCK_SIZE, BOARD_Y, BOARD_X + i * BLOCK_SIZE, BOARD_Y + HEIGHT * BLOCK_SIZE);
        }
        for (int i = 0; i <= HEIGHT; i++) {
            g.drawLine(BOARD_X, BOARD_Y + i * BLOCK_SIZE, BOARD_X + WIDTH * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE);
        }

        // 繪製計分框
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(50, 20, 150, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 60, 50);

        // 繪製等級框
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(430, 20, 120, 50);
        g.setColor(Color.WHITE);
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
            g.drawString("Game Over", BOARD_X + WIDTH * BLOCK_SIZE / 6, BOARD_Y + HEIGHT * BLOCK_SIZE / 3);
        }

        // 允許子類繪製模式特定 UI
        paintModeSpecific(g);
    }

    // 鉤子方法，子類可覆寫以繪製模式特定 UI
    protected void paintModeSpecific(Graphics g) {
        // 默認無額外繪製
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
}