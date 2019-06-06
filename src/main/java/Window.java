import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private Panel panel;
    private JButton upload;

    public Window(String sourceText) {
        super("JMOR");
        panel = new Panel(this, sourceText);
        panel.setBackground(Color.WHITE);

        add(panel, BorderLayout.CENTER);
        setSize(700, 500);
        setVisible(true);
    }
}
