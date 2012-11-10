import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.*;

public class Map implements Common {
    // map data
    private int[][] map;

    // map size (tile)
    private int row;
    private int col;

    // map size (pixel)
    private int width;
    private int height;

    // chip set
    private static BufferedImage image;

    // characters in this map
    private Vector<Character> characters = new Vector<Character>();
    // events in this map
    private Vector<Event> events = new Vector<Event>();

    // reference to MainPanel
    private MainPanel panel;

    private String mapFile;
    private String bgmName;

    public Map(String mapFile, String eventFile, String bgmName, MainPanel panel) {
        this.mapFile = mapFile;
        this.bgmName = bgmName;

        load(mapFile);
        loadEvent(eventFile);
        if (image == null) {
            loadImage("image/mapchip.gif");
        }
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        // display xrange of map (unit:pixel)
        int firstTileX = pixelsToTiles(offsetX);
        int lastTileX = firstTileX + pixelsToTiles(MainPanel.WIDTH) + 1;

        // display yrange of map (unit: pixel)
        int firstTileY = pixelsToTiles(offsetY);
        int lastTileY = firstTileY + pixelsToTiles(MainPanel.HEIGHT) + 1;

        // clipping
        lastTileX = Math.min(lastTileX, col);
        lastTileY = Math.min(lastTileY, row);

        for (int i = firstTileY; i < lastTileY; i++) {
            for (int j = firstTileX; j < lastTileX; j++) {
                int cx = (map[i][j] % 8) * CS;
                int cy = (map[i][j] / 8) * CS;
                g.drawImage(image,
                            tilesToPixels(j) - offsetX,
                            tilesToPixels(i) - offsetY,
                            tilesToPixels(j) - offsetX + CS,
                            tilesToPixels(i) - offsetY + CS,
                            cx, cy, cx + CS, cy + CS, panel);

                // draw events on (i, j)
                for (int n = 0; n < events.size(); n++) {
                    Event event = events.get(n);
                    if (event.x == j && event.y == i) {
                        cx = (event.id % 8) * CS;
                        cy = (event.id / 8) * CS;
                        g.drawImage(image,
                                    tilesToPixels(j) - offsetX,
                                    tilesToPixels(i) - offsetY,
                                    tilesToPixels(j) - offsetX + CS,
                                    tilesToPixels(i) - offsetY + CS,
                                    cx, cy, cx + CS, cy + CS, panel);
                    }
                }
            }
        }

        // draw characters in this map
        for (int i = 0; i < characters.size(); i++) {
            Character c = characters.get(i);
            c.draw(g, offsetX, offsetY);
        }
    }

    public boolean isHit(int x, int y) {
        if (map[y][x] == 1 ||    // wall
            map[y][x] == 2 ||    // throan
            map[y][x] == 5) {    // sea
            return true;
        }

        // Are there other characters?
        for (int i = 0; i < characters.size(); i++) {
            Character c = characters.get(i);
            if (c.getX() == x && c.getY() == y) {
                return true;
            }
        }

        // Are there events?
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event.x == x && event.y == y) {
                return event.isHit;
            }
        }

        return false;
    }

    public void addCharacter(Character c) {
        characters.add(c);
    }

    public void removeCharacter(Character c) {
        characters.remove(c);
    }

    // is there a character in (x, y) ?
    public Character checkCharacter(int x, int y) {
        for (int i = 0; i < characters.size(); i++) {
            Character c = characters.get(i);
            if (c.getX() == x && c.getY() == y) {
                return c;
            }
        }
        return null;
    }

    public Event checkEvent(int x, int y) {
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event.x == x && event.y == y) {
                return event;
            }
        }
        return null;
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }

    public static int pixelsToTiles(double pixels) {
        return (int)Math.floor(pixels / CS);
    }

    public static int tilesToPixels(int tiles) {
        return tiles * CS;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Vector<Character> getCharacters() {
        return characters;
    }

    public String getBgmName() {
        return bgmName;
    }

    public String getMapName() {
        return mapFile;
    }

    private void load(String filename) {
        try {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(filename)));
            // load row and col
            String line = br.readLine();
            row = Integer.parseInt(line);
            line = br.readLine();
            col = Integer.parseInt(line);
            // set map size
            width = col * CS;
            height = row * CS;
            // load map data
            map = new int[row][col];
            for (int i=0; i<row; i++) {
                line = br.readLine();
                for (int j=0; j<col; j++) {
                    map[i][j] = Integer.parseInt(line.charAt(j) + "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEvent(String filename) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream(filename), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                // skip null lines
                if (line.equals("")) continue;
                // skip comment lines
                if (line.startsWith("#")) continue;
                StringTokenizer st = new StringTokenizer(line, ",");
                String eventType = st.nextToken();
                if (eventType.equals("CHARACTER")) {
                    makeCharacterEvent(st);
                } else if (eventType.equals("TREASURE")) {
                    makeTreasureEvent(st);
                } else if (eventType.equals("DOOR")) {
                    makeDoorEvent(st);
                } else if (eventType.equals("MOVE")) {
                    makeMoveEvent(st);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImage(String filename) {
        try {
            image = ImageIO.read(getClass().getResource(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeCharacterEvent(StringTokenizer st) {
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        int id = Integer.parseInt(st.nextToken());
        int direction = Integer.parseInt(st.nextToken());
        int moveType = Integer.parseInt(st.nextToken());
        String message = st.nextToken();
        Character c = new Character(x, y, id, direction, moveType, this);
        c.setMessage(message);
        characters.add(c);
    }

    private void makeTreasureEvent(StringTokenizer st) {
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        String itemName = st.nextToken();
        TreasureEvent t = new TreasureEvent(x, y, itemName);
        events.add(t);
    }

    private void makeDoorEvent(StringTokenizer st) {
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        DoorEvent d = new DoorEvent(x, y);
        events.add(d);
    }

    private void makeMoveEvent(StringTokenizer st) {
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        int chipNo = Integer.parseInt(st.nextToken());
        int destMapNo = Integer.parseInt(st.nextToken());
        int destX = Integer.parseInt(st.nextToken());
        int destY = Integer.parseInt(st.nextToken());
        MoveEvent m = new MoveEvent(x, y, chipNo, destMapNo, destX, destY);
        events.add(m);
    }

    public void show() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }
}
