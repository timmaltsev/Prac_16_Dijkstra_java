package ui;

import algorithm.DijkstraAlgorithm;
import model.Graph;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame
        implements EditorListener {

    private final Graph graph;

    private final GraphPanel graphPanel;
    private final ToolPanel toolPanel;
    private final ControlPanel controlPanel;
    private final LogPanel logPanel;

    private JButton activeButton = null;

    private AlgorithmMode algorithmMode;
    private DijkstraAlgorithm algorithm;

    public MainFrame(Graph graph) {

        this.graph = graph;

        setTitle("Версия 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        graphPanel = new GraphPanel(graph);
        graphPanel.setEditorListener(this);

        toolPanel = new ToolPanel();
        controlPanel = new ControlPanel();
        logPanel = new LogPanel();

        graphPanel.setSelectionListener(source -> {

            algorithm = new DijkstraAlgorithm(graph, source);

            logPanel.clear();

            if (algorithmMode == AlgorithmMode.INSTANT) {

                algorithm.runToCompletion();

            } else {

                algorithm.step();
            }

            for (String message : algorithm.consumeLog()) {
                logPanel.log(message);
            }

            graphPanel.repaint();
        });

        JSplitPane rightSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                controlPanel,
                logPanel
        );
        rightSplit.setResizeWeight(0.25);
        rightSplit.setDividerLocation(180);

        JSplitPane topSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                graphPanel,
                rightSplit
        );
        topSplit.setResizeWeight(0.8);
        topSplit.setDividerLocation(850);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(toolPanel, BorderLayout.WEST);

        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                topSplit,
                bottomPanel
        );
        mainSplit.setResizeWeight(0.85);
        mainSplit.setDividerLocation(650);

        add(mainSplit);

        toolPanel.getAddVertexButton().addActionListener(e -> {
            Tips.showAddVertex(this);
            toggleMode(EditorMode.ADD_VERTEX,
                    toolPanel.getAddVertexButton());
        });

        toolPanel.getAddEdgeButton().addActionListener(e -> {
            Tips.showAddEdge(this);
            toggleMode(EditorMode.ADD_EDGE,
                    toolPanel.getAddEdgeButton());
        });

        toolPanel.getDeleteVertexButton().addActionListener(e -> {
            Tips.showDeleteVertex(this);
            toggleMode(EditorMode.DELETE_VERTEX,
                    toolPanel.getDeleteVertexButton());
        });

        toolPanel.getDeleteEdgeButton().addActionListener(e -> {
            Tips.showDeleteEdge(this);
            toggleMode(EditorMode.DELETE_EDGE,
                    toolPanel.getDeleteEdgeButton());
        });

        controlPanel.getStartButton().addActionListener(e -> {

            AlgorithmDialog dialog = new AlgorithmDialog(this);
            dialog.setVisible(true);

            algorithmMode = dialog.getSelectedMode();

            if (algorithmMode == null) {
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Щёлкните по стартовой вершине.");

            graphPanel.setMode(EditorMode.SELECT_SOURCE);
        });

        toolPanel.getNextStepButton().addActionListener(e -> {

            if (algorithm == null)
                return;

            if (algorithm.step()) {

                for (String message : algorithm.consumeLog()) {
                    logPanel.log(message);
                }

                graphPanel.repaint();

            } else {

                logPanel.log("Алгоритм завершён.");
            }
        });
    }

    private void toggleMode(EditorMode mode, JButton button) {

        if (graphPanel.getMode() == mode) {
            graphPanel.setMode(EditorMode.NONE);
            resetActiveButton();
            return;
        }

        graphPanel.setMode(mode);
        setActiveButton(button);
    }

    private void setActiveButton(JButton button) {

        resetActiveButton();

        activeButton = button;
        activeButton.setBackground(Color.GREEN);
    }

    private void resetActiveButton() {

        if (activeButton == null)
            return;

        activeButton.setBackground(null);
        activeButton.repaint();

        activeButton = null;
    }

    @Override
    public void modeFinished() {

        graphPanel.setMode(EditorMode.NONE);
        resetActiveButton();
    }
}
