import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.swing.*;

import TetrisPanel;
import javazoom.jl.player.Player;

public class Tetris extends JFrame {
    
    public Tetris() {
        setTitle("Tetris");
        setSize(10 * 30 + 16, 20 * 30 + 39);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        TetrisPanel tetrisPanel = new TetrisPanel();
        add(tetrisPanel);
        tetrisPanel.requestFocusInWindow();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Tetris();
        });
    }
}