import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuPanel extends JPanel {
    private static final int PANEL_WIDTH = 600;
    private static final int PANEL_HEIGHT = 700;
    private JButton startButton;
    private JButton arcadeButton;
    private JButton challengeButton;
    private JButton battleButton;
    private JLabel titleLabel;

    public MainMenuPanel(JFrame frame) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setLayout(null); // 使用絕對佈局以精確定位

        // 標題
        titleLabel = new JLabel("Tetris", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50));
        titleLabel.setBounds(0, 100, PANEL_WIDTH, 60);
        add(titleLabel);

        // Start 按鈕
        startButton = new JButton("Start");
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Color.LIGHT_GRAY);
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setBounds(200, 400, 200, 80);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> showGameModeButtons());
        add(startButton);
        JButton SingleplayerButton = new JButton("Single Player");
        JButton SingleplayerButton = new JButton("Arcane");
        SingleplayerButton.setForeground(Color.WHITE);
        SingleplayerButton.setBackground(Color.LIGHT_GRAY);
        SingleplayerButton.setFont(new Font("Arial", Font.BOLD, 30));
        SingleplayerButton.setBounds(150, 250, 300, 80);
        SingleplayerButton.setFocusPainted(false);
        SingleplayerButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            ArcadeTetrisPanel ArcadeTetrisPanel = new ArcadeTetrisPanel();
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
        ChallengeButton.setBounds(150, 400, 300, 80);
        ChallengeButton.setFocusPainted(false);
        ChallengeButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            ChallengeTetrisPanel ChallengePanel = new ChallengeTetrisPanel();
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
        TwoplayerButton.setBounds(150, 550, 300, 80);
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
>>>>>>> a1e027c12f50807caa8170c678372561ac5b408d
    }

    private void showGameModeButtons() {
        // 移除 Start 按鈕和原標題
        remove(startButton);
        startButton = null;
        remove(titleLabel);

        // 添加新標題 "Game mode"
        titleLabel = new JLabel("Game mode", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50));
        titleLabel.setBounds(0, 200, PANEL_WIDTH, 60);
        add(titleLabel);

        // 添加遊戲模式按鈕
        arcadeButton = new JButton("Arcade");
        arcadeButton.setForeground(Color.WHITE);
        arcadeButton.setBackground(Color.LIGHT_GRAY);
        arcadeButton.setFont(new Font("Arial", Font.BOLD, 20));
        arcadeButton.setBounds(200, 300, 200, 80);
        arcadeButton.setFocusPainted(false);
        arcadeButton.addActionListener(e -> startGame((JFrame) SwingUtilities.getWindowAncestor(this), "Arcade"));
        add(arcadeButton);

        challengeButton = new JButton("Challenge");
        challengeButton.setForeground(Color.WHITE);
        challengeButton.setBackground(Color.LIGHT_GRAY);
        challengeButton.setFont(new Font("Arial", Font.BOLD, 20));
        challengeButton.setBounds(200, 400, 200, 80);
        challengeButton.setFocusPainted(false);
        challengeButton.addActionListener(e -> startGame((JFrame) SwingUtilities.getWindowAncestor(this), "Challenge"));
        add(challengeButton);

        battleButton = new JButton("Battle");
        battleButton.setForeground(Color.WHITE);
        battleButton.setBackground(Color.LIGHT_GRAY);
        battleButton.setFont(new Font("Arial", Font.BOLD, 20));
        battleButton.setBounds(200, 500, 200, 80);
        battleButton.setFocusPainted(false);
        battleButton.addActionListener(e -> startGame((JFrame) SwingUtilities.getWindowAncestor(this), "Battle"));
        add(battleButton);

        revalidate();
        repaint();
    }

    private void startGame(JFrame frame, String mode) {
        frame.getContentPane().removeAll();
        TetrisPanel tetrisPanel = new TetrisPanel();
        frame.add(tetrisPanel);
        frame.pack();
        tetrisPanel.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
        // 未來可根據 mode 傳遞不同遊戲模式參數
        System.out.println("Starting " + mode + " mode"); // 暫時打印，未來實現不同模式
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
    }
}