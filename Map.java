import java.awt.*;
import javax.swing.*;

public class Map implements Common {
    private static final int ROW = 15;
    private static final int COL = 15;

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

    // mapchip
    private Image floorImage;
    private Image wallImage;

    // reference to MainPanel
    private MainPanel panel;

    public Map(MainPanel panel) {
        loadImage();
    }

    public void draw(Graphics g) {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                switch (map[i][j]) {
                case 0 :  // floor
                    g.drawImage(floorImage, j * CS, i * CS, panel);
                    break;
                case 1 :  // wall
                    g.drawImage(wallImage, j * CS, i * CS, panel);
                    break;
                }
            }
        }
    }

    public boolean isHit(int x, int y) {
        // is there a wall?
        if (map[y][x] == 1) {
            return true;
        }
        return false;
    }

    private void loadImage() {
        ImageIcon icon = new ImageIcon(
            getClass().getResource("image/floor.gif"));
        floorImage = icon.getImage();
        
        icon = new ImageIcon(
            getClass().getResource("image/wall.gif"));
        wallImage = icon.getImage();
    }
}
