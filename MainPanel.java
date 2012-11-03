import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MainPanel extends JPanel implements KeyListener, Common {
    public static final int WIDTH = 480;
    public static final int HEIGHT = 480;

    private Map map;
    private Character hero;

    public MainPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        map = new Map(this);
        hero = new Character(1, 1, "image/hero.gif", map, this);
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

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT :
                hero.move(LEFT);
                break;
            case KeyEvent.VK_RIGHT :
                hero.move(RIGHT);
                break;
            case KeyEvent.VK_UP :
                hero.move(UP);
                break;
            case KeyEvent.VK_DOWN :
                hero.move(DOWN);
                break;
        }
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}

