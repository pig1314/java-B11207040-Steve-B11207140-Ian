import javax.swing.*;

public class Tetris {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tetris");
            frame.setLocation(0, 0);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new MainMenuPanel(frame));
            frame.pack();
            //frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}