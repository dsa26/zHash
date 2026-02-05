import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;

// ChatGPT for Swing help only: https://chatgpt.com/share/6981525d-4fb4-8006-8fca-bbb3fd0aeff9
// Was very confused between AWT and Swing syntax and kept mixing the two up

public class Main extends Canvas {
    private Hash<Boolean> hash;
    private static final Font small = new Font("Sans Serif", Font.PLAIN, 12);
    private static final Font medium = new Font("Sans Serif", Font.BOLD, 14);
    private static final Font large = new Font("Sans Serif", Font.BOLD, 18);

    public Main(Hash<Boolean> hash) {
        this.hash = hash;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int count = 0; // Count of filled cells
        int currentRow = 0; // Current row number (8 cells per row)
        int currentRowStart = 10; // Current row's starting y-coordinate
        int currentRowMax = 10; // Current row's maximum cell height
        for (int i = 0; i < hash.size(); i++) {
            if (hash.getIndex(i) != null) {
                int height = calculatePoints(g, (count % 8) * 160 + 10, currentRowStart, i,
                        hash.getIndex(i)); // Returns cell height
                count++;
                currentRowMax = Math.max(currentRowMax, height);
            }
            if (count / 8 > currentRow) { // If it's time to move to the next row
                currentRowStart += currentRowMax + 10; // Dynamically adding new rows at the required height
                currentRowMax = 10;
                currentRow++;
            }
        }
    }

    public int calculatePoints(Graphics g, int x, int y, int i, Node<Boolean> node) {
        g.setColor(Color.DARK_GRAY);
        int height = 15 * (Hash.llSize(node) + 1) + 10;
        g.drawRect(x, y, 150, height);
        g.setColor(Color.BLACK);
        g.setFont(small);
        g.drawString("Index: " + i, x + 5, y + 15);
        g.setFont(medium);
        Node<Boolean> current = node;
        for (int j = 1; j <= Hash.llSize(node); j++) {
            g.drawString("Word " + j + ": " + current.key, x + 5, y + 15 * (j + 1));
            current = current.next;
        }
        return height; // For easier layout calculation
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public static void main(String[] args) {
        Hash<Boolean> hash = new Hash<>(2000);
        String[] words = readFile("1000WordDictionary.txt").split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i].replaceAll("(?![a-z]| ).", "")
                    .replaceAll("(?<= ) {1,}", "").trim();
            if (hash.get(word) == null)
                hash.put(word, true);
        }
        System.out.println("Size: " + hash.size());
        System.out.println("Filled Size: " + hash.filledSize());
        System.out.println("Total Collisions: " + hash.totalCollisions());
        System.out.println("Most Collisions: " + hash.mostCollisions());

        JFrame frame = new JFrame("HashMap Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 1000);
        Main view = new Main(hash);
        view.setSize(1300,
                (hash.filledSize() - hash.totalCollisions()) / 8 * 50 + 15 * hash.totalCollisions()); // Rough
                                                                                                      // calculation
                                                                                                      // for
                                                                                                      // total
                                                                                                      // height
        ScrollPane sp = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        sp.add(view);
        frame.add(sp);
        frame.setVisible(true);
    }

    private static String readFile(String filePath) {
        String output = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while (line != null) {
                output += " " + line;
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return output;
    }
}