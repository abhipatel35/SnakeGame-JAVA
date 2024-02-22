// Import necessary libraries
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

// Define the SnakeGame class which extends JPanel and implements ActionListener
public class SnakeGame extends JPanel implements ActionListener {

    // Define constants for the dimensions of the game window, size of the snake's segments,
    // maximum number of possible segments, position for random apple, and delay for timer
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int DOT_SIZE = 20;
    private final int ALL_DOTS = (WIDTH * HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int RAND_POS = 29;
    private final int DELAY = 140;

    // Arrays to store x and y coordinates of each segment of the snake
    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    // Variables to store the current number of segments, position of the apple,
    // and the game state
    private int dots;
    private int appleX;
    private int appleY;
    private boolean inGame = true;

    // Variables to track the direction of the snake
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    // Timer for game updates
    private Timer timer;

    // Constructor to initialize the game
    public SnakeGame() {
        initGame();
    }

    // Initialize the game
    private void initGame() {
        setBackground(Color.black); // Set background color of the game window
        setPreferredSize(new Dimension(WIDTH, HEIGHT)); // Set preferred size of the game window
        setFocusable(true); // Allow the game window to receive focus
        addKeyListener(new MyKeyAdapter()); // Add key listener to handle user input

        initSnake(); // Initialize the snake
        locateApple(); // Place the apple on the game board

        timer = new Timer(DELAY, this); // Create a timer to update the game state
        timer.start(); // Start the timer
    }

    // Initialize the snake
    private void initSnake() {
        dots = 3; // Start with 3 segments
        // Initialize the x and y coordinates of each segment
        for (int i = 0; i < dots; i++) {
            x[i] = 100 - i * DOT_SIZE;
            y[i] = 100;
        }
    }

    // Method to draw graphics on the game window
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g); // Draw the game components
    }

    // Method to draw game components
    private void doDrawing(Graphics g) {
        if (inGame) {
            // Draw the apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, DOT_SIZE, DOT_SIZE);

            // Draw the snake
            for (int i = 0; i < dots; i++) {
                if (i == 0) {
                    g.setColor(Color.green); // Head of the snake is green
                } else {
                    g.setColor(new Color(0, 128, 0)); // Body of the snake is dark green
                }
                g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE); // Draw each segment of the snake
            }

            Toolkit.getDefaultToolkit().sync(); // Synchronize graphics state
        } else {
            gameOver(g); // If the game is over, display the game over message
        }
    }

    // Method to handle game updates
    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple(); // Check if the snake has eaten the apple
            checkCollision(); // Check for collisions with walls or itself
            move(); // Move the snake
        }

        repaint(); // Redraw the game window
    }

    // Method to check if the snake has eaten the apple
    private void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            dots++; // Increase the length of the snake
            locateApple(); // Place the apple in a new random position
        }
    }

    // Method to move the snake
    private void move() {
        // Move each segment of the snake
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Change the position of the head based on the direction
        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }
        if (rightDirection) {
            x[0] += DOT_SIZE;
        }
        if (upDirection) {
            y[0] -= DOT_SIZE;
        }
        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    // Method to check for collisions
    private void checkCollision() {
        // Check if the snake collides with itself
        for (int i = dots; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                inGame = false;
            }
        }

        // Check if the snake collides with the boundaries
        if (y[0] >= HEIGHT || y[0] < 0 || x[0] >= WIDTH || x[0] < 0) {
            inGame = false;
        }

        // Stop the timer if the game is over
        if (!inGame) {
            timer.stop();
        }
    }

    // Method to place the apple in a random position
    private void locateApple() {
        Random rand = new Random();
        appleX = rand.nextInt(RAND_POS) * DOT_SIZE;
        appleY = rand.nextInt(RAND_POS) * DOT_SIZE;
    }

    // Method to display the game over message
    private void gameOver(Graphics g) {
        String gameOverMsg = "Game Over";
        String restartMsg = "Press R to Restart";

        // Set font and color for the "Game Over" message
        Font gameOverFont = new Font("Helvetica", Font.BOLD, 30);
        g.setColor(Color.white);
        g.setFont(gameOverFont);

        // Calculate position to center the "Game Over" message horizontally and vertically
        FontMetrics gameOverMetrics = getFontMetrics(gameOverFont);
        int gameOverX = (WIDTH - gameOverMetrics.stringWidth(gameOverMsg)) / 2;
        int gameOverY = HEIGHT / 2;

        // Draw the "Game Over" message
        g.drawString(gameOverMsg, gameOverX, gameOverY);

        // Set font and color for the "Press R to Restart" message
        Font restartFont = new Font("Helvetica", Font.BOLD, 25);
        g.setFont(restartFont);

        // Calculate position to center the "Press R to Restart" message horizontally and position it at the bottom
        FontMetrics restartMetrics = getFontMetrics(restartFont);
        int restartX = (WIDTH - restartMetrics.stringWidth(restartMsg)) / 2;
        int restartY = HEIGHT - restartMetrics.getHeight();

        // Draw the "Press R to Restart" message
        g.drawString(restartMsg, restartX, restartY);
    }


    // Inner class to handle keyboard input
    private class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            // Change the direction of the snake based on the pressed key
            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            if (!inGame && key == KeyEvent.VK_R) {
                restartGame();
            }
        }
    }

    private void restartGame() {
        inGame = true;
        initSnake();
        locateApple();
        timer.restart();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new SnakeGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}