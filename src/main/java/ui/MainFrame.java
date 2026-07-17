package ui;

import algorithm.DijkstraAlgorithm;
import model.Graph;
import model.Vertex;
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

    private JButton activeButton = null;

    private AlgorithmMode algorithmMode;
    private DijkstraAlgorithm algorithm;

    private boolean graphEditingLocked = false;


    public MainFrame(Graph graph) {

        this.graph = graph;
        this.loader = new JsonLoader();

        setTitle("Версия 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        graphPanel = new GraphPanel(graph);
        graphPanel.setEditorListener(this);

        toolPanel = new ToolPanel();
        controlPanel = new ControlPanel();
        logPanel = new LogPanel();

/*
 * TODO: я не знаю что это пока
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
*/

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

        controlPanel.getStartButton().addActionListener(e -> prepareAlgorithm());
        toolPanel.getNextStepButton().addActionListener(e -> makeAlgorithmStep());
        toolPanel.getPreviousStepButton().addActionListener(e -> makeAlgorithmStepBack());
    }

    private void toggleMode(EditorMode mode, JButton button) {

        if (graphEditingLocked) {
            JOptionPane.showMessageDialog(
                    this,
                    "Нельзя изменять граф, пока алгоритм выполняется в пошаговом режиме.",
                    "Алгоритм выполняется",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

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


    private void prepareAlgorithm() {
        if (graphEditingLocked) {
            JOptionPane.showMessageDialog(
                    this,
                    "Завершите пошаговое выполнение алгоритма перед новым запуском.",
                    "Алгоритм выполняется",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

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

        AlgorithmDialog dialog = new AlgorithmDialog(this);
        dialog.setVisible(true);

        algorithmMode = dialog.getSelectedMode();
        if (algorithmMode == null) {
            return;
        }

        graphPanel.setMode(EditorMode.SELECT_SOURCE);
        setActiveButton(controlPanel.getStartButton());
        logPanel.log("Выберите начальную вершину.");
    }

    private void startAlgorithm(Vertex sourceVertex) {
        algorithm = new DijkstraAlgorithm(graph, sourceVertex);
        logPanel.clear();
        logPanel.log("Начальная вершина: " + sourceVertex.getName());

        if (algorithmMode == AlgorithmMode.INSTANT) {
            algorithm.runToCompletion();
            logPanel.showAlgorithmResult(algorithm);
            finishAlgorithm();
        } else {
            setGraphEditingLocked(true);
            algorithm.step();
            logPanel.showAlgorithmResult(algorithm);
        }

        modeFinished();
        graphPanel.repaint();
    }

    public void makeAlgorithmStep() {
        if (algorithm == null) {
            return;
        }

        if (algorithm.step()) {
            logPanel.logMultiple(algorithm.consumeLog());
            graphPanel.repaint();
            return;
        }

        finishAlgorithm();
    }

    private void makeAlgorithmStepBack() {
        if (algorithm == null) {
            return;
        }

        if (algorithm.stepBack()) {
            logPanel.logMultiple(algorithm.consumeLog());
            graphPanel.repaint();
        }
    }

    private void finishAlgorithm() {
        logPanel.log("Алгоритм завершён.");
        JOptionPane.showMessageDialog(this, "Алгоритм выполнен.");
        setGraphEditingLocked(false);
        algorithm = null;
        modeFinished();
    }

    private void setGraphEditingLocked(boolean locked) {
        graphEditingLocked = locked;
        toolPanel.getAddVertexButton().setEnabled(!locked);
        toolPanel.getAddEdgeButton().setEnabled(!locked);
        toolPanel.getDeleteVertexButton().setEnabled(!locked);
        toolPanel.getDeleteEdgeButton().setEnabled(!locked);
    }

}
