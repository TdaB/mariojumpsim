package mariojumpsim;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class KeyListener extends KeyAdapter {
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isJumpPressed() {
        return jumpPressed;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        switch(event.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                jumpPressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        switch(event.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                jumpPressed = false;
                break;
        }
    }
}
