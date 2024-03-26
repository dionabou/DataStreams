import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class DataStreamsGUI extends JFrame {
    private JTextArea originalTextArea, filteredTextArea;
    private JTextField searchField;
    private JButton loadButton, searchButton, quitButton;
    private Path filePath;

    public DataStreamsGUI() {
        setTitle("DataStreams");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        // Left Panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        originalTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        JScrollPane originalScrollPane = new JScrollPane(originalTextArea);
        leftPanel.add(originalScrollPane, BorderLayout.CENTER);

        // Right Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        filteredTextArea = new JTextArea();
        filteredTextArea.setEditable(false);
        JScrollPane filteredScrollPane = new JScrollPane(filteredTextArea);
        rightPanel.add(filteredScrollPane, BorderLayout.CENTER);

        // Add panels to frame
        add(leftPanel);
        add(rightPanel);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        loadButton.addActionListener(new LoadButtonListener());
        searchButton.addActionListener(new SearchButtonListener());
        quitButton.addActionListener(new QuitButtonListener());

        bottomPanel.add(new JLabel("Search String: "));
        bottomPanel.add(searchField);
        bottomPanel.add(loadButton);
        bottomPanel.add(searchButton);
        bottomPanel.add(quitButton);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private class LoadButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(DataStreamsGUI.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                filePath = fileChooser.getSelectedFile().toPath();
                try {
                    String content = Files.readString(filePath);
                    originalTextArea.setText(content);
                    filteredTextArea.setText(""); // Clear filtered text area when loading new file
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DataStreamsGUI.this, "Error loading file: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class SearchButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (filePath == null) {
                JOptionPane.showMessageDialog(DataStreamsGUI.this, "Please load a file first.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String searchString = searchField.getText();
            try (Stream<String> lines = Files.lines(filePath)) {
                // Filter lines containing the search string
                String filteredContent = lines.filter(line -> line.contains(searchString))
                        .collect(Collectors.joining("\n"));
                // Update the filteredTextArea
                filteredTextArea.setText(filteredContent);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(DataStreamsGUI.this, "Error searching file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class QuitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DataStreamsGUI::new);
    }
}
