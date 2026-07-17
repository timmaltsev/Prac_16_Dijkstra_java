package ui;

import javax.swing.*;

import algorithm.DijkstraAlgorithm;

import java.awt.*;

import java.util.List;

public class LogPanel extends JPanel {

    private final JTextArea textArea;

    public LogPanel() {

        setLayout(new BorderLayout());

        textArea = new JTextArea();

        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void log(String message) {

        textArea.append(message);
        textArea.append("\n");

        textArea.setCaretPosition(
                textArea.getDocument().getLength());
    }

    public void logMultiple(List<String> messages){
        for (String line : messages) {
            log(line);
        }
    }

    public void clear() {
        textArea.setText("");
    }

    public void showAlgorithmResult(DijkstraAlgorithm algorithm) {
        for (String line : algorithm.consumeLog()) {
            log(line);
        }

        // textArea.setText("готово.");
    }
}
