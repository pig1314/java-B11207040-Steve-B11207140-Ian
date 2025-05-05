import java.awt.Color;
import java.awt.Point;

public class TetrominoT extends Tetromino {
    public TetrominoT(int[][] board) {
        super(board);
        shape = new Point[]{new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(0, 1)};
        color = Color.MAGENTA;
        position.x -= 1; // 調整初始位置以居中
    }

    @Override
    public void rotateCW() {
        Point[] newShape = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            newShape[i] = new Point(-shape[i].y, shape[i].x);
        }
        int newState = (rotationState + 1) % 4;
        tryRotate(newShape, newState, STANDARD_OFFSETS[rotationState]);
    }

    @Override
    public void rotateCCW() {
        Point[] newShape = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            newShape[i] = new Point(shape[i].y, -shape[i].x);
        }
        int newState = (rotationState - 1 + 4) % 4;
        tryRotate(newShape, newState, STANDARD_OFFSETS[(newState + 3) % 4]);
    }
}