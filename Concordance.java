import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import java.awt.*;

import java.util.Arrays;

// ChatGPT for Swing help only: https://chatgpt.com/share/6981525d-4fb4-8006-8fca-bbb3fd0aeff9
// Was very confused between AWT and Swing syntax and kept mixing the two up

public class Concordance extends Canvas {
    private Hash<Integer[][]> hash;
    private String[][] verses;
    private static final Font SMALL = new Font("Sans Serif", Font.PLAIN, 12);
    private static final Font MEDIUM = new Font("Sans Serif", Font.BOLD, 14);
    private static final Font LARGE = new Font("Sans Serif", Font.BOLD, 18);

    public Concordance(Hash<Integer[][]> hash) {
        this.hash = hash;
    }

    public static void main(String[] args) {
        Concordance view = new Concordance(new Hash<Integer[][]>());
        view.init("Jeremiah.txt");

        // for (int i = 0; i < hash.size(); i++) {
        // Node<Integer[][]> node = hash.getIndex(i);
        // // Note: Just realized that this only checks for the first node in the LL
        // // if (node != null) {
        // // System.out.print("Index: " + i + " Key: " + node.key);
        // // for (int j = 0; j < node.val.length; j++) {
        // // System.out.print(" [" + node.val[j][0] + ", " + node.val[j][1] + "]");
        // // }
        // // System.out.println("");
        // // }
        // }

        // view.process(args[0]);

        System.out.println("Size: " + view.hash.size());
        System.out.println("Filled Size: " + view.hash.filledSize());
        System.out.println("Total Collisions: " + view.hash.totalCollisions());
        System.out.println("Most Collisions: " + view.hash.mostCollisions());

        JFrame frame = new JFrame("HashMap Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 1000);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel search = new JPanel();
        JLabel searchTitle = new JLabel("Bible Lookup: ");
        searchTitle.setFont(LARGE);
        searchTitle.setHorizontalAlignment(JLabel.CENTER);
        search.add(searchTitle);
        JTextField searchField = new JTextField(20);
        searchField.setFont(SMALL);
        search.add(searchField);
        mainPanel.add(search);

        JPanel results = new JPanel();
        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
        mainPanel.add(results);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                view.process(searchField.getText(), results);
            }

            public void removeUpdate(DocumentEvent e) {
                view.process(searchField.getText(), results);
            }

            public void insertUpdate(DocumentEvent e) {
                view.process(searchField.getText(), results);
            }
        });

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private void process(String word) {
        Integer[][] positions = lookup(word);
        if (positions == null)
            return;
        for (Integer[] verse : positions) {
            String[] verseParts = verses[verse[0]][verse[1]].split(word);
            System.out.print((verse[0] + 1) + ":" + (verse[1] + 1) + " ");
            for (int i = 0; i < verseParts.length; i++) {
                if (i != 0)
                    System.out.print("\u001B[1m" + word + "\u001B[0m"); // Makes the word bold
                System.out.print(verseParts[i]);
            }
            System.out.println("\n");
        }
    }

    private void process(String word, JPanel results) {
        Integer[][] positions = lookup(word);
        results.removeAll();
        results.revalidate();
        results.repaint();
        if (positions == null)
            return;
        for (Integer[] verse : positions) {
            String verseDisplay = "";
            String[] verseParts = verses[verse[0]][verse[1]].split(" " + word + " ");
            verseDisplay += (verse[0] + 1) + ":" + (verse[1] + 1) + " ";
            for (int i = 0; i < verseParts.length; i++) {
                if (i != 0)
                    verseDisplay += "<b> " + word + " </b>"; // Makes the word bold
                verseDisplay += verseParts[i];
            }
            JLabel verseLabel = new JLabel(
                    "<html><div style='width: 650px; padding: 10px;'>" + verseDisplay + "</div></html>");
            // Google AI Overview recommended using HTML for easier formatting
            verseLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            wrapper.add(verseLabel); // For easier sizing, spacing, and wrapping
            results.add(wrapper);
        }
        results.revalidate();
        results.repaint();
    }

    private Integer[][] lookup(String word) {
        return hash.get(word);
    }

    private void init(String filename) {
        hash = new Hash<>(10000);
        verses = readBible(filename);

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
    }

    private static String[][] readBible(String filename) {
        String[][] bible = new String[5][70];
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine();
            int currentRow = 0;
            int currentColumn = 0;
            while (line != null) {
                line = line.toLowerCase().replaceAll("(?![a-z]| |[0-9]|:).", ""); // Had this on 1-9 for a long time and
                                                                                  // kept debugging why 5:20 became 5:2
                // if (currentRow == 4 && currentColumn == 19)
                // System.out.println("check 1");
                // Don't know why this line doesn't get read
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
                bible[currentRow][currentColumn] += line.replaceAll(":", ""); // Had to wait because colon represents
                                                                              // verse numbering
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return bible;
    }
}