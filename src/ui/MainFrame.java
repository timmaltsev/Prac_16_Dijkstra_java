package ui;

import model.Graph;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final Graph graph;
    private final GraphPanel graphPanel;

    public MainFrame(Graph graph) {

        this.graph = graph;

        setTitle("Прототип");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        graphPanel = new GraphPanel(this.graph);
        add(graphPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addVertexButton = new JButton("Добавить вершину");
        JButton addEdgeButton = new JButton("Добавить ребро");
        JButton deleteVertexButton = new JButton("Удалить вершину");
        JButton deleteEdgeButton = new JButton("Удалить ребро");
        JButton cancelButton = new JButton("Отмена");

        controlPanel.add(addVertexButton);
        controlPanel.add(addEdgeButton);
        controlPanel.add(deleteVertexButton);
        controlPanel.add(deleteEdgeButton);
        controlPanel.add(cancelButton);

        add(controlPanel, BorderLayout.SOUTH);

        addVertexButton.addActionListener(e -> {
            graphPanel.setMode(EditorMode.ADD_VERTEX);
        });

        addEdgeButton.addActionListener(e -> {
            graphPanel.setMode(EditorMode.ADD_EDGE);
        });

        deleteVertexButton.addActionListener(e -> {
            graphPanel.setMode(EditorMode.DELETE_VERTEX);
        });

        deleteEdgeButton.addActionListener(e -> {
            graphPanel.setMode(EditorMode.DELETE_EDGE);
        });

        cancelButton.addActionListener(e -> {
            graphPanel.setMode(EditorMode.NONE);
        });
    }
}