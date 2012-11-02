import java.awt.*;
import javax.swing.*;

class MainPanel extends JPanel {
    private static final int WIDTH = 480;
    private static final int HEIGHT = 480;

    private Image heroImage;

    public MainPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        loadImage();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(heroImage, 0, 0, this);
    }

    private void loadImage() {
        ImageIcon icon = new ImageIcon(
            getClass().getResource("image/hero.gif"));
        heroImage = icon.getImage();
    }
}
