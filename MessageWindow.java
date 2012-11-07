import java.awt.*;

public class MessageWindow {
    // width of white border
    private static final int EDGE_WIDTH = 2;

    // outer frame
    private Rectangle rect;
    // inner frame
    private Rectangle innerRect;

    // message window is visible ?
    private boolean isVisible = false;

    public MessageWindow(Rectangle rect) {
        this.rect = rect;
        innerRect = new Rectangle(
                rect.x + EDGE_WIDTH,
                rect.y + EDGE_WIDTH,
                rect.width - EDGE_WIDTH * 2,
                rect.height - EDGE_WIDTH * 2);
    }

    public void draw(Graphics g) {
        if (isVisible == false) {
            return;
        }

        // draw outer rect
        g.setColor(Color.WHITE);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);

        // draw inner rect
        g.setColor(Color.BLACK);
        g.fillRect(innerRect.x, innerRect.y,
                   innerRect.width, innerRect.height);
    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
