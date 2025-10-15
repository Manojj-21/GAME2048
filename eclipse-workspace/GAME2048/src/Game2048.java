import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Game2048 extends JFrame {
    private int size;
    private int[][] board;
    private int score;
    private JLabel scoreLabel;
    private JPanel boardPanel;
    private final Color[] tileColors = {
        new Color(0xCDC1B4), new Color(0xEEE4DA), new Color(0xEDE0C8), new Color(0xF2B179),
        new Color(0xF59563), new Color(0xF67C5F), new Color(0xF65E3B), new Color(0xEDCF72),
        new Color(0xEDCC61), new Color(0xEDC850), new Color(0xEDC53F), new Color(0xEDC22E)
    };

    public Game2048(int boardSize) {
        size = boardSize;
        board = new int[size][size];
        setupGUI();
        newGame();
    }

    private void setupGUI() {
        setTitle("2048 Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        setResizable(false);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(scoreLabel, BorderLayout.NORTH);

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(size, size, 5, 5));
        boardPanel.setBackground(new Color(0xBBADA0));
        add(boardPanel, BorderLayout.CENTER);

        JButton restartButton = new JButton("Restart");
        restartButton.setFocusable(false);
        restartButton.addActionListener(e -> newGame());
        add(restartButton, BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                boolean moved = false;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: moved = moveLeft(); break;
                    case KeyEvent.VK_RIGHT: moved = moveRight(); break;
                    case KeyEvent.VK_UP: moved = moveUp(); break;
                    case KeyEvent.VK_DOWN: moved = moveDown(); break;
                }
                if (moved) {
                    addRandomTile();
                    updateGUI();
                    checkGameState();
                }
            }
        });

        setFocusable(true);
        pack();
        setVisible(true);
    }

    private void newGame() {
        for (int[] row : board) java.util.Arrays.fill(row, 0);
        score = 0;
        addRandomTile();
        addRandomTile();
        updateGUI();
        scoreLabel.setText("Score: 0");
    }

    private void updateGUI() {
        boardPanel.removeAll();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                JLabel label = new JLabel(board[r][c] == 0 ? "" : String.valueOf(board[r][c]), SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 28));
                label.setOpaque(true);
                int value = board[r][c];
                int colorIndex = 0;
                if (value > 0) {
                    int tmp = value;
                    while (tmp > 2) {
                        tmp /= 2;
                        colorIndex++;
                    }
                }
                label.setBackground(tileColors[Math.min(colorIndex, tileColors.length - 1)]);
                label.setForeground(value < 8 ? new Color(0x776E65) : new Color(0xF9F6F2));
                label.setPreferredSize(new Dimension(80, 80));
                label.setBorder(BorderFactory.createLineBorder(new Color(0xBBADA0), 2));
                boardPanel.add(label);
            }
        }
        scoreLabel.setText("Score: " + score);
        boardPanel.revalidate();
        boardPanel.repaint();
        pack();
    }

    private void addRandomTile() {
        List<Point> empty = new ArrayList<>();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c] == 0) empty.add(new Point(r, c));
            }
        }
        if (empty.isEmpty()) return;
        Point pos = empty.get(new Random().nextInt(empty.size()));
        board[pos.x][pos.y] = (new Random().nextInt(10) == 0) ? 4 : 2;
    }

    // Left move logic
    private boolean moveLeft() {
        boolean moved = false;
        for (int r = 0; r < size; r++) {
            int[] row = board[r];
            int[] compressed = compress(row);
            int[] merged = merge(compressed);
            int[] finalRow = compress(merged);
            if (!java.util.Arrays.equals(row, finalRow)) {
                board[r] = finalRow;
                moved = true;
            }
        }
        return moved;
    }

    // Right move logic
    private boolean moveRight() {
        boolean moved = false;
        for (int r = 0; r < size; r++) {
            int[] row = reverse(board[r]);
            int[] compressed = compress(row);
            int[] merged = merge(compressed);
            int[] finalRow = reverse(compress(merged));
            if (!java.util.Arrays.equals(board[r], finalRow)) {
                board[r] = finalRow;
                moved = true;
            }
        }
        return moved;
    }

    // Up move logic
    private boolean moveUp() {
        boolean moved = false;
        for (int c = 0; c < size; c++) {
            int[] col = new int[size];
            for (int r = 0; r < size; r++) col[r] = board[r][c];
            int[] compressed = compress(col);
            int[] merged = merge(compressed);
            int[] finalCol = compress(merged);
            for (int r = 0; r < size; r++) {
                if (board[r][c] != finalCol[r]) {
                    board[r][c] = finalCol[r];
                    moved = true;
                }
            }
        }
        return moved;
    }

    // Down move logic
    private boolean moveDown() {
        boolean moved = false;
        for (int c = 0; c < size; c++) {
            int[] col = new int[size];
            for (int r = 0; r < size; r++) col[r] = board[r][c];
            int[] reversed = reverse(col);
            int[] compressed = compress(reversed);
            int[] merged = merge(compressed);
            int[] finalCol = reverse(compress(merged));
            for (int r = 0; r < size; r++) {
                if (board[r][c] != finalCol[r]) {
                    board[r][c] = finalCol[r];
                    moved = true;
                }
            }
        }
        return moved;
    }

    // Compresses non-zero numbers leftwards in a row/column
    private int[] compress(int[] arr) {
        int[] res = new int[size];
        int idx = 0;
        for (int n : arr) if (n != 0) res[idx++] = n;
        return res;
    }

    // Merges adjacent pairs with the same value
    private int[] merge(int[] arr) {
        int[] res = arr.clone();
        for (int i = 0; i < size - 1; i++) {
            if (res[i] != 0 && res[i] == res[i + 1]) {
                res[i] *= 2;
                score += res[i];
                res[i + 1] = 0;
            }
        }
        return res;
    }

    // Reverse helper for right/down moves
    private int[] reverse(int[] arr) {
        int[] res = new int[size];
        for (int i = 0; i < size; i++) res[i] = arr[size - 1 - i];
        return res;
    }

    // Win/Lose logic
    private void checkGameState() {
        if (has2048()) {
            JOptionPane.showMessageDialog(this, "Congratulations! You reached 2048!");
            newGame();
        } else if (!canMove()) {
            JOptionPane.showMessageDialog(this, "Game Over! No more moves.");
            newGame();
        }
    }

    private boolean has2048() {
        for (int[] row : board)
            for (int n : row)
                if (n == 2048)
                    return true;
        return false;
    }

    private boolean canMove() {
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++) {
                if (board[r][c] == 0)
                    return true;
                if (c < size - 1 && board[r][c] == board[r][c + 1])
                    return true;
                if (r < size - 1 && board[r][c] == board[r + 1][c])
                    return true;
            }
        return false;
    }

    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> {
            int boardSize = 4; // default
            while (true) {
                String input = JOptionPane.showInputDialog(null, "Enter board size (Y):", "Grid Size", JOptionPane.QUESTION_MESSAGE);
                if (input == null) return; // user cancelled
                try {
                    boardSize = Integer.parseInt(input);
                    if (boardSize >= 3 && boardSize <= 10) break; // reasonable size
                    JOptionPane.showMessageDialog(null, "Enter a number between 3 and 10.");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid integer.");
                }
            }
            new Game2048(boardSize);
        });
    }
}
