import java.awt.*;
import javax.swing.*;

public class Character implements Common {
    // character's speed
    private static final int SPEED = 4;

    private Image image;

    // character's position (unit: tile)
    private int x, y;
    // character's position (unit: pixel)
    private int px, py;

    // character's direction (LEFT, RIGHT, UP or DOWN)
    private int direction;
    // character's animation counter
    private int count;

    private boolean isMoving;
    private int moveLength;

    // thread for character animation
    private Thread threadAnime;

    // reference to Map
    private Map map;

    public Character(int x, int y, String filename, Map map) {
        // init character
        this.x = x;
        this.y = x;
        px = x * CS;
        py = y * CS;

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
                    px - offsetX, py - offsetY,
                    px - offsetX + CS, py - offsetY + CS,
                    count * CS, direction * CS,
                    CS + count * CS, direction * CS + CS,
                    null);
    }

    public boolean move() {
        switch (direction) {
        case LEFT:
            if (moveLeft()) {
                // return true if pixel-based scrolling is completed.
                return true;
            }
            break;
        case RIGHT:
            if (moveRight()) {
                return true;
            }
            break;
        case UP:
            if (moveUp()) {
                return true;
            }
            break;
        case DOWN:
            if (moveDown()) {
                return true;
            }
            break;
        }

        return false;
    }

    private boolean moveLeft() {
        int nextX = x - 1;
        int nextY = y;
        if (nextX < 0) nextX = 0;
        if (!map.isHit(nextX, nextY)) {
            px -= Character.SPEED;
            if (px < 0) px = 0;
            moveLength += Character.SPEED;
            if (moveLength >= CS) {
                // pixel-based scrolling is completed
                // hero moves to left tile
                x--;
                if (x < 0) x = 0;
                px = x * CS;
                isMoving = false;
                return true;
            }
        } else {
            isMoving = false;
            px = x * CS;
            py = y * CS;
        }
        return false;
    }

    private boolean moveRight() {
        int nextX = x + 1;
        int nextY = y;
        if (nextX > map.getCol() - 1) nextX = map.getCol() - 1;
        if (!map.isHit(nextX, nextY)) {
            px += Character.SPEED;
            if (px > map.getWidth() - CS)
                px = map.getWidth() - CS;
            moveLength += Character.SPEED;
            if (moveLength >= CS) {
                x++;
                if (x > map.getCol() - 1) x = map.getCol() - 1;
                px = x * CS;
                isMoving = false;
                return true;
            }
        } else {
            isMoving = false;
            px = x * CS;
            py = y * CS;
        }

        return false;
    }

    private boolean moveUp() {
        int nextX = x;
        int nextY = y - 1;
        if (nextY < 0) nextY = 0;
        if (!map.isHit(nextX, nextY)) {
            py -= Character.SPEED;
            if (py < 0) py = 0;
            moveLength += Character.SPEED;
            if (moveLength >= CS) {
                y--;
                if (y < 0) y = 0;
                py = y * CS;
                isMoving = false;
                return true;
            }
        } else {
            isMoving = false;
            px = x * CS;
            py = y * CS;
        }
        return false;
    }

    private boolean moveDown() {
        int nextX = x;
        int nextY = y + 1;
        if (nextY > map.getRow() - 1) nextY = map.getRow() - 1;
        if (!map.isHit(nextX, nextY)) {
            py += Character.SPEED;
            if (py > map.getHeight() - CS)
                py = map.getHeight() - CS;
            moveLength += Character.SPEED;
            if (moveLength >= CS) {
                y++;
                if (y > map.getRow() - 1) y = map.getRow() - 1;
                py = y * CS;
                isMoving = false;
                return true;
            }
        } else {
            isMoving = false;
            px = x * CS;
            py = y * CS;
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPX() {
        return px;
    }

    public int getPY() {
        return py;
    }

    public void setDirection(int dir) {
        direction = dir;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean flag) {
        isMoving = flag;
        moveLength = 0;
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
