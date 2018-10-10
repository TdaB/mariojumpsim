package mariojumpsim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;       // Using AWT's Graphics and Color
import java.awt.event.*; // Using AWT event classes and listener interfaces
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;    // Using Swing's components and containers


public class Simulator extends JFrame {
    public static final int CANVAS_WIDTH  = 1000;
    public static final int CANVAS_HEIGHT = 800;
    public static final int GROUND_HEIGHT = 700;
    public static final int PLAYER_WIDTH = 25;
    public static final int PLAYER_HEIGHT = 25;

    private static final double ACCEL_X = 2000;
    private static final double DECEL_X = 3000;
    private static final double ACCEL_Y = 8000;
    private static final double MAX_VELOCITY_X = 1000;
    private static final double MAX_VELOCITY_Y = 10000;
    private static final double JUMP_VELOCITY = 3000;
    private static final double ZERO_LIMIT = 10;

    private double velX = 0;
    private double velY = 0;
    private double playerX = CANVAS_WIDTH / 2;
    private double playerY = CANVAS_HEIGHT / 2;

    private long startTime;
    private long lastUpdateTime;
    private long frames = 0;

    private Map<String, Boolean> keyHistory = new HashMap<>();
    private DrawCanvas canvas;
    private static final Logger log = LoggerFactory.getLogger(Simulator.class);

    // Set up the GUI components and event handlers
    public Simulator() {
        this.keyHistory.put("left", false);
        this.keyHistory.put("right", false);
        this.keyHistory.put("jump", false);
        this.addKeyListener(new KeyListener());

        this.startTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();

        canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        // Set the Drawing JPanel as the JFrame's content-pane
        Container cp = getContentPane();
        cp.add(canvas);
        setDefaultCloseOperation(EXIT_ON_CLOSE);   // Handle the CLOSE button
        pack();              // Either pack() the components; or setSize()
        setTitle("T da B's Amazing Simulator");  // "super" JFrame sets the title
        setVisible(true);    // "super" JFrame show
    }

    private double updateVelocityX(double initialV, double accel, double t) {
        double finalV = initialV + (accel * t);
        if (finalV < -1.0 * MAX_VELOCITY_X) {
            return -1.0 * MAX_VELOCITY_X;
        } else if (finalV > MAX_VELOCITY_X) {
            return MAX_VELOCITY_X;
        } else {
            return finalV;
        }
    }

    private double updateVelocityY(double initialV, double accel, double t) {
        double finalV = initialV + (accel * t);
        if (finalV < -1.0 * MAX_VELOCITY_Y) {
            return -1.0 * MAX_VELOCITY_Y;
        } else if (finalV > MAX_VELOCITY_Y) {
            return MAX_VELOCITY_Y;
        } else {
            return finalV;
        }
    }

    private double distance(double initialV, double finalV, double t) {
        return (initialV + finalV) * .5 * t;
    }

    public void updatePlayer() {
        long currentTime = System.currentTimeMillis();
        double elapsed = (double) (currentTime - this.lastUpdateTime) * .001;
        this.lastUpdateTime = currentTime;
        if (log.isDebugEnabled()) {
            this.frames++;
            double totalElapsed = (double) (currentTime - this.startTime) * .001;
            log.debug("FRAMES PER SECOND = " + (double) this.frames / totalElapsed);
        }

        boolean leftPressed = keyHistory.get("left");
        boolean rightPressed = keyHistory.get("right");
        boolean jumpPressed = keyHistory.get("jump");

        if (leftPressed && !rightPressed) {
            double updatedVelX = this.updateVelocityX(this.velX, -1.0 * ACCEL_X, elapsed);
            this.playerX += this.distance(this.velX, updatedVelX, elapsed);
            this.velX = updatedVelX;
        } else if (rightPressed && !leftPressed) {
            double updatedVelX = this.updateVelocityX(this.velX, ACCEL_X, elapsed);
            this.playerX += this.distance(this.velX, updatedVelX, elapsed);
            this.velX = updatedVelX;
        }  else {
            if (Math.abs(this.velX) < ZERO_LIMIT) {
                this.velX = 0;
            // Decelerate X
            } else if (this.velX < 0) {
                double updatedVelX = this.updateVelocityX(this.velX, DECEL_X, elapsed);
                this.playerX += this.distance(this.velX, updatedVelX, elapsed);
                this.velX = updatedVelX;
            } else {
                double updatedVelX = this.updateVelocityX(this.velX, -1.0 * DECEL_X, elapsed);
                this.playerX += this.distance(this.velX, updatedVelX, elapsed);
                this.velX = updatedVelX;
            }
        }
        if (jumpPressed && playerY == GROUND_HEIGHT) {
            double updatedVelY = -1 * JUMP_VELOCITY;
            this.playerY += this.distance(this.velY, updatedVelY, elapsed);
            this.velY = updatedVelY;
        } else {
            if (this.playerY < GROUND_HEIGHT) {
                // Decel Y
                double updatedVelY = this.updateVelocityY(this.velY, ACCEL_Y, elapsed);
                this.playerY += this.distance(this.velY, updatedVelY, elapsed);
                if (this.playerY >= GROUND_HEIGHT) {
                    this.playerY = GROUND_HEIGHT;
                    this.velY = 0;
                } else {
                    this.velY = updatedVelY;
                }
            }
        }
        log.debug("UPDATED VEL X = " + this.velX);
        log.debug("UPDATED VEL Y = " + this.velY);
        log.debug("UPDATED PLAYER X = " + playerX);
        log.debug("UPDATED PLAYER Y = " + this.playerY);
    }

    private class DrawCanvas extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            setBackground(Color.BLACK);

            g.setColor(Color.WHITE);
            g.drawLine(0, GROUND_HEIGHT, CANVAS_WIDTH - 1, GROUND_HEIGHT);

            g.setColor(Color.RED);
            g.fillRect((int) playerX - (PLAYER_WIDTH / 2), (int) playerY - PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);

            updatePlayer();

            this.repaint();
        }
    }

    private class KeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            switch(event.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    keyHistory.put("left", true);
                    break;
                case KeyEvent.VK_RIGHT:
                    keyHistory.put("right", true);
                    break;
                case KeyEvent.VK_SPACE:
                    keyHistory.put("jump", true);
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
            switch(event.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    keyHistory.put("left", false);
                    break;
                case KeyEvent.VK_RIGHT:
                    keyHistory.put("right", false);
                    break;
                case KeyEvent.VK_SPACE:
                    keyHistory.put("jump", false);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        // Run the GUI codes on the Event-Dispatching thread for thread safety
        SwingUtilities.invokeLater(() -> new Simulator());
    }
}