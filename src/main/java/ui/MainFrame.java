package ui;

import algorithm.DijkstraAlgorithm;
import model.Graph;
import model.Vertex;
import input.JsonLoader;
import algorithm.PathResult;

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

    private JButton activeButton = null;

    private DijkstraAlgorithm algorithm;

    public MainFrame(Graph graph) {

        this.graph = graph;
        this.loader = new JsonLoader();

        setTitle("Версия 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        graphPanel = new GraphPanel(graph);
        graphPanel.setEditorListener(this);

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

        // ВАЖНО: раньше здесь было ДВА addActionListener на getStartButton() —
        // первый сразу открывал messagebox и ставил SELECT_SOURCE, второй (prepareAlgorithm)
        // видел уже выставленный SELECT_SOURCE и тут же расценивал это как "повторный клик = отмена".
        // Кнопка Start из-за этого гасила сама себя в один клик. Оставляем только один обработчик.
        controlPanel.getStartButton().addActionListener(e -> prepareAlgorithm());

        toolPanel.getNextStepButton().addActionListener(e -> makeAlgorithmStep());

        toolPanel.getPreviousStepButton().addActionListener(e -> makeAlgorithmStepBack());

        toolPanel.getPathViewButton().addActionListener(e -> {

                logPanel.log("Выберите конечную вершину.");

                toggleMode(EditorMode.VIEW_PATH,
                        toolPanel.getPathViewButton());
        });

        controlPanel.getLoadButton().addActionListener(e -> loadGraph());

        controlPanel.getClearButton().addActionListener(e -> clearGraph());

        graphPanel.setEditorListener(this);

        updateStepButtons();
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

    @Override
    public void sourceVertexSelected(Vertex sourceVertex){

        startAlgorithm(sourceVertex);
    }

    @Override
    public void onPathSelected(PathResult result){

        modeFinished();

        logPanel.log("Путь: " + result.toString());
        logPanel.log("Длина пути: " + result.getDistance());
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
            modeFinished();
            return; // пользователь нажал "Отмена"
        }

        algorithm = new DijkstraAlgorithm(graph, sourceVertex);

        graphPanel.setAlgorithm(algorithm);

        logPanel.clear();

        if (mode == AlgorithmMode.INSTANT) {

            algorithm.runToCompletion();

            logPanel.showAlgorithmResult(algorithm);

            JOptionPane.showMessageDialog(this, "Алгоритм выполнен.");

            modeFinished();

        } else {

            logPanel.log("Алгоритм готов к выполнению.");
            logPanel.log("Нажмите \"Следующий шаг\" - \"→\".");

            modeFinished();
        }

        graphPanel.repaint();
        updateStepButtons();
    }

    public void makeAlgorithmStep(){

        if (algorithm == null || !algorithm.canStepForward()) {
            return;
        }

        algorithm.step();
        logPanel.logMultiple(algorithm.consumeLog());

        if (algorithm.isFinished() && !algorithm.canStepForward()) {
            algorithm.finishAlgorithm();
            logPanel.logMultiple(algorithm.consumeLog());
            JOptionPane.showMessageDialog(this, "Алгоритм выполнен.");
        }

        graphPanel.repaint();
        updateStepButtons();
    }

    public void makeAlgorithmStepBack(){

        if (algorithm == null || !algorithm.canStepBack()) {
            return;
        }

        algorithm.stepBack();
        logPanel.logMultiple(algorithm.consumeLog());

        graphPanel.repaint();
        updateStepButtons();
    }

    /**
     * Держит кнопки "←"/"→" в актуальном состоянии — недоступны, когда
     * двигаться в эту сторону уже некуда. Заодно избавляет от повторного
     * всплывающего "Алгоритм выполнен" при накликивании "→" после конца.
     */
    private void updateStepButtons() {

        boolean hasAlgorithm = algorithm != null;

        toolPanel.getNextStepButton().setEnabled(
                hasAlgorithm && algorithm.canStepForward());

        toolPanel.getPreviousStepButton().setEnabled(
                hasAlgorithm && algorithm.canStepBack());
    }

    private void clearGraph() {

        graphPanel.clear();
        algorithm = null;
        updateStepButtons();
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

            algorithm = null;
            updateStepButtons();

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
