package ui;

import model.Graph;
import model.Vertex;
import algorithm.DijkstraAlgorithm;
import input.JsonLoader;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame
                       implements EditorListener {

    private Graph graph;
    private final JsonLoader loader;

    private final GraphPanel graphPanel;
    private final ToolPanel toolPanel;
    private final ControlPanel controlPanel;
    private final LogPanel logPanel;

    private DijkstraAlgorithm algorithm;

    private JButton activeButton = null;

    public MainFrame(Graph graph) {

        this.graph = graph;
        this.loader = new JsonLoader();

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

        toolPanel.getNextStepButton().addActionListener(e ->
                makeAlgorithmStep()
        );

        controlPanel.getLoadButton().addActionListener(e -> loadGraph());

        controlPanel.getStartButton().addActionListener(e -> prepareAlgorithm());

        controlPanel.getClearButton().addActionListener(e -> clearGraph());


        graphPanel.setEditorListener(this);
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

    @Override
    public void sourceVertexSelected(Vertex sourceVertex){

        startAlgorithm(sourceVertex);
    }

    private void prepareAlgorithm() {
        if (graph.getVertices().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Граф пуст.",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (graph.getVertices().isEmpty()) {
            logPanel.log("Граф пуст.");
            return;
        }

        if (graphPanel.getMode() == EditorMode.SELECT_SOURCE) {

            modeFinished();

            logPanel.log("Запуск алгоритма отменён.");

            return;
        }

        graphPanel.setMode(EditorMode.SELECT_SOURCE);

        setActiveButton(controlPanel.getStartButton());

        logPanel.log("Выберите начальную вершину.");
    }

    private void startAlgorithm(Vertex sourceVertex) {

        AlgorithmDialog dialog = new AlgorithmDialog(this);
        dialog.setVisible(true);

        AlgorithmMode mode = dialog.getSelectedMode();

        if (mode == null) {
            return; // пользователь нажал "Отмена"
        }

        algorithm = new DijkstraAlgorithm(graph, sourceVertex);

        if (mode == AlgorithmMode.INSTANT) {

            algorithm.runToCompletion();

            logPanel.showAlgorithmResult(algorithm);

            finishAlgorithm();

        } else {

            logPanel.log("Алгоритм готов к выполнению.");
            logPanel.log("Нажмите \"Следующий шаг\" - \"→\".");
        }

    }

    public void makeAlgorithmStep(){

        if (algorithm.isFinished()) {
            finishAlgorithm();
            return;
        }

        algorithm.step();
        logPanel.logMultiple(algorithm.consumeLog());
    }

    private void finishAlgorithm() {

        JOptionPane.showMessageDialog(
                    this,
                    "Алгоритм выполнен.");

        modeFinished();
    }

    private void clearGraph() {

        graphPanel.clear();
    }

    private void loadGraph() {

        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {

            graph = loader.load(
                    chooser.getSelectedFile(),
                    graphPanel.getSize());

            graphPanel.setGraph(graph);

            graphPanel.repaint();

            logPanel.log("Граф успешно загружен.");

        }
        catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);

        }

    }
}