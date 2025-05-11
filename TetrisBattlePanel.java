import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TetrisBattlePanel extends AbstractTetrisPanel {
    private static final int PANEL_WIDTH = 1200;
    private static final int PANEL_HEIGHT = 710;
    private PlayerBoard player1Board;
    private PlayerBoard player2Board;

    public TetrisBattlePanel() {
        super();
        setupPanel();
        setupKeyBindings();
        System.out.println("TetrisBattlePanel initialized: " + PANEL_WIDTH + "x" + PANEL_HEIGHT);
    }

    @Override
    protected void setupPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setBackground(Color.DARK_GRAY);

        player1Board = new PlayerBoard(0, this, 1);
        player2Board = new PlayerBoard(600, this, 2);
        player1Board.setOpponent(player2Board);
        player2Board.setOpponent(player1Board);

        add(player1Board);
        add(player2Board);
        System.out.println("Player boards added: P1 at (0,0,600x710), P2 at (600,0,600x710)");
    }

    @Override
    protected void setupKeyBindings() {
        // Player 1 controls
        bindKey("W", "player1.rotateCW", () -> player1Board.rotateCW());
        bindKey("E", "player1.rotateCCW", () -> player1Board.rotateCCW());
        bindKey("A", "player1.moveLeft", () -> player1Board.move(-1, 0));
        bindKey("D", "player1.moveRight", () -> player1Board.move(1, 0));
        bindKey("S", "player1.moveDown", () -> player1Board.moveDown());
        bindKey("C", "player1.hold", () -> player1Board.hold());
        bindKey("SPACE", "player1.hardDrop", () -> player1Board.hardDrop());
        bindKey("M", "player1.toggleMute", this::toggleMute);

        // Player 2 controls
        bindKey("UP", "player2.rotateCW", () -> player2Board.rotateCW());
        bindKey("B", "player2.rotateCCW", () -> player2Board.rotateCCW());
        bindKey("LEFT", "player2.moveLeft", () -> player2Board.move(-1, 0));
        bindKey("RIGHT", "player2.moveRight", () -> player2Board.move(1, 0));
        bindKey("DOWN", "player2.moveDown", () -> player2Board.moveDown());
        bindKey("N", "player2.hold", () -> player2Board.hold());
        bindKey("M", "player2.hardDrop", () -> player2Board.hardDrop());
        bindKey("V", "player2.toggleMute", this::toggleMute);
    }

    private void bindKey(String key, String actionName, Runnable action) {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(key), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    action.run();
                    player1Board.repaint();
                    player2Board.repaint();
                    System.out.println("Key action: " + actionName);
                } catch (Exception ex) {
                    System.err.println("Error in key action " + actionName + ": " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    protected Tetromino createNewPiece() {
        Tetromino piece = super.createNewPiece();
        System.out.println("Created piece: " + piece.getClass().getSimpleName() + ", shape.length: " + (piece.getShape() != null ? piece.getShape().length : "null"));
        return piece;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
        System.out.println("TetrisBattlePanel gained focus");
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        stopMusic();
        System.out.println("TetrisBattlePanel removed, music stopped");
    }

    private class PlayerBoard extends JPanel {
        private final int playerNumber;
        private final TetrisBattlePanel parentPanel;
        private PlayerBoard opponent;
        private static PlayerBoard loser = null;
        private boolean isGameOver = false;
        private Timer timer;
        private final int xOffset;

        public PlayerBoard(int xOffset, TetrisBattlePanel parent, int playerNumber) {
            this.parentPanel = parent;
            this.playerNumber = playerNumber;
            this.xOffset = xOffset;
            setBounds(xOffset, 0, 600, PANEL_HEIGHT);
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createLineBorder(playerNumber == 1 ? Color.BLUE : Color.RED));

            board = new int[HEIGHT][WIDTH];
            try {
                currentPiece = createNewPiece();
                nextPiece = createNewPiece();
                holdPiece = null;
            } catch (Exception e) {
                System.err.println("Error initializing pieces for Player " + playerNumber + ": " + e.getMessage());
                e.printStackTrace();
            }

            timer = new Timer(SPEED_TABLE[0], new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!isGameOver) {
                        try {
                            moveDown();
                            repaint();
                            System.out.println("Player " + playerNumber + " timer tick");
                        } catch (Exception ex) {
                            System.err.println("Error in timer for Player " + playerNumber + ": " + ex.getMessage());
                            ex.printStackTrace();
                            timer.stop();
                        }
                    }
                }
            });
            timer.start();
            System.out.println("PlayerBoard " + playerNumber + " initialized at (" + xOffset + ",0,600x710)");
        }

        public void setOpponent(PlayerBoard opponent) {
            this.opponent = opponent;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                Graphics2D g2d = (Graphics2D) g.create();
                if (playerNumber == 2) {
                    g2d.translate(-600, 0);
                }
                parentPanel.paintBoard(g2d, board, currentPiece);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                g2d.drawString("Player " + playerNumber, 250, 50);
                if (loser == this) {
                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("Arial", Font.BOLD, 40));
                    g2d.drawString("Game Over", BOARD_X + WIDTH * BLOCK_SIZE / 6, BOARD_Y + HEIGHT * BLOCK_SIZE / 2);
                } else if (loser == opponent) {
                    g2d.setColor(Color.GREEN);
                    g2d.setFont(new Font("Arial", Font.BOLD, 40));
                    g2d.drawString("Winner!", BOARD_X + WIDTH * BLOCK_SIZE / 6, BOARD_Y + HEIGHT * BLOCK_SIZE / 2);
                }
                g2d.dispose();
                System.out.println("PlayerBoard " + playerNumber + " painted");
            } catch (Exception e) {
                System.err.println("Error painting PlayerBoard " + playerNumber + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void move(int dx, int dy) {
            try {
                parentPanel.move(currentPiece, dx, dy);
                repaint();
            } catch (Exception e) {
                System.err.println("Error moving piece for Player " + playerNumber + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void rotateCW() {
            try {
                parentPanel.rotateCW(currentPiece);
                repaint();
                System.out.println("Player " + playerNumber + " rotateCW");
            } catch (Exception e) {
                System.err.println("Error rotating CW for Player " + playerNumber + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void rotateCCW() {
            try {
                parentPanel.rotateCCW(currentPiece);
                repaint();
                System.out.println("Player " + playerNumber + " rotateCCW");
            } catch (Exception e) {
                System.err.println("Error rotating CCW for Player " + playerNumber + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void hold() {
            try {
                parentPanel.hold();
                repaint();
            } catch (Exception e) {
                System.err.println("Error holding piece for Player " + playerNumber + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void moveDown() {
            try {
                if (!parentPanel.moveDown()) {
                    int linesClearedThisTurn = parentPanel.clearLines();
                    if (linesClearedThisTurn > 0 && opponent != null) {
                        opponent.addObstacleRow(linesClearedThisTurn);
                    }
                    if (!parentPanel.canMove(currentPiece)) {
                        loser = this;
                        isGameOver = true;
                        timer.stop();
                        if (opponent != null) {
                            opponent.isGameOver = true;
                            opponent.timer.stop();
                            parentPanel.stopMusic();
                        }
                    }
                }
                repaint();
            } catch (Exception e) {
                System.err.println("Error moving down for Player " + playerNumber + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        public void hardDrop() {
            try {
                parentPanel.hardDrop();
                int linesClearedThisTurn = parentPanel.clearLines();
                if (linesClearedThisTurn > 0 && opponent != null) {
                    opponent.addObstacleRow(linesClearedThisTurn);
                }
                if (!parentPanel.canMove(currentPiece)) {
                    loser = this;
                    isGameOver = true;
                    timer.stop();
                    if (opponent != null) {
                        opponent.isGameOver = true;
                        opponent.timer.stop();
                        parentPanel.stopMusic();
                    }
                }
                repaint();
            } catch (Exception e) {
                System.err.println("Error hard dropping for Player " + playerNumber + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void addObstacleRow(int lines) {
            try {
                for (int l = 0; l < lines; l++) {
                    parentPanel.addObstacleRow();
                }
                repaint();
            } catch (Exception e) {
                System.err.println("Error adding obstacle row for Player " + playerNumber + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}