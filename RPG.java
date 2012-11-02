import java.awt.*;
import javax.swing.*;

public class RPG extends JFrame {
    public RPG() {
        setTitle("RPG");

        MainPanel panel = new MainPanel();
        Container contentPane = getContentPane();
        contentPane.add(panel);

        pack();
    }

    public static void main(String[] args) {
        RPG frame = new RPG();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
