import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuPanel extends JPanel {
    private static final int PANEL_WIDTH = 600;
    private static final int PANEL_HEIGHT = 710;

    public MainMenuPanel(JFrame frame) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(null); // 使用絕對佈局以精確定位

        // 標題
        JLabel titleLabel = new JLabel("Tetris", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50));
        titleLabel.setBounds(0, 50, PANEL_WIDTH, 60);
        add(titleLabel);

        // Start 按鈕
        JButton SingleplayerButton = new JButton("Arcade");
        SingleplayerButton.setForeground(Color.WHITE);
        SingleplayerButton.setBackground(Color.LIGHT_GRAY);
        SingleplayerButton.setFont(new Font("Arial", Font.BOLD, 30));
        SingleplayerButton.setBounds(150, 150, 300, 80);
        SingleplayerButton.setFocusPainted(false);
        SingleplayerButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            ArcadeTetrisPanel ArcadeTetrisPanel = new ArcadeTetrisPanel(frame);
            frame.add(ArcadeTetrisPanel);
            frame.pack();
            ArcadeTetrisPanel.requestFocusInWindow();
            frame.revalidate();
            frame.repaint();
        });
        add(SingleplayerButton);
        
        JButton ChallengeButton = new JButton("Challenge");
        ChallengeButton.setForeground(Color.WHITE);
        ChallengeButton.setBackground(Color.LIGHT_GRAY);
        ChallengeButton.setFont(new Font("Arial", Font.BOLD, 30));
        ChallengeButton.setBounds(150, 300, 300, 80);
        ChallengeButton.setFocusPainted(false);
        ChallengeButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            ChallengeTetrisPanel ChallengePanel = new ChallengeTetrisPanel(frame);
            frame.add(ChallengePanel);
            frame.pack();
            ChallengePanel.requestFocusInWindow();
            frame.revalidate();
            frame.repaint();
        });
        add(ChallengeButton);
        
        JButton TwoplayerButton = new JButton("Battle");
        TwoplayerButton.setForeground(Color.WHITE);
        TwoplayerButton.setBackground(Color.LIGHT_GRAY);
        TwoplayerButton.setFont(new Font("Arial", Font.BOLD, 30));
        TwoplayerButton.setBounds(150, 450, 300, 80);
        TwoplayerButton.setFocusPainted(false);
        TwoplayerButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            TetrisBattlePanel tetrisPanel = new TetrisBattlePanel(frame);
            frame.add(tetrisPanel);
            frame.pack();
            tetrisPanel.requestFocusInWindow();
            frame.revalidate();
            frame.repaint();
        });
        add(TwoplayerButton);
        
        JButton quitButton = new JButton("Quit");
        quitButton.setForeground(Color.WHITE);
        quitButton.setBackground(Color.LIGHT_GRAY);
        quitButton.setFont(new Font("Arial", Font.BOLD, 30));
        quitButton.setBounds(150, 600, 300, 80);
        quitButton.setFocusPainted(false);
        quitButton.addActionListener(e -> System.exit(0));
        add(quitButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 可選：添加背景效果（例如，漸層或簡單矩形）
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
    }
}