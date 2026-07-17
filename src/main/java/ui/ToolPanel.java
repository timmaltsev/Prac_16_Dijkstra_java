package ui;

import javax.swing.*;
import java.awt.*;

public class ToolPanel extends JPanel {

    private final JButton addVertexButton;
    private final JButton addEdgeButton;
    private final JButton deleteVertexButton;
    private final JButton deleteEdgeButton;
    private final JButton moveVertexButton;
    private final JButton pathViewButton;
    private final JButton previousStepButton;
    private final JButton nextStepButton;


    public ToolPanel() {

        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));

        JPanel addPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        addPanel.setBorder(
                BorderFactory.createTitledBorder("Добавление"));

        addVertexButton = new JButton("+ Вершина");
        addEdgeButton = new JButton("+ Ребро");

        addPanel.add(addVertexButton);
        addPanel.add(addEdgeButton);

        JPanel deletePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        deletePanel.setBorder(
                BorderFactory.createTitledBorder("Удаление"));

        deleteVertexButton = new JButton("- Вершина");
        deleteEdgeButton = new JButton("- Ребро");

        deletePanel.add(deleteVertexButton);
        deletePanel.add(deleteEdgeButton);


        pathViewButton = new JButton("Просмотр путей");


        previousStepButton = new JButton("←");
        nextStepButton = new JButton("→");


        add(addPanel);
        add(deletePanel);

        add(Box.createHorizontalStrut(20));

        add(moveVertexButton);

        add(Box.createHorizontalStrut(20));

        add(pathViewButton);

        add(Box.createHorizontalStrut(20));

        add(previousStepButton);
        add(nextStepButton);
    }

    public JButton getAddVertexButton() {
        return addVertexButton;
    }

    public JButton getAddEdgeButton() {
        return addEdgeButton;
    }

    public JButton getDeleteVertexButton() {
        return deleteVertexButton;
    }

    public JButton getDeleteEdgeButton() {
        return deleteEdgeButton;
    }

    public JButton getMoveVertexButton() {
        return moveVertexButton;
    }
    
    public JButton getPathViewButton() {
        return pathViewButton;
    }

    public JButton getPreviousStepButton() {
        return previousStepButton;
    }

    public JButton getNextStepButton() {
        return nextStepButton;
    }
}
