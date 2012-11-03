import java.awt.*;
import javax.swing.*;

public class Character implements Common {
    private Image image;

    // character's position
    private int x, y;
    // character's direction (LEFT, RIGHT, UP or DOWN)
    private int direction;
    // character's animation counter
    private int count;

    // thread for character animation
    private Thread threadAnime;

    // reference to Map
    private Map map;

    // reference to MainPanel
    private MainPanel panel;

    public Character(int x, int y, String filename, Map map) {
        // init character
        this.x = 1;
        this.y = 1;
        direction = DOWN;
        count = 0;

        this.map = map;

        loadImage(filename);

        // run thread
        threadAnime = new Thread(new AnimationThread());
        threadAnime.start();
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        // switch image based on animation counter
        g.drawImage(image,
                    x * CS - offsetX, y * CS - offsetY,
                    x * CS - offsetX + CS, y * CS - offsetY + CS,
                    count * CS, direction * CS,
                    CS + count * CS, direction * CS + CS,
                    null);
    }

    public void move(int dir) {
        // move if there is not a wall
        switch (dir) {
            case LEFT:
                if (!map.isHit(x-1, y)) x--;
                direction = LEFT;
                break;
            case RIGHT:
                if (!map.isHit(x+1, y)) x++;
                direction = RIGHT;
                break;
            case UP:
                if (!map.isHit(x, y-1)) y--;
                direction = UP;
                break;
            case DOWN:
                if (!map.isHit(x, y+1)) y++;
                direction = DOWN;
                break;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private void loadImage(String filename) {
        ImageIcon icon = new ImageIcon(getClass().getResource(filename));
        image = icon.getImage();
    }

    // Animation Class
    private class AnimationThread extends Thread {
        public void run() {
            while (true) {
                if (count == 0) {
                    count = 1;
                } else if (count == 1) {
                    count = 0;
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
