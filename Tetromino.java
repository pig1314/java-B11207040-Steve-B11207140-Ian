import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;

public abstract class Tetromino {
    protected Point[] shape;
    protected Color color;
    protected Point position;
    protected int[][] board;
    protected int rotationState; // 0, 1 (R), 2, 3 (L)
    protected static final int WIDTH = 10;
    protected static final int HEIGHT = 20;

    // SRS 偏移表（非 I 方塊）
    protected static final Point[][] STANDARD_OFFSETS = {
        {new Point(0, 0), new Point(-1, 0), new Point(-1, 1), new Point(0, -2), new Point(-1, -2)}, // 0->R
        {new Point(0, 0), new Point(1, 0), new Point(1, -1), new Point(0, 2), new Point(1, 2)},   // R->2
        {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, -2), new Point(1, -2)},  // 2->L
        {new Point(0, 0), new Point(-1, 0), new Point(-1, -1), new Point(0, 2), new Point(-1, 2)} // L->0
    };

    // SRS 偏移表（I 方塊）
    protected static final Point[][] I_OFFSETS = {
        {new Point(0, 0), new Point(-1, 0), new Point(2, 0), new Point(-1, 2), new Point(2, -1)}, // 0->R
        {new Point(0, 0), new Point(-2, 0), new Point(1, 0), new Point(-2, -1), new Point(1, 2)}, // R->2
        {new Point(0, 0), new Point(2, 0), new Point(-1, 0), new Point(2, 1), new Point(-1, -2)}, // 2->L
        {new Point(0, 0), new Point(1, 0), new Point(-2, 0), new Point(1, -2), new Point(-2, 1)}  // L->0
    };

    public Tetromino(int[][] board) {
        this.board = board;
        this.position = new Point(WIDTH / 2, 0);
        this.rotationState = 0;
    }

    public Point[] getShape() {
        if (shape == null || shape.length != 4 || containsNull(shape)) {
            System.err.println("Invalid shape in " + this.getClass().getSimpleName() + ": " + Arrays.toString(shape) + ", returning default");
            return new Point[]{new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1)};
        }
        return shape;
    }

    private boolean containsNull(Point[] points) {
        for (Point p : points) {
            if (p == null) return true;
        }
        return false;
    }

    public Color getColor() {
        return color;
    }

    public Point getPosition() {
        return position;
    }

    public int getRotationState() {
        return rotationState;
    }

    public Point[] getAbsolutePoints() {
        Point[] shape = getShape();
        Point[] result = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            result[i] = new Point(position.x + shape[i].x, position.y + shape[i].y);
        }
        return result;
    }

    protected boolean canMove(int dx, int dy, Point[] newShape) {
        if (newShape == null || newShape.length != 4 || containsNull(newShape)) {
            System.err.println("Invalid newShape in canMove for " + this.getClass().getSimpleName() + ": " + Arrays.toString(newShape));
            return false;
        }
        for (Point p : newShape) {
            int boardX = position.x + p.x + dx;
            int boardY = position.y + p.y + dy;
            if (boardX < 0 || boardX >= WIDTH || boardY >= HEIGHT || (boardY >= 0 && board[boardY][boardX] != 0)) {
                return false;
            }
        }
        return true;
    }

    public void move(int dx, int dy) {
        if (canMove(dx, dy, shape)) {
            position.translate(dx, dy);
        }
    }

    public boolean moveDown() {
        if (canMove(0, 1, shape)) {
            position.translate(0, 1);
            return true;
        }
        return false;
    }

    public abstract void rotateCW();
    public abstract void rotateCCW();

    protected boolean tryRotate(Point[] newShape, int newState, Point[] offsets) {
        if (newShape == null || newShape.length != 4 || containsNull(newShape)) {
            System.err.println("Invalid newShape in tryRotate for " + this.getClass().getSimpleName() + ": " + Arrays.toString(newShape));
            return false;
        }
        for (Point offset : offsets) {
            if (canMove(offset.x, offset.y, newShape)) {
                position.translate(offset.x, offset.y);
                shape = newShape;
                rotationState = newState;
                return true;
            }
        }
        return false;
    }
}