import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ChallengeTetrisPanel extends AbstractTetrisPanel {
    private static final int[] OBSTACLE_INTERVAL_TABLE = {
        10000, 9600, 9200, 8800, 8400, 8000, 7600, 7200, 6800, 6400,
        6000, 5600, 5200, 4800, 4400 // 等級 1-15，15 級後固定 2000ms
    };
    private Timer obstacleTimer;
    private Random random;

    public ChallengeTetrisPanel() {
        super();
        random = new Random();
    }

    @Override
    protected void initializeGameMode() {
        // 初始化挑戰模式特定邏輯
        obstacleTimer = new Timer(OBSTACLE_INTERVAL_TABLE[0], e -> addObstacleLine());
        obstacleTimer.start();
    }

    // 添加障礙行並上移遊戲板，每次只有一個隨機空格
    private void addObstacleLine() {
        if (isPaused || isGameOver) {
            return;
        }

        // 檢查頂部是否阻塞
        for (int j = 0; j < WIDTH; j++) {
            if (board[0][j] != 0) {
                isGameOver = true;
                timer.stop();
                obstacleTimer.stop();
                repaint();
                return;
            }
        }

        // 上移遊戲板（從第 1 行到第 19 行）
        for (int i = 1; i < HEIGHT; i++) {
            System.arraycopy(board[i], 0, board[i-1], 0, WIDTH);
        }

        // 生成障礙行（底部，board[19]，9 個方塊，1 個隨機空格）
        int[] newLine = new int[WIDTH];
        Color[] colors = {
            Color.GRAY
        };
        // 先填充所有位置
        for (int j = 0; j < WIDTH; j++) {
            newLine[j] = colors[0].getRGB();
        }
        // 隨機選擇一個位置設為空格
        int emptyIndex = random.nextInt(WIDTH);
        newLine[emptyIndex] = 0;
        board[HEIGHT-1] = newLine;

        // 檢查當前方塊是否仍可移動
        if (!canMove(currentPiece)) {
            isGameOver = true;
            timer.stop();
            obstacleTimer.stop();
        }

        repaint();
    }

    @Override
    protected void restartGame() {
        super.restartGame();
        // 重置障礙計時器
        obstacleTimer.setDelay(OBSTACLE_INTERVAL_TABLE[0]);
        obstacleTimer.start();
    }

    @Override
    protected void togglePause() {
        super.togglePause();
        // 同步障礙計時器
        if (isPaused) {
            obstacleTimer.stop();
        } else {
            obstacleTimer.start();
        }
    }

    @Override
    protected void adjustGameSpeed() {
        super.adjustGameSpeed();
        // 調整障礙生成頻率
        int delay = OBSTACLE_INTERVAL_TABLE[Math.min(level - 1, OBSTACLE_INTERVAL_TABLE.length - 1)];
        obstacleTimer.setDelay(delay);
    }
}