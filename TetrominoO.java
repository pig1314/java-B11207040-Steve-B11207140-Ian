import java.awt.Color;
import java.awt.Point;

public class TetrominoO extends Tetromino {
    public TetrominoO(int[][] board) {
        super(board);
        shape = new Point[]{new Point(0, 0), new Point(1, 0), new Point(0, -1), new Point(1, -1)};
        color = Color.YELLOW;
        position.x -= 1; // 調整初始位置以居中
    }

    @Override
    public void rotateCW() {
        // O 方塊不旋轉
    }

    @Override
    public void rotateCCW() {
        // O 方塊不旋轉
    }
}