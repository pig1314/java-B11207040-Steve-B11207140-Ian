import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TetrisBattlePanel extends AbstractTetrisPanel {
    private static final int BLOCK_SIZE = 20; // 縮放至 20x20 像素
    private static final int PLAYER1_BOARD_X = 50;
    private static final int PLAYER2_BOARD_X = 350;
    private static final int BOARD_Y = 100;
    private static final int PLAYER1_SCORE_X = 50;
    private static final int PLAYER2_SCORE_X = 350;
    private static final int SCORE_Y = 20;
    private static final int PLAYER1_LEVEL_X = 50;
    private static final int PLAYER2_LEVEL_X = 350;
    private static final int LEVEL_Y = 60;
    private static final int PLAYER1_NEXT_X = 50;
    private static final int PLAYER2_NEXT_X = 350;
    private static final int NEXT_Y = 550;
    private static final int PLAYER1_HOLD_X = 150;
    private static final int PLAYER2_HOLD_X = 450;
    private static final int HOLD_Y = 550;

    private PlayerState player1;
    private PlayerState player2;
    private String winner = null;

    public TetrisBattlePanel() {
        super();
        setFocusable(true);
    }

    // 玩家狀態類，封裝遊戲板、方塊、計分等
    private class PlayerState {
        int[][] board = new int[HEIGHT][WIDTH];
        Tetromino currentPiece;
        Tetromino nextPiece;
        Tetromino holdPiece;
        boolean hasHeld = false;
        Timer timer;
        int score = 0;
        int level = 1;
        int linesCleared = 0;
        boolean isGameOver = false;
        String lastAction = "";

        PlayerState() {
            currentPiece = createNewPiece();
            nextPiece = createNewPiece();
            holdPiece = null;
            timer = new Timer(SPEED_TABLE[0], e -> moveDown(this));
            timer.start();
        }
    }

    @Override
    protected void initializeGameMode() {
        // 初始化雙玩家
        player1 = new PlayerState();
        player2 = new PlayerState();

        // 設置按鍵監聽器
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_L) {
                    togglePause();
                }
                if (!isPaused && !player1.isGameOver && !player2.isGameOver) {
                    // 玩家 1 按鍵
                    if (!player1.isGameOver) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                player1.currentPiece.move(-1, 0);
                                player1.lastAction = "left";
                                break;
                            case KeyEvent.VK_RIGHT:
                                player1.currentPiece.move(1, 0);
                                player1.lastAction = "right";
                                break;
                            case KeyEvent.VK_DOWN:
                                player1.currentPiece.moveDown();
                                player1.lastAction = "down";
                                break;
                            case KeyEvent.VK_UP:
                                player1.currentPiece.rotateCW();
                                player1.lastAction = "rotate";
                                break;
                            case KeyEvent.VK_Z:
                                player1.currentPiece.rotateCCW();
                                player1.lastAction = "rotateBack";
                                break;
                            case KeyEvent.VK_C:
                                hold(player1);
                                player1.lastAction = "hold";
                                break;
                            case KeyEvent.VK_SPACE:
                                hardDrop(player1);
                                player1.lastAction = "hardDrop";
                                break;
                        }
                    }
                    // 玩家 2 按鍵
                    if (!player2.isGameOver) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_A:
                                player2.currentPiece.move(-1, 0);
                                player2.lastAction = "left";
                                break;
                            case KeyEvent.VK_D:
                                player2.currentPiece.move(1, 0);
                                player2.lastAction = "right";
                                break;
                            case KeyEvent.VK_S:
                                player2.currentPiece.moveDown();
                                player2.lastAction = "down";
                                break;
                            case KeyEvent.VK_W:
                                player2.currentPiece.rotateCW();
                                player2.lastAction = "rotate";
                                break;
                            case KeyEvent.VK_Q:
                                player2.currentPiece.rotateCCW();
                                player2.lastAction = "rotateBack";
                                break;
                            case KeyEvent.VK_E:
                                hold(player2);
                                player2.lastAction = "hold";
                                break;
                            case KeyEvent.VK_SHIFT:
                                hardDrop(player2);
                                player2.lastAction = "hardDrop";
                                break;
                        }
                    }
                    repaint();
                }
            }
        });
    }

    @Override
    protected void restartGame() {
        player1 = new PlayerState();
        player2 = new PlayerState();
        isPaused = false;
        winner = null;
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

    @Override
    protected void togglePause() {
        if (!player1.isGameOver && !player2.isGameOver) {
            isPaused = !isPaused;
            if (isPaused) {
                player1.timer.stop();
                player2.timer.stop();
                restartButton = new JButton("Restart");
                restartButton.setForeground(Color.WHITE);
                restartButton.setBackground(Color.LIGHT_GRAY);
                restartButton.setFont(new Font("Arial", Font.BOLD, 20));
                restartButton.setBounds(240, 400, 120, 50);
                restartButton.setFocusPainted(false);
                restartButton.addActionListener(e -> restartGame());
                add(restartButton);

                quitButton = new JButton("Quit");
                quitButton.setForeground(Color.WHITE);
                quitButton.setBackground(Color.LIGHT_GRAY);
                quitButton.setFont(new Font("Arial", Font.BOLD, 20));
                quitButton.setBounds(240, 500, 120, 50);
                quitButton.setFocusPainted(false);
                quitButton.addActionListener(e -> System.exit(0));
                add(quitButton);
            } else {
                player1.timer.start();
                player2.timer.start();
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

    private void moveDown(PlayerState player) {
        if (isPaused) {
            return;
        }
        if (player.currentPiece != null && player.currentPiece.moveDown()) {
            // 繼續下移
        } else {
            placePiece(player);
            clearLines(player);
            player.currentPiece = player.nextPiece;
            player.nextPiece = createNewPiece();
            if (!canMove(player)) {
                player.isGameOver = true;
                player.timer.stop();
                determineWinner();
            }
            player.hasHeld = false;
        }
        repaint();
    }

    private boolean canMove(PlayerState player) {
        return player.currentPiece.canMove(0, 0, player.currentPiece.getShape());
    }

    private void placePiece(PlayerState player) {
        for (Point p : player.currentPiece.getAbsolutePoints()) {
            int boardX = p.x;
            int boardY = p.y;
            if (boardY >= 0) {
                player.board[boardY][boardX] = player.currentPiece.getColor().getRGB();
            }
        }
        // T-spin 檢測
        if (player.currentPiece instanceof TetrominoT && (player.lastAction.equals("rotate") || player.lastAction.equals("rotateBack"))) {
            Point center = new Point(player.currentPiece.getPosition().x + 1, player.currentPiece.getPosition().y);
            int occupiedCount = 0;
            int[] dx = {-1, 1, -1, 1};
            int[] dy = {-1, -1, 1, 1};
            for (int i = 0; i < 4; i++) {
                int checkX = center.x + dx[i];
                int checkY = center.y + dy[i];
                if (checkX < 0 || checkX >= WIDTH || checkY < 0 || checkY >= HEIGHT || (checkY >= 0 && player.board[checkY][checkX] != 0)) {
                    occupiedCount++;
                }
            }
            if (occupiedCount >= 3) {
                player.score += 100 * player.level;
            }
        }
    }

    private void clearLines(PlayerState player) {
        int linesClearedThisTurn = 0;
        for (int i = HEIGHT - 1; i >= 0; i--) {
            boolean isFull = true;
            for (int j = 0; j < WIDTH; j++) {
                if (player.board[i][j] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                for (int k = i; k > 0; k--) {
                    System.arraycopy(player.board[k-1], 0, player.board[k], 0, WIDTH);
                }
                for (int j = 0; j < WIDTH; j++) {
                    player.board[0][j] = 0;
                }
                linesClearedThisTurn++;
                i++;
            }
        }
        player.linesCleared += linesClearedThisTurn;
        switch (linesClearedThisTurn) {
            case 1: player.score += 100 * player.level; break;
            case 2: player.score += 300 * player.level; break;
            case 3: player.score += 500 * player.level; break;
            case 4: player.score += 800 * player.level; break;
        }
        // 攻擊對方
        if (linesClearedThisTurn >= 2) {
            PlayerState opponent = (player == player1) ? player2 : player1;
            addObstacleLines(opponent, linesClearedThisTurn);
        }
        int newLevel = player.linesCleared / LINES_PER_LEVEL + 1;
        if (newLevel > player.level) {
            player.level = newLevel;
            adjustGameSpeed(player);
        }
    }

    private void addObstacleLines(PlayerState opponent, int numLines) {
        Random random = new Random();
        Color[] colors = {
            new TetrominoI(opponent.board).getColor(),
            new TetrominoO(opponent.board).getColor(),
            new TetrominoT(opponent.board).getColor(),
            new TetrominoL(opponent.board).getColor(),
            new TetrominoJ(opponent.board).getColor(),
            new TetrominoS(opponent.board).getColor(),
            new TetrominoZ(opponent.board).getColor()
        };

        for (int n = 0; n < numLines; n++) {
            // 檢查頂部是否阻塞
            for (int j = 0; j < WIDTH; j++) {
                if (opponent.board[0][j] != 0) {
                    opponent.isGameOver = true;
                    opponent.timer.stop();
                    determineWinner();
                    return;
                }
            }
            // 上移遊戲板
            for (int i = 1; i < HEIGHT; i++) {
                System.arraycopy(opponent.board[i], 0, opponent.board[i-1], 0, WIDTH);
            }
            // 生成障礙行（9 個方塊，1 個隨機空格）
            int[] newLine = new int[WIDTH];
            for (int j = 0; j < WIDTH; j++) {
                newLine[j] = colors[random.nextInt(colors.length)].getRGB();
            }
            int emptyIndex = random.nextInt(WIDTH);
            newLine[emptyIndex] = 0;
            opponent.board[HEIGHT-1] = newLine;
        }

        // 檢查對方當前方塊是否可移動
        if (!opponent.currentPiece.canMove(0, 0, opponent.currentPiece.getShape())) {
            opponent.isGameOver = true;
            opponent.timer.stop();
            determineWinner();
        }
        repaint();
    }

    private void adjustGameSpeed(PlayerState player) {
        int delay = SPEED_TABLE[Math.min(player.level - 1, SPEED_TABLE.length - 1)];
        player.timer.setDelay(delay);
    }

    private void hold(PlayerState player) {
        if (player.hasHeld) {
            return;
        }
        if (player.holdPiece == null) {
            player.holdPiece = player.currentPiece;
            player.currentPiece = player.nextPiece;
            player.nextPiece = createNewPiece();
        } else {
            Tetromino temp = player.currentPiece;
            player.currentPiece = player.holdPiece;
            player.holdPiece = temp;
        }
        player.currentPiece.position.setLocation(WIDTH / 2, 0);
        if (!(player.currentPiece instanceof TetrominoO)) {
            player.currentPiece.position.x -= 1;
        }
        player.currentPiece.rotationState = 0;
        player.hasHeld = true;
        repaint();
    }

    private void hardDrop(PlayerState player) {
        while (player.currentPiece.moveDown()) {
            // 持續下移
        }
        placePiece(player);
        clearLines(player);
        player.currentPiece = player.nextPiece;
        player.nextPiece = createNewPiece();
        if (!canMove(player)) {
            player.isGameOver = true;
            player.timer.stop();
            determineWinner();
        }
        player.hasHeld = false;
        repaint();
    }

    private void determineWinner() {
        if (player1.isGameOver && !player2.isGameOver) {
            winner = "Player 2 Wins";
            player2.timer.stop();
        } else if (player2.isGameOver && !player1.isGameOver) {
            winner = "Player 1 Wins";
            player1.timer.stop();
        } else if (player1.isGameOver && player2.isGameOver) {
            winner = "Draw";
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        
        // 繪製玩家 1 遊戲板
        g.setColor(Color.DARK_GRAY);
        g.fillRect(PLAYER1_BOARD_X, BOARD_Y, WIDTH * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (player1.board[i][j] != 0) {
                    g.setColor(new Color(player1.board[i][j]));
                    g.fillRect(PLAYER1_BOARD_X + j * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }
        if (player1.currentPiece != null) {
            g.setColor(player1.currentPiece.getColor());
            for (Point p : player1.currentPiece.getAbsolutePoints()) {
                if (p.y >= 0) {
                    g.fillRect(PLAYER1_BOARD_X + p.x * BLOCK_SIZE, BOARD_Y + p.y * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }
        g.setColor(Color.WHITE);
        for (int i = 0; i <= WIDTH; i++) {
            g.drawLine(PLAYER1_BOARD_X + i * BLOCK_SIZE, BOARD_Y, PLAYER1_BOARD_X + i * BLOCK_SIZE, BOARD_Y + HEIGHT * BLOCK_SIZE);
        }
        for (int i = 0; i <= HEIGHT; i++) {
            g.drawLine(PLAYER1_BOARD_X, BOARD_Y + i * BLOCK_SIZE, PLAYER1_BOARD_X + WIDTH * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE);
        }

        // 繪製玩家 2 遊戲板
        g.setColor(Color.DARK_GRAY);
        g.fillRect(PLAYER2_BOARD_X, BOARD_Y, WIDTH * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (player2.board[i][j] != 0) {
                    g.setColor(new Color(player2.board[i][j]));
                    g.fillRect(PLAYER2_BOARD_X + j * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }
        if (player2.currentPiece != null) {
            g.setColor(player2.currentPiece.getColor());
            for (Point p : player2.currentPiece.getAbsolutePoints()) {
                if (p.y >= 0) {
                    g.fillRect(PLAYER2_BOARD_X + p.x * BLOCK_SIZE, BOARD_Y + p.y * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }
        g.setColor(Color.WHITE);
        for (int i = 0; i <= WIDTH; i++) {
            g.drawLine(PLAYER2_BOARD_X + i * BLOCK_SIZE, BOARD_Y, PLAYER2_BOARD_X + i * BLOCK_SIZE, BOARD_Y + HEIGHT * BLOCK_SIZE);
        }
        for (int i = 0; i <= HEIGHT; i++) {
            g.drawLine(PLAYER2_BOARD_X, BOARD_Y + i * BLOCK_SIZE, PLAYER2_BOARD_X + WIDTH * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE);
        }

        // 繪製計分
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(PLAYER1_SCORE_X, SCORE_Y, 100, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("P1 Score: " + player1.score, PLAYER1_SCORE_X + 10, SCORE_Y + 20);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(PLAYER2_SCORE_X, SCORE_Y, 100, 30);
        g.setColor(Color.WHITE);
        g.drawString("P2 Score: " + player2.score, PLAYER2_SCORE_X + 10, SCORE_Y + 20);

        // 繪製等級
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(PLAYER1_LEVEL_X, LEVEL_Y, 100, 30);
        g.setColor(Color.WHITE);
        g.drawString("P1 Level: " + player1.level, PLAYER1_LEVEL_X + 10, LEVEL_Y + 20);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(PLAYER2_LEVEL_X, LEVEL_Y, 100, 30);
        g.setColor(Color.WHITE);
        g.drawString("P2 Level: " + player2.level, PLAYER2_LEVEL_X + 10, LEVEL_Y + 20);

        // 繪製 Next
        g.setColor(Color.DARK_GRAY);
        g.fillRect(PLAYER1_NEXT_X, NEXT_Y, 80, 60);
        g.setColor(Color.WHITE);
        g.drawRect(PLAYER1_NEXT_X, NEXT_Y, 80, 60);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("P1 Next", PLAYER1_NEXT_X + 10, NEXT_Y - 10);
        if (player1.nextPiece != null) {
            g.setColor(player1.nextPiece.getColor());
            for (Point p : player1.nextPiece.getShape()) {
                int drawX = PLAYER1_NEXT_X + 20 + p.x * BLOCK_SIZE;
                int drawY = NEXT_Y + 20 + p.y * BLOCK_SIZE;
                g.fillRect(drawX, drawY, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
            }
        }
        g.setColor(Color.DARK_GRAY);
        g.fillRect(PLAYER2_NEXT_X, NEXT_Y, 80, 60);
        g.setColor(Color.WHITE);
        g.drawRect(PLAYER2_NEXT_X, NEXT_Y, 80, 60);
        g.drawString("P2 Next", PLAYER2_NEXT_X + 10, NEXT_Y - 10);
        if (player2.nextPiece != null) {
            g.setColor(player2.nextPiece.getColor());
            for (Point p : player2.nextPiece.getShape()) {
                int drawX = PLAYER2_NEXT_X + 20 + p.x * BLOCK_SIZE;
                int drawY = NEXT_Y + 20 + p.y * BLOCK_SIZE;
                g.fillRect(drawX, drawY, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
            }
        }

        // 繪製 Hold
        g.setColor(Color.DARK_GRAY);
        g.fillRect(PLAYER1_HOLD_X, HOLD_Y, 80, 60);
        g.setColor(Color.WHITE);
        g.drawRect(PLAYER1_HOLD_X, HOLD_Y, 80, 60);
        g.drawString("P1 Hold", PLAYER1_HOLD_X + 10, HOLD_Y - 10);
        if (player1.holdPiece != null) {
            g.setColor(player1.holdPiece.getColor());
            for (Point p : player1.holdPiece.getShape()) {
                int drawX = PLAYER1_HOLD_X + 20 + p.x * BLOCK_SIZE;
                int drawY = HOLD_Y + 20 + p.y * BLOCK_SIZE;
                g.fillRect(drawX, drawY, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
            }
        }
        g.setColor(Color.DARK_GRAY);
        g.fillRect(PLAYER2_HOLD_X, HOLD_Y, 80, 60);
        g.setColor(Color.WHITE);
        g.drawRect(PLAYER2_HOLD_X, HOLD_Y, 80, 60);
        g.drawString("P2 Hold", PLAYER2_HOLD_X + 10, HOLD_Y - 10);
        if (player2.holdPiece != null) {
            g.setColor(player2.holdPiece.getColor());
            for (Point p : player2.holdPiece.getShape()) {
                int drawX = PLAYER2_HOLD_X + 20 + p.x * BLOCK_SIZE;
                int drawY = HOLD_Y + 20 + p.y * BLOCK_SIZE;
                g.fillRect(drawX, drawY, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
            }
        }

        // 繪製暫停提示
        if (isPaused) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Paused", PANEL_WIDTH / 4, BOARD_Y + 150); 
        }

        // 繪製模式特定內容
        paintModeSpecific(g);
    }

    @Override
    protected void paintModeSpecific(Graphics g) {
        // 繪製勝者提示
        if (winner != null) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString(winner, PANEL_WIDTH / 4, BOARD_Y + HEIGHT * BLOCK_SIZE / 2);
        }
    }
}