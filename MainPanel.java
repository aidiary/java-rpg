import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

class MainPanel extends JPanel implements KeyListener, Runnable, Common {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 640;

    // 20ms/frame = 50fps
    private static final int PERIOD = 20;

    // debug mode
    private static final boolean DEBUG_MODE = true;

    // map list
    private Map[] maps;
    // current map number
    private int mapNo;

    // our hero!
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
    private static Rectangle WND_RECT = new Rectangle(142, 480, 356, 140);

    private MidiEngine midiEngine = new MidiEngine();
    private WaveEngine waveEngine = new WaveEngine();

    // BGM
    // from TAM Music Factory http://www.tam-music.com/
    private static final String[] bgmNames = {"castle", "field"};
    // Sound Clip
    private static final String[] soundNames = {"treasure", "door", "step"};

    // double buffering
    private Graphics dbg;
    private Image dbImage = null;

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
        maps = new Map[2];
        maps[0] = new Map("map/castle.map", "event/castle.evt", "castle", this);
        maps[1] = new Map("map/field.map", "event/field.evt", "field", this);
        mapNo = 0;  // initial map

        // create character
        hero = new Character(6, 6, 0, DOWN, 0, maps[mapNo]);

        // add characters to the map
        maps[mapNo].addCharacter(hero);

        // create message window
        messageWindow = new MessageWindow(WND_RECT);

        // load BGM and sound clips
        loadSound();

        midiEngine.play(maps[mapNo].getBgmName());

        // start game loop
        gameLoop = new Thread(this);
        gameLoop.start();
    }

    public void run() {
        long beforeTime, timeDiff, sleepTime;

        beforeTime = System.currentTimeMillis();
        while (true) {
            checkInput();
            gameUpdate();
            gameRender();
            printScreen();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleepTime = PERIOD - timeDiff;
            // sleep at least 5ms
            if (sleepTime <= 0) {
                sleepTime = 5;
            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            beforeTime = System.currentTimeMillis();
        }
    }

    private void checkInput() {
        if (messageWindow.isVisible()) {
            messageWindowCheckInput();
        } else {
            mainWindowCheckInput();
        }
    }

    private void gameUpdate() {
        if (!messageWindow.isVisible()) {
            heroMove();
            characterMove();
        }
    }

    private void gameRender() {
        if (dbImage == null) {
            // buffer image
            dbImage = createImage(WIDTH, HEIGHT);
            if (dbImage == null) {
                return;
            } else {
                // device context of buffer image
                dbg = dbImage.getGraphics();
            }
        }

        dbg.setColor(Color.WHITE);
        dbg.fillRect(0, 0, WIDTH, HEIGHT);

        // calculate offset so that the hero is in the center of a screen.
        int offsetX = hero.getPX() - MainPanel.WIDTH / 2;
        // do not scroll at the edge of the map
        if (offsetX < 0) {
            offsetX = 0;
        } else if (offsetX > maps[mapNo].getWidth() - MainPanel.WIDTH) {
            offsetX = maps[mapNo].getWidth() - MainPanel.WIDTH;
        }

        int offsetY = hero.getPY() - MainPanel.HEIGHT / 2;
        // do not scroll at the edge of the map
        if (offsetY < 0) {
            offsetY = 0;
        } else if (offsetY > maps[mapNo].getHeight() - MainPanel.HEIGHT) {
            offsetY = maps[mapNo].getHeight() - MainPanel.HEIGHT;
        }

        // draw map
        maps[mapNo].draw(dbg, offsetX, offsetY);

        // draw message window
        messageWindow.draw(dbg);

        // display debug information
        if (DEBUG_MODE) {
            Font font = new Font("SansSerif", Font.BOLD, 16);
            dbg.setFont(font);
            dbg.setColor(Color.YELLOW);
            dbg.drawString(maps[mapNo].getMapName() + " (" + maps[mapNo].getCol() + "," + maps[mapNo].getRow() + ")", 4, 16);
            dbg.drawString("(" + hero.getX() + "," + hero.getY() + ") ", 4, 32);
            dbg.drawString("(" + hero.getPX() + "," + hero.getPY() + ")", 4, 48);
            dbg.drawString(maps[mapNo].getBgmName(), 4, 64);
        }
    }

    private void printScreen() {
        Graphics g = getGraphics();
        if ((g != null) && (dbImage != null)) {
            g.drawImage(dbImage, 0, 0, null);
        }
        Toolkit.getDefaultToolkit().sync();
        if (g != null) {
            g.dispose();
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
                waveEngine.play("treasure");
                messageWindow.setMessage("HERO DISCOVERED/" + treasure.getItemName());
                messageWindow.show();
                maps[mapNo].removeEvent(treasure);
                return;
            }

            // door
            DoorEvent door = hero.open();
            if (door != null) {
                waveEngine.play("door");
                maps[mapNo].removeEvent(door);
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
                Event event = maps[mapNo].checkEvent(hero.getX(), hero.getY());
                if (event instanceof MoveEvent) {
                    waveEngine.play("step");
                    // move to another map
                    MoveEvent m = (MoveEvent)event;
                    maps[mapNo].removeCharacter(hero);
                    mapNo = m.destMapNo;
                    hero = new Character(m.destX, m.destY, 0, DOWN, 0, maps[mapNo]);
                    maps[mapNo].addCharacter(hero);
                    midiEngine.play(maps[mapNo].getBgmName());
                }
            }
        }
    }

    private void characterMove() {
        // get characters in the map
        Vector<Character> characters = maps[mapNo].getCharacters();
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

    private void loadSound() {
        // load midi files
        for (int i = 0; i < bgmNames.length; i++) {
            midiEngine.load(bgmNames[i], "bgm/" + bgmNames[i] + ".mid");
        }

        // load sound clip files
        for (int i = 0; i < soundNames.length; i++) {
            waveEngine.load(soundNames[i], "sound/" + soundNames[i] + ".wav");
        }
    }
}