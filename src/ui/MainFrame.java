package ui;

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

    public MainFrame(Graph graph) {

        this.graph = graph;

        setTitle("Прототип");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        graphPanel = new GraphPanel(this.graph);

        toolPanel = new ToolPanel();

        controlPanel = new ControlPanel();

        logPanel = new LogPanel();


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


        toolPanel.getAddVertexButton().addActionListener(e ->
                toggleMode(EditorMode.ADD_VERTEX,
                        toolPanel.getAddVertexButton()));

        toolPanel.getAddEdgeButton().addActionListener(e ->
                toggleMode(EditorMode.ADD_EDGE,
                        toolPanel.getAddEdgeButton()));

        toolPanel.getDeleteVertexButton().addActionListener(e ->
                toggleMode(EditorMode.DELETE_VERTEX,
                        toolPanel.getDeleteVertexButton()));

        toolPanel.getDeleteEdgeButton().addActionListener(e ->
                toggleMode(EditorMode.DELETE_EDGE,
                        toolPanel.getDeleteEdgeButton()));

        controlPanel.getStartButton().addActionListener(e -> {

            AlgorithmDialog dialog =
                    new AlgorithmDialog(this);

            dialog.setVisible(true);

            AlgorithmMode mode =
                    dialog.getSelectedMode();

            if (mode == null)
                return;

            if (mode == AlgorithmMode.INSTANT) {

            }
            
            else {

            }

        });
    }

    /**
     * Переключение режима редактора.
     */
    private void toggleMode(EditorMode mode, JButton button) {

        if (graphPanel.getMode() == mode) {

            graphPanel.setMode(EditorMode.NONE);

            resetActiveButton();

            return;
        }

        graphPanel.setMode(mode);

        setActiveButton(button);
    }

    /**
     * Подсветка выбранной кнопки.
     */
    private void setActiveButton(JButton button) {

        resetActiveButton();

        activeButton = button;

        activeButton.setBackground(Color.GREEN);
    }

    /**
     * Снять подсветку.
     */
    private void resetActiveButton() {

        if (activeButton != null) {

            activeButton.setBackground(null);
            activeButton.repaint();

            activeButton = null;
        }
    }

    @Override
    public void modeFinished() {

        graphPanel.setMode(EditorMode.NONE);

        resetActiveButton();

    }
}