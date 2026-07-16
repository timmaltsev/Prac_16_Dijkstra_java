package ui;

import model.Edge;
import model.Graph;
import model.Vertex;

import javax.swing.*;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GraphPanel extends JPanel {

    private final Graph graph;


    private EditorMode mode = EditorMode.NONE;

    private EditorListener listener;

    private Vertex firstVertex = null;
    private Vertex sourceVertex = null;

    private static final int RADIUS = 20;

    private static final int EDGE_TOLERANCE = 8;

    public GraphPanel(Graph graph) {

        this.graph = graph;

        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });

    }

    public void setEditorListener(EditorListener listener) {
        this.listener = listener;
    }

    public EditorMode getMode() {
        return mode;
    }

    public void setMode(EditorMode mode) {

        this.mode = mode;
        firstVertex = null;

        repaint();
    }

    private void handleMouseClick(int x, int y) {

        switch (mode) {

            case ADD_VERTEX:
                addVertex(x, y);
                break;

            case ADD_EDGE:

                addEdge(x, y);
                break;

            case DELETE_VERTEX:

                deleteVertex(x, y);
                break;

            case DELETE_EDGE:

                deleteEdge(x, y);
                break;

            case SELECT_SOURCE:
                selectSource(x, y);
                break;

            default:
                break;
        }
    }

    /**
     * Добавление вершины.
     */
    private void addVertex(int x, int y) {

        String name = JOptionPane.showInputDialog(
                this,
                "Введите название вершины:"
        );

        if (name == null)
            return;

        name = name.trim();

        if (name.isEmpty())
            return;

        try { graph.addVertex(name, x, y); }
        catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        if (listener != null) {
            listener.modeFinished();
        }

        repaint();
    }

    /**
     * Добавление ребра.
     */
    private void addEdge(int x, int y) {

        Vertex vertex = findVertex(x, y);

        if (vertex == null)
            return;

        if (firstVertex == null) {

            firstVertex = vertex;

            repaint();

            return;
        }

        String input = JOptionPane.showInputDialog(
                this,
                "Введите вес ребра:"
        );

        if (input == null)
            return;

        try {

            double weight = Double.parseDouble(input);

            graph.addEdge(firstVertex, vertex, weight);

            firstVertex = null;

            if (listener != null){
                listener.modeFinished();
            }

            repaint();
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Вес должен быть числом.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Удаление вершины.
     */
    private void deleteVertex(int x, int y) {

        Vertex vertex = findVertex(x, y);

        if (vertex == null)
            return;

        graph.removeVertex(vertex);

        if (listener != null) {
            listener.modeFinished();
        }

        repaint();

    }

    /**
     * Удаление ребра.
     */
    private void deleteEdge(int x, int y) {

        Edge edge = findEdge(x, y);

        if (edge == null)
            return;

        graph.removeEdge(edge);

        if (listener != null) {
            listener.modeFinished();
        }

        repaint();
    }

    private void selectSource(int x, int y) {
        sourceVertex = findVertex(x, y);

        if (sourceVertex == null) {
            return;
        }

        if (listener != null) {
            listener.sourceVertexSelected(sourceVertex);
        }

        repaint();
    }

    /**
     * Поиск вершины по координатам.
     */
    private Vertex findVertex(int x, int y) {

        for (Vertex vertex : graph.getVertices()) {

            int dx = x - vertex.getX();
            int dy = y - vertex.getY();

            if (dx * dx + dy * dy <= RADIUS * RADIUS)
                return vertex;
        }

        return null;
    }

    /**
     * Поиск ребра по координатам.
     */
    private Edge findEdge(int x, int y) {

        for (Edge edge : graph.getEdges()) {

            double distance = distanceToSegment(
                    x,
                    y,
                    edge.getFrom().getX(),
                    edge.getFrom().getY(),
                    edge.getTo().getX(),
                    edge.getTo().getY()
            );

            if (distance <= EDGE_TOLERANCE)
                return edge;
        }
        return null;
    }

    /**
     * Расстояние от точки до отрезка.
     */
    private double distanceToSegment(
            double px,
            double py,
            double x1,
            double y1,
            double x2,
            double y2) {

        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0)
            return Point.distance(px, py, x1, y1);

        double t =
                ((px - x1) * dx + (py - y1) * dy)
                        / (dx * dx + dy * dy);

        t = Math.max(0, Math.min(1, t));

        double nearestX = x1 + t * dx;
        double nearestY = y1 + t * dy;

        return Point.distance(px, py, nearestX, nearestY);
    }

    /**
     * Рисование ориентированного ребра.
     */
    private void drawDirectedEdge(Graphics2D g2, Edge edge) {

        Vertex from = edge.getFrom();
        Vertex to = edge.getTo();

        double x1 = from.getX();
        double y1 = from.getY();

        double x2 = to.getX();
        double y2 = to.getY();

        double angle = Math.atan2(y2 - y1, x2 - x1);

        int startX = (int) (x1 + RADIUS * Math.cos(angle));
        int startY = (int) (y1 + RADIUS * Math.sin(angle));

        int endX = (int) (x2 - RADIUS * Math.cos(angle));
        int endY = (int) (y2 - RADIUS * Math.sin(angle));

        g2.drawLine(startX, startY, endX, endY);

        int arrowLength = 12;
        double arrowAngle = Math.toRadians(25);

        int xArrow1 = (int) (
                endX - arrowLength * Math.cos(angle - arrowAngle));

        int yArrow1 = (int) (
                endY - arrowLength * Math.sin(angle - arrowAngle));

        int xArrow2 = (int) (
                endX - arrowLength * Math.cos(angle + arrowAngle));

        int yArrow2 = (int) (
                endY - arrowLength * Math.sin(angle + arrowAngle));

        g2.drawLine(endX, endY, xArrow1, yArrow1);
        g2.drawLine(endX, endY, xArrow2, yArrow2);

        String weight = String.valueOf(edge.getWeight());

        FontMetrics fm = g2.getFontMetrics();

        int textX = (startX + endX) / 2;
        int textY = (startY + endY) / 2;

        g2.drawString(
                weight,
                textX - fm.stringWidth(weight) / 2,
                textY - 5
        );
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);


        g2.setColor(Color.BLACK);

        for (Edge edge : graph.getEdges()) {
            drawDirectedEdge(g2, edge);
        }


        for (Vertex vertex : graph.getVertices()) {

            int x = vertex.getX();
            int y = vertex.getY();

            if (vertex.equals(firstVertex))
                g2.setColor(Color.RED);
            else if (vertex.equals(sourceVertex))
                g2.setColor(new Color(0, 128, 255));
            else
                g2.setColor(new Color(255, 180, 0));

            g2.fillOval(
                    x - RADIUS,
                    y - RADIUS,
                    RADIUS * 2,
                    RADIUS * 2
            );

            g2.setColor(Color.BLACK);

            g2.drawOval(
                    x - RADIUS,
                    y - RADIUS,
                    RADIUS * 2,
                    RADIUS * 2
            );

            FontMetrics fm = g2.getFontMetrics();

            int tx = x - fm.stringWidth(vertex.getName()) / 2;
            int ty = y + fm.getAscent() / 2 - 2;

            g2.drawString(vertex.getName(), tx, ty);
        }

    }
}
