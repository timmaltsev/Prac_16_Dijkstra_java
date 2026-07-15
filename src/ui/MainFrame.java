package ui;

import model.Graph;
import model.Vertex;
import algorithm.DijkstraAlgorithm;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame
                       implements EditorListener {

    private final Graph graph;

    private final GraphPanel graphPanel;
    private final ToolPanel toolPanel;
    private final ControlPanel controlPanel;
    private final LogPanel logPanel;

    private DijkstraAlgorithm algorithm;
    // private AlgorithmMode algorithmMode;

    private JButton activeButton = null;

    public MainFrame(Graph graph) {

        this.graph = graph;

        setTitle("Версия 1");
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

        controlPanel.getStartButton().addActionListener(e -> startAlgorithm());
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

    private void startAlgorithm() {

        if (graph.getVertices().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Граф пуст.",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AlgorithmDialog dialog = new AlgorithmDialog(this);
        dialog.setVisible(true);

        AlgorithmMode mode = dialog.getSelectedMode();

        if (mode == null) {
            return; // пользователь нажал "Отмена"
        }

        Object[] vertexNames = graph.getVertices()
                .stream()
                .map(Vertex::getName)
                .toArray();

        Object selected = JOptionPane.showInputDialog(
                this,
                "Выберите начальную вершину:",
                "Начальная вершина",
                JOptionPane.QUESTION_MESSAGE,
                null,
                vertexNames,
                vertexNames[0]);

        if (selected == null) {
            return; // пользователь отменил выбор вершины
        }

        Vertex source = graph.findVertexByName(selected.toString());

        if (source == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Не удалось найти выбранную вершину.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        algorithm = new DijkstraAlgorithm(graph, source);

        if (mode == AlgorithmMode.INSTANT) {

            algorithm.runToCompletion();

            JOptionPane.showMessageDialog(
                    this,
                    "Алгоритм выполнен.");

            // Позже здесь будет:
            // logPanel.showAlgorithmResult(algorithm);

        } else {

            algorithm.step();

            JOptionPane.showMessageDialog(
                    this,
                    "Выполнен первый шаг алгоритма.\n"
                            + "Дальнейшее выполнение будет осуществляться кнопкой \"Следующий шаг\".");

            // Позже здесь будет обновление интерфейса
        }

    }

    private Vertex chooseSourceVertex() {

        Object[] names =
                graph.getVertices()
                        .stream()
                        .map(Vertex::getName)
                        .toArray();

        Object result =
                JOptionPane.showInputDialog(
                        this,
                        "Выберите начальную вершину",
                        "Алгоритм Дейкстры",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        names,
                        names[0]);

        if (result == null)
            return null;

        return graph.findVertexByName(result.toString());

    }
}