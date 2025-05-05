import java.awt.Color;
import java.awt.Point;

public abstract class Tetromino {
    protected Point[] shape;
    protected Color color;
    protected Point position;
    protected int[][] board;
    protected static final int WIDTH = 10;
    protected static final int HEIGHT = 20;

    public Tetromino(int[][] board) {
        this.board = board;
        this.position = new Point(WIDTH / 2, 0);
    }

    public Point[] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public Point getPosition() {
        return position;
    }

    public Point[] getAbsolutePoints() {
        Point[] result = new Point[shape.length];
        for (int i = 0; i < shape.length; i++) {
            result[i] = new Point(position.x + shape[i].x, position.y + shape[i].y);
        }
        return result;
    }

    protected boolean canMove(int dx, int dy, Point[] newShape) {
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
}