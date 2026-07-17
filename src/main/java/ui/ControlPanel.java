package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private final JButton createButton;
    private final JButton loadButton;
    private final JButton deleteButton;
    private final JButton startButton;
    private final JButton clearButton;

    public ControlPanel() {

        setLayout(new GridLayout(4, 1, 5, 5));

        createButton = new JButton("Создать");
        loadButton = new JButton("Загрузить");
        deleteButton = new JButton("Удалить");
        startButton = new JButton("Запустить алгоритм");
        clearButton = new JButton("Сбросить");

        add(createButton);
        add(loadButton);
        add(deleteButton);
        add(startButton);
        add(clearButton);
    }

    public JButton getCreateButton() {
        return createButton;
    }

    public JButton getLoadButton() {
        return loadButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getClearButton() {
        return clearButton;
    }
}
