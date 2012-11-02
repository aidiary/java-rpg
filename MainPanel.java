import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MainPanel extends JPanel implements KeyListener, Common {
    private static final int WIDTH = 480;
    private static final int HEIGHT = 480;

    private static final int ROW = 15;
    private static final int COL = 15;
    private static final int CS = 32;  // cell size

    // map 0:floor 1:wall
    private int[][] map = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,1,1,1,1,0,0,0,0,1},
        {1,0,0,0,0,1,0,0,0,1,0,0,0,0,1},
        {1,0,0,0,0,1,0,0,0,1,0,0,0,0,1},
        {1,0,0,0,0,1,0,0,0,1,0,0,0,0,1},
        {1,0,0,0,0,1,1,0,1,1,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};

    private Image heroImage;
    private Image floorImage;
    private Image wallImage;

    // hero's position
    private int x, y;

    public MainPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        loadImage();

        // init hero's position
        x = 1;
        y = 1;

        setFocusable(true);
        addKeyListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
        drawChara(g);
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT :
                move(LEFT);
                break;
            case KeyEvent.VK_RIGHT :
                move(RIGHT);
                break;
            case KeyEvent.VK_UP :
                move(UP);
                break;
            case KeyEvent.VK_DOWN :
                move(DOWN);
                break;
        }
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    private boolean isHit(int x, int y) {
        // is there a wall?
        if (map[y][x] == 1) {
            return true;
        }
        return false;
    }

    private void move(int dir) {
        // move if there is not a wall
        switch (dir) {
            case LEFT:
                if (!isHit(x-1, y)) x--;
                break;
            case RIGHT:
                if (!isHit(x+1, y)) x++;
                break;
            case UP:
                if (!isHit(x, y-1)) y--;
                break;
            case DOWN:
                if (!isHit(x, y+1)) y++;
                break;
        }
    }

    private void loadImage() {
        ImageIcon icon = new ImageIcon(
            getClass().getResource("image/hero.gif"));
        heroImage = icon.getImage();

        icon = new ImageIcon(
            getClass().getResource("image/floor.gif"));
        floorImage = icon.getImage();
        
        icon = new ImageIcon(
            getClass().getResource("image/wall.gif"));
        wallImage = icon.getImage();
    }

    private void drawChara(Graphics g) {
        g.drawImage(heroImage, x * CS, y * CS, this);
    }

    private void drawMap(Graphics g) {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                switch (map[i][j]) {
                case 0 :  // floor
                    g.drawImage(floorImage, j * CS, i * CS, this);
                    break;
                case 1 :  // wall
                    g.drawImage(wallImage, j * CS, i * CS, this);
                    break;
                }
            }
        }
    }
}
