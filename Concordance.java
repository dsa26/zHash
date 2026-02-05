import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;

import java.util.Arrays;

// ChatGPT for Swing help only: https://chatgpt.com/share/6981525d-4fb4-8006-8fca-bbb3fd0aeff9
// Was very confused between AWT and Swing syntax and kept mixing the two up

public class Concordance extends Canvas {
    private Hash<Integer[][]> hash;
    private static final Font small = new Font("Sans Serif", Font.PLAIN, 12);
    private static final Font medium = new Font("Sans Serif", Font.BOLD, 14);
    private static final Font large = new Font("Sans Serif", Font.BOLD, 18);

    public Concordance(Hash<Integer[][]> hash) {
        this.hash = hash;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    public void calculatePoints(Graphics g, int x, int y, int i, Node<Integer[][]> node) {

    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public static void main(String[] args) {
        Hash<Integer[][]> hash = new Hash<>(10000);
        String[][] verses = readBible();

        for (int i = 0; i < verses.length; i++) {
            for (int j = 0; j < verses[i].length; j++) {
                if (verses[i][j] != null) {
                    String[] words = verses[i][j].split(" ");
                    for (int k = 0; k < words.length; k++) {
                        Integer[][] node = hash.get(words[k]);
                        if (node == null)
                            hash.put(words[k], new Integer[][] { { i, j } });
                        else {
                            Integer[][] newArr = Arrays.copyOf(node, node.length + 1);
                            newArr[node.length] = new Integer[] { i, j };
                            hash.put(words[k], newArr);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < hash.size(); i++) {
            Node<Integer[][]> node = hash.getIndex(i);
            if (node != null) {
                System.out.print("Index: " + i + " Key: " + node.key);
                for (int j = 0; j < node.val.length; j++) {
                    System.out.print(" [" + node.val[j][0] + ", " + node.val[j][1] + "]");
                }
                System.out.println("");
            }
        }

        System.out.println("Size: " + hash.size());
        System.out.println("Filled Size: " + hash.filledSize());
        System.out.println("Total Collisions: " + hash.totalCollisions());
        System.out.println("Most Collisions: " + hash.mostCollisions());

        JFrame frame = new JFrame("HashMap Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 1000);
        Concordance view = new Concordance(hash);
        view.setSize(1300, 1000);
        ScrollPane sp = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        sp.add(view);
        frame.add(sp);
        frame.setVisible(true);
    }

    private static String[][] readBible() {
        String[][] bible = new String[5][70];
        try (BufferedReader br = new BufferedReader(new FileReader("Jeremiah.txt"))) {
            String line = br.readLine();
            int currentRow = 0;
            int currentColumn = 0;
            while (line != null) {
                line = line.toLowerCase().replaceAll("(?![a-z]| |[1-9]|:).", "");
                if (!line.equals("") && Character.isDigit(line.charAt(0))) {
                    String row = "";
                    String column = "";
                    for (int i = 0; Character.isDigit(line.charAt(i)); i++) {
                        row += line.charAt(i);
                    }
                    for (int i = row.length() + 1; Character.isDigit(line.charAt(i)); i++) {
                        column += line.charAt(i);
                    }
                    line = line.substring(row.length() + column.length() + 2); // Two accounting for colon and space
                    currentRow = Integer.parseInt(row) - 1;
                    currentColumn = Integer.parseInt(column) - 1;
                }
                if (bible[currentRow][currentColumn] == null)
                    bible[currentRow][currentColumn] = "";
                if (!line.equals(""))
                    line += " ";
                bible[currentRow][currentColumn] += line;
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return bible;
    }
}