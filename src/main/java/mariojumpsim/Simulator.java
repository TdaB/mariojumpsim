package mariojumpsim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class Simulator extends JFrame {
    // Canvas properties
    private static final int CANVAS_WIDTH  = 1000;
    private static final int CANVAS_HEIGHT = 800;
    private static final int GROUND_HEIGHT = 700;
    // Player properties
    private static final int PLAYER_WIDTH = 25;
    private static final int PLAYER_HEIGHT = 25;
    private static final double ACCEL_X = 2000;
    private static final double DECEL_X = 3000;
    private static final double ACCEL_Y = 8000;
    private static final double VELOCITY_MAX_X = 1000;
    private static final double VELOCITY_MAX_Y = 10000;
    private static final double JUMP_VELOCITY = 3000;
    private static final double ZERO_ERROR = .001;
    private double velX = 0;
    private double velY = 0;
    private double playerX = .5 * CANVAS_WIDTH;
    private double playerY = .5 * CANVAS_HEIGHT;

    private long startTime;
    private long lastUpdateTime;
    private long frames = 0;

    private static final Logger log = LoggerFactory.getLogger(Simulator.class);
    private final KeyListener keyListener = new KeyListener();

    // Set up the GUI components and event handlers
    public Simulator() {
        this.addKeyListener(keyListener);

        this.startTime = System.currentTimeMillis();
        this.lastUpdateTime = System.currentTimeMillis();

        DrawCanvas canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        getContentPane().add(canvas);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setTitle("Incredible Mario Simulator");
        setVisible(true);
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
        this.updateX(elapsed);
        this.updateY(elapsed);

        log.debug("UPDATED VEL X = " + this.velX);
        log.debug("UPDATED VEL Y = " + this.velY);
        log.debug("UPDATED PLAYER X = " + this.playerX);
        log.debug("UPDATED PLAYER Y = " + this.playerY);
    }

    private void updateX(double elapsed) {
        boolean leftPressed = this.keyListener.isLeftPressed();
        boolean rightPressed = this.keyListener.isRightPressed();

        if (leftPressed && !rightPressed) {
            double updatedVelX = Physics.updateVelocity(this.velX, -1.0 * ACCEL_X, elapsed, VELOCITY_MAX_X);
            this.playerX += Physics.distance(this.velX, updatedVelX, elapsed);
            this.velX = updatedVelX;
        } else if (rightPressed && !leftPressed) {
            double updatedVelX = Physics.updateVelocity(this.velX, ACCEL_X, elapsed, VELOCITY_MAX_X);
            this.playerX += Physics.distance(this.velX, updatedVelX, elapsed);
            this.velX = updatedVelX;
        }  else {
            if (Math.abs(this.velX) < ZERO_ERROR) {
                return;
            } else if (this.velX < 0) {
                double updatedVelX = Physics.updateVelocity(this.velX, DECEL_X, elapsed, VELOCITY_MAX_X);
                this.playerX += Physics.distance(this.velX, updatedVelX, elapsed);
                this.velX = updatedVelX;
            } else {
                double updatedVelX = Physics.updateVelocity(this.velX, -1.0 * DECEL_X, elapsed, VELOCITY_MAX_X);
                this.playerX += Physics.distance(this.velX, updatedVelX, elapsed);
                this.velX = updatedVelX;
            }
        }
    }

    private void updateY(double elapsed) {
        boolean jumpPressed = this.keyListener.isJumpPressed();

        if (jumpPressed && this.playerY == GROUND_HEIGHT) {
            double updatedVelY = -1 * JUMP_VELOCITY;
            this.playerY += Physics.distance(this.velY, updatedVelY, elapsed);
            this.velY = updatedVelY;
        } else {
            if (this.playerY < GROUND_HEIGHT) {
                // Decel Y
                double updatedVelY = Physics.updateVelocity(this.velY, ACCEL_Y, elapsed, VELOCITY_MAX_Y);
                this.playerY += Physics.distance(this.velY, updatedVelY, elapsed);
                if (this.playerY >= GROUND_HEIGHT) {
                    this.playerY = GROUND_HEIGHT;
                    this.velY = 0;
                } else {
                    this.velY = updatedVelY;
                }
            }
        }
    }

    private class DrawCanvas extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw ground
            g.setColor(new Color(92, 64, 51));
            //g.drawLine(0, GROUND_HEIGHT, CANVAS_WIDTH - 1, GROUND_HEIGHT);
            g.fillRect(0, GROUND_HEIGHT, CANVAS_WIDTH, CANVAS_HEIGHT - GROUND_HEIGHT);
            // Draw player
            g.setColor(Color.RED);
            g.fillRect((int) playerX - (PLAYER_WIDTH / 2), (int) playerY - PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);

            setBackground(new Color(49, 200, 248));

            updatePlayer();

            repaint();
        }
    }

    public static void main(String[] args) {
        // Run the GUI codes on the Event-Dispatching thread for thread safety
        SwingUtilities.invokeLater(() -> new Simulator());
    }
}
