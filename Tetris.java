import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Tetris extends JFrame {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;
    private int[][] board = new int[HEIGHT][WIDTH];
    private Tetromino currentPiece;
    private Timer timer;
    private boolean isGameOver = false;
    
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
    
    private final Color[] COLORS = {
        Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.ORANGE,
        Color.BLUE, Color.GREEN, Color.RED
    };

    class Tetromino {
        int[][] shape;
        Color color;
        int x, y;

        Tetromino(int[][] shape, Color color) {
            this.shape = shape;
            this.color = color;
            this.x = WIDTH / 2 - shape[0].length / 2;
            this.y = 0;
        }
    }

    public Tetris() {
        setTitle("Tetris");
        setLocation(100,40);
        setSize(WIDTH * BLOCK_SIZE + 20, HEIGHT * BLOCK_SIZE + 40);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        currentPiece = createNewPiece();
        
        timer = new Timer(500, e -> {
            if (!isGameOver) {
                moveDown();
                repaint();
            }
        });
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isGameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_A:
                            moveLeft();
                            break;
                        case KeyEvent.VK_D:
                            moveRight();
                            break;
                        case KeyEvent.VK_S:
                            moveDown();
                            break;
                        case KeyEvent.VK_W:
                            rotate();
                            break;
                    }
                    repaint();
                }
            }
        });
        
         setFocusable(true);//make sure that keyboard actions can be captured correctly.
    }

    private Tetromino createNewPiece() {
        Random random = new Random();
        int index = random.nextInt(SHAPES.length);
        return new Tetromino(SHAPES[index], COLORS[index]);
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
                timer.stop();
            }
            return false;
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
    }

    private void clearLines() {
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
                i++;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        //game region
        g2d.setColor(Color.BLACK);
        g2d.fillRect(10, 0, WIDTH * BLOCK_SIZE + 2, HEIGHT * BLOCK_SIZE + 1);

        //blocks past
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j] != 0) {
                    g2d.setColor(new Color(board[i][j]));
                    g2d.fillRect(j * BLOCK_SIZE + 11, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g2d.setColor(Color.GRAY);
                    g2d.drawRect(j * BLOCK_SIZE + 11, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        //blocks now
        g2d.setColor(currentPiece.color);
        for (int i = 0; i < currentPiece.shape.length; i++) {
            for (int j = 0; j < currentPiece.shape[i].length; j++) {
                if (currentPiece.shape[i][j] == 1) {
                    int drawX = (currentPiece.x + j) * BLOCK_SIZE;
                    int drawY = (currentPiece.y + i) * BLOCK_SIZE;
                    if (drawY >= 0) {
                        g2d.fillRect(drawX + 11, drawY, BLOCK_SIZE, BLOCK_SIZE);
                        g2d.setColor(Color.GRAY);
                        g2d.drawRect(drawX + 11, drawY, BLOCK_SIZE, BLOCK_SIZE);
                        g2d.setColor(currentPiece.color);
                    }
                }
            }
        }

        //gameover note
        if (isGameOver) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.drawString("Game Over", WIDTH * BLOCK_SIZE / 4, HEIGHT * BLOCK_SIZE / 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }
}