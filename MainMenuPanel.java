import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuPanel extends JPanel {
    private static final int PANEL_WIDTH = 600;
    private static final int PANEL_HEIGHT = 700;

    public MainMenuPanel(JFrame frame) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(null); // 使用絕對佈局以精確定位

        // 標題
        JLabel titleLabel = new JLabel("Tetris", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50));
        titleLabel.setBounds(0, 100, PANEL_WIDTH, 60);
        add(titleLabel);

        // Start 按鈕
        JButton SingleplayerButton = new JButton("Single Player");
        SingleplayerButton.setForeground(Color.WHITE);
        SingleplayerButton.setBackground(Color.LIGHT_GRAY);
        SingleplayerButton.setFont(new Font("Arial", Font.BOLD, 30));
        SingleplayerButton.setBounds(150, 250, 300, 80);
        SingleplayerButton.setFocusPainted(false);
        SingleplayerButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            TetrisPanel tetrisPanel = new TetrisPanel();
            frame.add(tetrisPanel);
            frame.pack();
            tetrisPanel.requestFocusInWindow();
            frame.revalidate();
            frame.repaint();
        });
        add(SingleplayerButton);
        
        JButton TwoplayerButton = new JButton("Two Players");
        TwoplayerButton.setForeground(Color.WHITE);
        TwoplayerButton.setBackground(Color.LIGHT_GRAY);
        TwoplayerButton.setFont(new Font("Arial", Font.BOLD, 30));
        TwoplayerButton.setBounds(150, 400, 300, 80);
        TwoplayerButton.setFocusPainted(false);
        TwoplayerButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            TetrisBattlePanel tetrisPanel = new TetrisBattlePanel();
            frame.add(tetrisPanel);
            frame.pack();
            tetrisPanel.requestFocusInWindow();
            frame.revalidate();
            frame.repaint();
        });
        add(TwoplayerButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 可選：添加背景效果（例如，漸層或簡單矩形）
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
    }
}