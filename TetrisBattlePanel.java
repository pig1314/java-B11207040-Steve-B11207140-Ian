import javax.swing.*;
import javazoom.jl.player.Player;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Random;

public class TetrisBattlePanel extends JPanel {
    private static final int PANEL_WIDTH = 1200;
    private static final int PANEL_HEIGHT = 710;
    private Thread musicThread;
    private boolean isPlayingMusic = true;
    private PlayerBoard player1Board;
    private PlayerBoard player2Board;

    public TetrisBattlePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(null);

        // 初始化兩個玩家的棋盤
        player1Board = new PlayerBoard(0, this, 1);
        player2Board = new PlayerBoard(600, this, 2);
        player1Board.setOpponent(player2Board);
        player2Board.setOpponent(player1Board);

        add(player1Board);
        add(player2Board);

        // 設置鍵盤綁定
        setupKeyBindings();

        // 確保面板可以接收鍵盤輸入
        setFocusable(true);
        requestFocusInWindow();
        musicThread = new Thread(new Runnable() {
        public void run() {
            playMusic();
        }
        });
        musicThread.start();
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

    private void setupKeyBindings() {
        // Player 1 鍵盤綁定 (WASD, E, F, Space)
        bindKey("W", "player1.rotateCW", () -> player1Board.rotateCW());
        bindKey("E", "player1.rotateCCW", () -> player1Board.rotateCCW());
        bindKey("A", "player1.moveLeft", () -> player1Board.move(-1, 0));
        bindKey("D", "player1.moveRight", () -> player1Board.move(1, 0));
        bindKey("S", "player1.moveDown", () -> player1Board.moveDown());
        bindKey("C", "player1.hold", () -> player1Board.hold());
        bindKey("SPACE", "player1.hardDrop", () -> player1Board.hardDrop());

        // Player 2 鍵盤綁定 (箭頭鍵, Z, C, Space)
        bindKey("UP", "player2.rotateCW", () -> player2Board.rotateCW());
        bindKey("B", "player2.rotateCCW", () -> player2Board.rotateCCW());
        bindKey("LEFT", "player2.moveLeft", () -> player2Board.move(-1, 0));
        bindKey("RIGHT", "player2.moveRight", () -> player2Board.move(1, 0));
        bindKey("DOWN", "player2.moveDown", () -> player2Board.moveDown());
        bindKey("N", "player2.hold", () -> player2Board.hold());
        bindKey("M", "player2.hardDrop", () -> player2Board.hardDrop());
    }

    private void bindKey(String key, String actionName, Runnable action) {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(key), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
                player1Board.repaint();
                player2Board.repaint();
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    private class PlayerBoard extends JPanel {
        private static final int WIDTH = 10;
        private static final int HEIGHT = 20;
        private static final int BLOCK_SIZE = 30;
        private static final int BOARD_WIDTH = 600;
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
        private int level = 1;
        private int linesCleared = 0;
        private static final int LINES_PER_LEVEL = 10;
        private int score = 0;
        private String lastAction = "";
        private final int playerNumber;
        private final TetrisBattlePanel parentPanel;
        private PlayerBoard opponent;
        private static final Color OBSTACLE_COLOR = Color.GRAY;
        private static PlayerBoard loser = null; // 跟踪哪個玩家輸了

        private static final int[] SPEED_TABLE = {
            800, 716, 633, 550, 466, 383, 300, 216, 133, 100, 83, 83, 83, 66, 50
        };

        public PlayerBoard(int xOffset, TetrisBattlePanel parent, int playerNumber) {
            this.parentPanel = parent;
            this.playerNumber = playerNumber;
            setBounds(xOffset, 0, BOARD_WIDTH, PANEL_HEIGHT);
            setBackground(Color.BLACK);

            currentPiece = createNewPiece();
            nextPiece = createNewPiece();
            holdPiece = null;

            timer = new Timer(SPEED_TABLE[0], e -> {
                if (!isGameOver) {
                    moveDown();
                    repaint();
                }
            });
            timer.start();
        }

        public void setOpponent(PlayerBoard opponent) {
            this.opponent = opponent;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(BOARD_X, BOARD_Y, WIDTH * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);
            for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    if (board[i][j] != 0) {
                        g.setColor(new Color(board[i][j]));
                        g.fillRect(BOARD_X + j * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                        g.setColor(Color.GRAY);
                        g.drawRect(BOARD_X + j * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
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
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i <= WIDTH; i++) {
                g.drawLine(BOARD_X + i * BLOCK_SIZE, BOARD_Y, BOARD_X + i * BLOCK_SIZE, BOARD_Y + HEIGHT * BLOCK_SIZE);
            }
            for (int i = 0; i <= HEIGHT; i++) {
                g.drawLine(BOARD_X, BOARD_Y + i * BLOCK_SIZE, BOARD_X + WIDTH * BLOCK_SIZE, BOARD_Y + i * BLOCK_SIZE);
            }
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(50, 20, 150, 50);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 60, 50);
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(430, 20, 120, 50);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Level: " + level, 440, 50);
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
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Player " + playerNumber, 250, 50);
            if (loser == this) {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("Game Over", BOARD_X + WIDTH * BLOCK_SIZE / 6, BOARD_Y + HEIGHT * BLOCK_SIZE / 2);
            } else if (loser == opponent) {
                g.setColor(Color.GREEN);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("Winner!", BOARD_X + WIDTH * BLOCK_SIZE / 6, BOARD_Y + HEIGHT * BLOCK_SIZE / 2);
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
        
        public void move(int dx, int dy) {
            if (!isGameOver) {
                currentPiece.move(dx, dy);
                lastAction = dx < 0 ? "left" : dx > 0 ? "right" : "down";
            }
        }

        public void rotateCW() {
            if (!isGameOver) {
                currentPiece.rotateCW();
                lastAction = "rotate";
            }
        }

        public void rotateCCW() {
            if (!isGameOver) {
                currentPiece.rotateCCW();
                lastAction = "rotate";
            }
        }

        public void hold() {
            if (!isGameOver && !hasHeld) {
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
                lastAction = "hold";
            }
        }

        public void moveDown() {
            if (isGameOver) return;
            if (currentPiece != null && currentPiece.moveDown()) {
                lastAction = "down";
            } else {
                placePiece();
                int linesClearedThisTurn = clearLines();
                if (linesClearedThisTurn > 0) {
                    opponent.addObstacleRow(linesClearedThisTurn);
                }
                currentPiece = nextPiece;
                nextPiece = createNewPiece();
                if (!canMove(currentPiece)) {
                    loser = this; // 標記當前玩家為輸家
                    isGameOver = true;
                    timer.stop();
                    opponent.isGameOver = true;
                    opponent.timer.stop();
                }
                hasHeld = false;
            }
        }

        public void hardDrop() {
            if (!isGameOver) {
                while (currentPiece.moveDown()) {
                }
                placePiece();
                int linesClearedThisTurn = clearLines();
                if (linesClearedThisTurn > 0) {
                    opponent.addObstacleRow(linesClearedThisTurn);
                }
                currentPiece = nextPiece;
                nextPiece = createNewPiece();
                if (!canMove(currentPiece)) {
                    loser = this; // 標記當前玩家為輸家
                    isGameOver = true;
                    timer.stop();
                    opponent.isGameOver = true;
                    opponent.timer.stop();
                }
                hasHeld = false;
                lastAction = "hardDrop";
            }
        }

        private boolean canMove(Tetromino piece) {
            return piece.canMove(0, 0, piece.getShape());
        }

        private void placePiece() {
            for (Point p : currentPiece.getAbsolutePoints()) {
                int boardX = p.x;
                int boardY = p.y;
                if (boardY >= 0) {
                    board[boardY][boardX] = currentPiece.getColor().getRGB();
                }
            }
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
                    score += 100 * level;
                }
            }
        }

        private int clearLines() {
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
            return linesClearedThisTurn;
        }

        private void adjustGameSpeed() {
            int delay = SPEED_TABLE[Math.min(level - 1, SPEED_TABLE.length - 1)];
            timer.setDelay(delay);
        }

        private void addObstacleRow(int lines) {
            for (int l = 0; l < lines; l++) {
                // 將所有現有方塊上移一行
                for (int i = 0; i < HEIGHT - 1; i++) {
                    System.arraycopy(board[i + 1], 0, board[i], 0, WIDTH);
                }
                // 在底部添加一排障礙物，隨機留一個空位
                Random random = new Random();
                int emptySlot = random.nextInt(WIDTH);
                for (int j = 0; j < WIDTH; j++) {
                    if (j == emptySlot) {
                        board[HEIGHT - 1][j] = 0;
                    } else {
                        board[HEIGHT - 1][j] = OBSTACLE_COLOR.getRGB();
                    }
                }
            }
            // 重置當前方塊到初始位置
            currentPiece.position.setLocation(WIDTH / 2, 0);
            if (!(currentPiece instanceof TetrominoO)) {
                currentPiece.position.x -= 1;
            }
            currentPiece.rotationState = 0;

            // 檢查當前方塊是否合法
            if (!canMove(currentPiece)) {
                // 嘗試不同位置
                boolean validPositionFound = false;
                for (int y = 0; y < 4; y++) {
                    currentPiece.position.y = y;
                    for (int x = 0; x < WIDTH - 3; x++) {
                        currentPiece.position.x = x;
                        if (canMove(currentPiece)) {
                            validPositionFound = true;
                            break;
                        }
                    }
                    if (validPositionFound) break;
                }
                if (!validPositionFound) {
                    loser = this; // 標記當前玩家為輸家
                    isGameOver = true;
                    timer.stop();
                    opponent.isGameOver = true;
                    opponent.timer.stop();
                }
            }
            repaint();
        }
    }
}