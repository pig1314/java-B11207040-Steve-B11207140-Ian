import javax.swing.*;

public class Tetris {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tetris");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new TetrisPanel());
            frame.pack(); // 自動調整 JFrame 大小以適應 TetrisPanel
            frame.setLocationRelativeTo(null); // 視窗居中
            frame.setVisible(true);
        });
    }
}