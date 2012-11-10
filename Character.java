import java.awt.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

public class Character implements Common {
    private static final int SPEED = 4;
    public static final double PROB_MOVE = 0.02;

    private static BufferedImage image;
    private int id;

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

    private int moveType;
    private String message;

    // thread for character animation
    private Thread threadAnime;

    // reference to Map
    private Map map;

    public Character(int x, int y, int id, int direction,
                     int moveType, Map map) {
        // init character
        this.x = x;
        this.y = y;
        px = x * CS;
        py = y * CS;
        this.id = id;
        this.direction = direction;
        this.moveType = moveType;
        this.map = map;

        count = 0;

        if (image == null) {
            loadImage("image/character.gif");
        }

        // run thread
        threadAnime = new Thread(new AnimationThread());
        threadAnime.start();
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        int cx = (id % 8) * (CS * 2);
        int cy = (id / 8) * (CS * 4);
        // switch image based on animation counter
        g.drawImage(image,
                    px - offsetX,
                    py - offsetY,
                    px - offsetX + CS,
                    py - offsetY + CS,
                    cx + count * CS,
                    cy + direction * CS,
                    cx + CS + count * CS,
                    cy + direction * CS + CS,
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

    // return the character which is in front of this character
    public Character talkWith() {
        int nextX = 0;
        int nextY = 0;
        switch (direction) {
        case LEFT:
            nextX = x - 1;
            nextY = y;
            break;
        case RIGHT:
            nextX = x + 1;
            nextY = y;
            break;
        case UP:
            nextX = x;
            nextY = y - 1;
            break;
        case DOWN:
            nextX = x;
            nextY = y + 1;
            break;
        }

        // is there a character?
        Character c = map.checkCharacter(nextX, nextY);
        // a player and a character are opposed mutually.
        if (c != null) {
            switch (direction) {
            case LEFT:
                c.setDirection(RIGHT);
                break;
            case RIGHT:
                c.setDirection(LEFT);
                break;
            case UP:
                c.setDirection(DOWN);
                break;
            case DOWN:
                c.setDirection(UP);
                break;
            }
        }
        return c;
    }

    public TreasureEvent search() {
        Event event = map.checkEvent(x, y);
        if (event instanceof TreasureEvent) {
            return (TreasureEvent)event;
        }
        return null;
    }

    public DoorEvent open() {
        int nextX = 0;
        int nextY = 0;
        switch (direction) {
        case LEFT:
            nextX = x - 1;
            nextY = y;
            break;
        case RIGHT:
            nextX = x + 1;
            nextY = y;
            break;
        case UP:
            nextX = x;
            nextY = y - 1;
            break;
        case DOWN:
            nextX = x;
            nextY = y + 1;
            break;
        }
        Event event = map.checkEvent(nextX, nextY);
        if (event instanceof DoorEvent) {
            return (DoorEvent)event;
        }
        return null;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMoveType() {
        return moveType;
    }

    private void loadImage(String filename) {
        try {
            image = ImageIO.read(getClass().getResource(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
