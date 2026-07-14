package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private final JButton createButton;
    private final JButton loadButton;
    private final JButton startButton;
    private final JButton clearButton;

    public ControlPanel() {

        setLayout(new GridLayout(4, 1, 5, 5));

        createButton = new JButton("Создать");
        loadButton = new JButton("Загрузить");
        startButton = new JButton("Запустить алгоритм");
        clearButton = new JButton("Очистить граф");

        add(createButton);
        add(loadButton);
        add(startButton);
        add(clearButton);
    }

    public JButton getLoadButton() {
        return loadButton;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getClearButton() {
        return clearButton;
    }
}