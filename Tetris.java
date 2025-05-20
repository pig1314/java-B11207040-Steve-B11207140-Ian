import javax.swing.*;
import javazoom.jl.player.Player;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Random;

public class Tetris {
    private static Thread musicThread;
    private static boolean isPlayingMusic = true;
    
    public static void main(String[] args) {
        musicThread = new Thread(new Runnable() {
            public void run() {
                playMusic();
            }
        });
        musicThread.start();
        
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
    
    private static void playMusic() {
        try {
            while (isPlayingMusic) {
                FileInputStream fis = new FileInputStream("tetris.mp3");
                BufferedInputStream bis = new BufferedInputStream(fis);
                Player player = new Player(bis);
                player.play();
            }
        } catch (Exception e) {
            System.out.println("Problem playing sound file: " + e.getMessage());
        }
    }
}