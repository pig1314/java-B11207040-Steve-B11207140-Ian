import java.awt.Color;
import java.awt.Point;

public class TetrominoT extends Tetromino {
    public TetrominoT(int[][] board) {
        super(board);
        shape = new Point[]{new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(1, 1)};
        color = Color.MAGENTA;
        position.x -= 2; // 調整初始位置以居中
    }

    @Override
    public void rotateCW() {
        Point[] rotated = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            rotated[i] = new Point(-shape[i].y, shape[i].x); // 順時針旋轉 90 度
        }
        if (canMove(0, 0, rotated)) {
            shape = rotated;
        }
    }

    @Override
    public void rotateCCW() {
        Point[] rotated = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            rotated[i] = new Point(shape[i].y, -shape[i].x); // 逆時針旋轉 -90 度
        }
        if (canMove(0, 0, rotated)) {
            shape = rotated;
        }
    }
}