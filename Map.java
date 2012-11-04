import java.awt.*;
import javax.swing.*;
import java.io.*;

public class Map implements Common {
    // map data
    private int[][] map;

    // map size (tile)
    private int row;
    private int col;

    // map size (pixel)
    private int width;
    private int height;

    // chipset
    private Image floorImage;
    private Image wallImage;
    private Image throneImage;

    // reference to MainPanel
    private MainPanel panel;

    public Map(String filename, MainPanel panel) {
        load(filename);
        loadImage();
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
                switch (map[i][j]) {
                case 0:  // floor
                    g.drawImage(floorImage,
                                tilesToPixels(j) - offsetX,
                                tilesToPixels(i) - offsetY,
                                panel);
                    break;
                case 1:  // wall
                    g.drawImage(wallImage,
                                tilesToPixels(j) - offsetX,
                                tilesToPixels(i) - offsetY,
                                panel);
                    break;
                case 2:  // throne
                    g.drawImage(throneImage,
                                tilesToPixels(j) - offsetX,
                                tilesToPixels(i) - offsetY,
                                panel);
                }
            }
        }
    }

    public static int pixelsToTiles(double pixels) {
        return (int)Math.floor(pixels / CS);
    }

    public static int tilesToPixels(int tiles) {
        return tiles * CS;
    }

    public boolean isHit(int x, int y) {
        // is there a wall or a throne?
        if (map[y][x] == 1 || map[y][x] == 2) {
            return true;
        }
        return false;
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

    private void loadImage() {
        ImageIcon icon = new ImageIcon(
                getClass().getResource("image/floor.gif"));
        floorImage = icon.getImage();

        icon = new ImageIcon(
                getClass().getResource("image/wall.gif"));
        wallImage = icon.getImage();

        icon = new ImageIcon(
                getClass().getResource("image/throne.gif"));
        throneImage = icon.getImage();
    }
}
