import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

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
    private ActionKey spaceKey;

    private Thread gameLoop;

    private Random rand = new Random();

    private MessageWindow messageWindow;
    private static Rectangle WND_RECT = new Rectangle(62, 324, 356, 140);

    public MainPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        setFocusable(true);
        addKeyListener(this);

        // create action keys
        leftKey = new ActionKey();
        rightKey = new ActionKey();
        upKey = new ActionKey();
        downKey = new ActionKey();
        spaceKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);

        // create map
        map = new Map("map/map.dat", "event/event.dat", this);

        // create character
        hero = new Character(4, 4, 0, DOWN, 0, map);

        // add characters to the map
        map.addCharacter(hero);

        // create message window
        messageWindow = new MessageWindow(WND_RECT);

        // start game loop
        gameLoop = new Thread(this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // calculate offset so that the hero is in the center of a screen.
        int offsetX = hero.getPX() - MainPanel.WIDTH / 2;
        // do not scroll at the edge of the map
        if (offsetX < 0) {
            offsetX = 0;
        } else if (offsetX > map.getWidth() - MainPanel.WIDTH) {
            offsetX = map.getWidth() - MainPanel.WIDTH;
        }

        int offsetY = hero.getPY() - MainPanel.HEIGHT / 2;
        // do not scroll at the edge of the map
        if (offsetY < 0) {
            offsetY = 0;
        } else if (offsetY > map.getHeight() - MainPanel.HEIGHT) {
            offsetY = map.getHeight() - MainPanel.HEIGHT;
        }

        // draw map
        map.draw(g, offsetX, offsetY);

        // draw message window
        messageWindow.draw(g);
    }

    public void run() {
        while (true) {
            if (messageWindow.isVisible()) {
                messageWindowCheckInput();
            } else {
                mainWindowCheckInput();
            }

            if (!messageWindow.isVisible()) {
                heroMove();
                characterMove();
            }

            repaint();

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void mainWindowCheckInput() {
        if (leftKey.isPressed()) {
            if (!hero.isMoving()) {
                hero.setDirection(LEFT);
                hero.setMoving(true);
            }
        }

        if (rightKey.isPressed()) {
            if (!hero.isMoving()) {
                hero.setDirection(RIGHT);
                hero.setMoving(true);
            }
        }

        if (upKey.isPressed()) {
            if (!hero.isMoving()) {
                hero.setDirection(UP);
                hero.setMoving(true);
            }
        }

        if (downKey.isPressed()) {
            if (!hero.isMoving()) {
                hero.setDirection(DOWN);
                hero.setMoving(true);
            }
        }

        if (spaceKey.isPressed()) {
            // cannot open window if hero is moving
            if (hero.isMoving()) {
                return;
            }

            // search
            TreasureEvent treasure = hero.search();
            if (treasure != null) {
                messageWindow.setMessage("HERO DISCOVERED/" + treasure.getItemName());
                messageWindow.show();
                map.removeEvent(treasure);
                return;
            }

            // door
            DoorEvent door = hero.open();
            if (door != null) {
                map.removeEvent(door);
                return;
            }

            // talk
            if (!messageWindow.isVisible()) {
                Character c = hero.talkWith();
                if (c != null) {
                    messageWindow.setMessage(c.getMessage());
                    messageWindow.show();
                } else {
                    messageWindow.setMessage("THERE IS NO ONE/IN THAT DIRECTION");
                    messageWindow.show();
                }
            }
        }
    }

    private void messageWindowCheckInput() {
        if (spaceKey.isPressed()) {
            if (messageWindow.nextPage()) {
                messageWindow.hide();
            }
        }
    }

    private void heroMove() {
        if (hero.isMoving()) {
            if (hero.move()) {
            }
        }
    }

    private void characterMove() {
        // get characters in the map
        Vector<Character> characters = map.getCharacters();
        // move each character
        for (int i = 0; i < characters.size(); i++) {
            Character c = characters.get(i);
            if (c.getMoveType() == 1) {
                if (c.isMoving()) {
                    c.move();
                } else if (rand.nextDouble() < Character.PROB_MOVE) {
                    c.setDirection(rand.nextInt(4));
                    c.setMoving(true);
                }
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
        if (keyCode == KeyEvent.VK_SPACE) {
            spaceKey.press();
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
        if (keyCode == KeyEvent.VK_SPACE) {
            spaceKey.release();
        }
    }

    public void keyTyped(KeyEvent e) {
    }
}