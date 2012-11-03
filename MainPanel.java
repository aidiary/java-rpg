import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MainPanel extends JPanel implements KeyListener, Common {
    private static final int WIDTH = 480;
    private static final int HEIGHT = 480;

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
        map.draw(g);
        hero.draw(g);
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

