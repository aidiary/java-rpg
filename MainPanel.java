import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MainPanel extends JPanel implements KeyListener, Runnable, Common {
    public static final int WIDTH = 480;
    public static final int HEIGHT = 480;

    private Map map;
    private Character hero;

    // action keys
    private ActionKey leftKey;
    private ActionKey rightKey;
    private ActionKey upKey;
    private ActionKey downKey;

    private Thread gameLoop;

    public MainPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        setFocusable(true);
        addKeyListener(this);

        // create action keys
        leftKey = new ActionKey();
        rightKey = new ActionKey();
        upKey = new ActionKey();
        downKey = new ActionKey();

        // create map and hero
        map = new Map(this);
        hero = new Character(1, 1, "image/hero.gif", map, this);

        // start game loop
        gameLoop = new Thread(this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // calculate offset so that the hero is in the center of a screen.
        int offsetX = hero.getX() * CS - MainPanel.WIDTH / 2;
        // do not scroll at the edge of the map
        if (offsetX < 0) {
            offsetX = 0;
        } else if (offsetX > Map.WIDTH - MainPanel.WIDTH) {
            offsetX = Map.WIDTH - MainPanel.WIDTH;
        }

        int offsetY = hero.getY() * CS - MainPanel.HEIGHT / 2;
        // do not scroll at the edge of the map
        if (offsetY < 0) {
            offsetY = 0;
        } else if (offsetY > Map.HEIGHT - MainPanel.HEIGHT) {
            offsetY = Map.HEIGHT - MainPanel.HEIGHT;
        }

        map.draw(g, offsetX, offsetY);
        hero.draw(g, offsetX, offsetY);
    }

    public void run() {
        while (true) {
            if (leftKey.isPressed()) {
                hero.move(LEFT);
            } else if (rightKey.isPressed()) {
                hero.move(RIGHT);
            } else if (upKey.isPressed()) {
                hero.move(UP);
            } else if (downKey.isPressed()) {
                hero.move(DOWN);
            }

            repaint();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT) {
            leftKey.press();
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            rightKey.press();
        }
        if (keyCode == KeyEvent.VK_UP) {
            upKey.press();
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            downKey.press();
        }
    }

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT) {
            leftKey.release();
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            rightKey.release();
        }
        if (keyCode == KeyEvent.VK_UP) {
            upKey.release();
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            downKey.release();
        }
    }

    public void keyTyped(KeyEvent e) {
    }
}