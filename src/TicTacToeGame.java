import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TicTacToeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeScreen());
    }
}

// Welcome Screen Class
class WelcomeScreen extends JFrame {
    JComboBox<String> difficultyCombo;

    public WelcomeScreen() {
        setTitle("Welcome to Tic Tac Toe");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("Tic Tac Toe", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        difficultyCombo = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultyCombo.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setPreferredSize(new Dimension(150, 40));
        startButton.addActionListener(e -> startGame());

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.add(title);
        panel.add(new JLabel("Select Difficulty:", JLabel.CENTER));
        panel.add(difficultyCombo);
        panel.add(startButton);

        add(panel);
        setVisible(true);
    }

    private void startGame() {
        String difficulty = (String) difficultyCombo.getSelectedItem();
        new TicTacToe(difficulty);
        dispose(); // Close welcome screen
    }
}

// Game Class
class TicTacToe extends JFrame {
    private JButton[] buttons = new JButton[9];
    private char currentPlayer = 'X';
    private boolean gameOver = false;
    private String difficulty;
    private Random random = new Random();
    private int[] winningLine = null; // for animation

    public TicTacToe(String difficulty) {
        this.difficulty = difficulty;

        setTitle("Tic Tac Toe - " + difficulty + " Mode");
        setSize(400, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel(new GridLayout(3, 3));
        initializeButtons(gamePanel);

        JPanel controlPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        JButton backButton = new JButton("Select Level");

        resetButton.addActionListener(e -> resetGame());
        backButton.addActionListener(e -> {
            dispose();
            new WelcomeScreen();
        });

        controlPanel.add(resetButton);
        controlPanel.add(backButton);

        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void initializeButtons(JPanel panel) {
        Font font = new Font("Arial", Font.BOLD, 48);
        for (int i = 0; i < 9; i++) {
            JButton button = new JButton("");
            button.setFont(font);
            int index = i;
            button.addActionListener(e -> handleClick(index));
            buttons[i] = button;
            panel.add(button);
        }
    }

    private void handleClick(int index) {
        if (!buttons[index].getText().equals("") || gameOver) return;

        buttons[index].setText(String.valueOf(currentPlayer));
        buttons[index].setEnabled(false);

        if (checkWin(currentPlayer)) {
            gameOver = true;
            animateWin();
            return;
        } else if (isDraw()) {
            gameOver = true;
            JOptionPane.showMessageDialog(this, "It's a draw!");
            return;
        }

        switchPlayer();

        if (currentPlayer == 'O') {
            botMove();
        }
    }

    private void botMove() {
        if (difficulty.equals("Easy")) {
            easyBot();
        } else if (difficulty.equals("Medium")) {
            mediumBot();
        } else {
            hardBot();
        }
    }

    private void easyBot() {
        while (true) {
            int move = random.nextInt(9);
            if (buttons[move].getText().equals("")) {
                buttons[move].setText("O");
                buttons[move].setEnabled(false);
                break;
            }
        }
        postBotMove();
    }

    private void mediumBot() {
        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText("O");
                if (checkWin('O')) {
                    buttons[i].setEnabled(false);
                    gameOver = true;
                    animateWin();
                    return;
                }
                buttons[i].setText("");
            }
        }

        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText("X");
                if (checkWin('X')) {
                    buttons[i].setText("O");
                    buttons[i].setEnabled(false);
                    postBotMove();
                    return;
                }
                buttons[i].setText("");
            }
        }

        easyBot(); // fallback
    }

    private void hardBot() {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText("O");
                int score = minimax(false);
                buttons[i].setText("");
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }

        buttons[bestMove].setText("O");
        buttons[bestMove].setEnabled(false);
        postBotMove();
    }

    private int minimax(boolean isMaximizing) {
        if (checkWin('O')) return 1;
        if (checkWin('X')) return -1;
        if (isDraw()) return 0;

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText(isMaximizing ? "O" : "X");
                int score = minimax(!isMaximizing);
                buttons[i].setText("");

                bestScore = isMaximizing
                        ? Math.max(score, bestScore)
                        : Math.min(score, bestScore);
            }
        }
        return bestScore;
    }

    private void postBotMove() {
        if (checkWin('O')) {
            gameOver = true;
            animateWin();
        } else if (isDraw()) {
            gameOver = true;
            JOptionPane.showMessageDialog(this, "It's a draw!");
        } else {
            switchPlayer();
        }
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private boolean checkWin(char player) {
        String mark = String.valueOf(player);
        int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] line : lines) {
            if (buttons[line[0]].getText().equals(mark) &&
                buttons[line[1]].getText().equals(mark) &&
                buttons[line[2]].getText().equals(mark)) {
                winningLine = line;
                return true;
            }
        }
        return false;
    }

    private void animateWin() {
        if (winningLine == null) return;

        Timer timer = new Timer(300, null);
        final int[] count = {0};

        timer.addActionListener(e -> {
            for (int index : winningLine) {
                JButton b = buttons[index];
                b.setBackground((count[0] % 2 == 0) ? Color.YELLOW : null);
            }
            count[0]++;
            if (count[0] > 5) {
                timer.stop();
                JOptionPane.showMessageDialog(this, currentPlayer + " wins!");
            }
        });

        timer.start();

        // Disable all buttons
        for (JButton b : buttons) b.setEnabled(false);
    }

    private boolean isDraw() {
        for (JButton b : buttons) {
            if (b.getText().equals("")) return false;
        }
        return true;
    }

    private void resetGame() {
        for (JButton button : buttons) {
            button.setText("");
            button.setEnabled(true);
            button.setBackground(null);
        }
        currentPlayer = 'X';
        gameOver = false;
        winningLine = null;
    }
}
