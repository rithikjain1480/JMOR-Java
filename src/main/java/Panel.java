import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class Panel extends JPanel {
    private int setting = 0;
    private Window window;
    private String sourceText = "";
    ArrayList<String> links = new ArrayList<String>();
    private boolean made;

    public Panel(Window window, String sourceText) {
        super();
        this.window = window;
        this.sourceText = sourceText;
        setting = 0;
        made = false;
    }

    public void setSetting(int s) {
        setting = s;
    }

    public void loadLinks() {
        RakeAnalyzer analyzer = new RakeAnalyzer(sourceText);
        analyzer.generateKeywords();
        //analyzer.generateKeyPhrases(10);
        ArrayList<String> queries = analyzer.keywords();
        System.out.println(queries);

        GoogleCrawler crawler = new GoogleCrawler();
        try {
            for (String query: queries) {
                for (String link: crawler.relevantLinks(query)) {
                    links.add(link);
                }
            }
        } catch (IOException e) {
            links.add("Failed to load links: " + e);
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);

        // fonts
        Font title = new Font("Dialog", Font.PLAIN, 60);
        Font subtitle = new Font("Dialog", Font.ITALIC, 15);
        Font heading = new Font("Dialog", Font.PLAIN, 30);
        Font link = new Font("Dialog", Font.PLAIN, 15);

        // header
        printText(g2, title, "JMOR", 30, 70);
        printText(g2, subtitle, "Helping Researchers Connect With Relevant Research", 30, 100);

        if (setting == 0) {
            // upload
            if (made == false) {
                final JButton gen = new JButton("Generate Links");

                gen.setSize(100, 20);
                gen.setLocation(300, 200);
                gen.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        setting += 1;
                        // PLACEHOLDER: REPLACE WITH FILE UPLOAD
                        //sourceText = "Compatibility of systems of linear constraints over the set of natural numbers. Criteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for constructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.";
                        JOptionPane.showMessageDialog(window,
                                "Text read: " + sourceText.substring(0, 20) + "...");
                        loadLinks();
                        setting = 1;
                        repaint();
                        remove(gen);
                    }
                });
                add(gen);
                made = true;
            }
        } else {
            printText(g2, heading, "Links", 30, 150);
            int linkY = 175;
            try {
                for (String url : links) {
                    if (url.indexOf("webcache") == -1) {
                        printText(g2, link, url, 45, linkY);
                        linkY += 25;
                    }
                }
            } catch (Exception e) {
                printText(g2, link, "could not load links: " + e, 45, linkY);
            }
        }
    }

    private void printText(Graphics2D g2, Font f, String s, int x, int y) {
        //g2.setColor(COLOR.blue);
        g2.setFont(f);
        g2.drawString(s, x, y);
    }
}