import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;

public class TetrominoI extends Tetromino {
    public TetrominoI(int[][] board) {
        super(board);
        shape = new Point[]{new Point(-1, 0), new Point(0, 0), new Point(1, 0), new Point(2, 0)};
        color = Color.CYAN;
        position.x -= 1; // 調整初始位置以居中
    }

    @Override
    public void rotateCW() {
        Point[] newShape = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            newShape[i] = new Point(-shape[i].y, shape[i].x); // 順時針 90 度
        }
        int newState = (rotationState + 1) % 4;
        if (!tryRotate(newShape, newState, I_OFFSETS[rotationState])) {
            System.err.println("Rotation CW failed for TetrominoI, shape: " + Arrays.toString(newShape));
        }
    }

    @Override
    public void rotateCCW() {
        Point[] newShape = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            newShape[i] = new Point(shape[i].y, -shape[i].x); // 逆時針 -90 度
        }
        int newState = (rotationState - 1 + 4) % 4;
        if (!tryRotate(newShape, newState, I_OFFSETS[(newState + 3) % 4])) {
            System.err.println("Rotation CCW failed for TetrominoI, shape: " + Arrays.toString(newShape));
        }
    }
}