package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Граф — хранит список вершин и рёбер, предоставляет базовые операции
 * добавления/удаления. Никакой привязки к алгоритму Дейкстры здесь нет —
 * по плану прототипа это только структура данных.
 */
public class Graph {
    public static final double MIN_VERTEX_DISTANCE = 50;
    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    public Vertex addVertex(String name, int x, int y) {

        if (findVertexByName(name) != null) {
            throw new IllegalArgumentException(
                    "Вершина с именем \"" + name + "\" уже существует."
            );
        }

        for (Vertex vertex : vertices) {

            double distance = Math.hypot(
                    vertex.getX() - x,
                    vertex.getY() - y
            );

            if (distance < MIN_VERTEX_DISTANCE) {
                throw new IllegalArgumentException(
                        "Вершины расположены слишком близко друг к другу."
                );
            }
        }
        Vertex vertex = new Vertex(name, x, y);
        vertices.add(vertex);
        return vertex;
    }

    /**
     * Удаляет вершину и все инцидентные ей рёбра
     * (требование из спецификации: "удаление вершины со всеми инцидентными ей рёбрами").
     */
    public void removeVertex(Vertex vertex) {
        vertices.remove(vertex);
        edges.removeIf(edge -> edge.getFrom().equals(vertex) || edge.getTo().equals(vertex));
    }

    public Edge addEdge(Vertex from, Vertex to, double weight) {
        if (from.equals(to)) {
            throw new IllegalArgumentException(
                    "Нельзя создать петлю."
            );
        }

        if (weight <= 0) {
            throw new IllegalArgumentException(
                    "Вес ребра должен быть больше нуля."
            );
        }

        for (Edge edge : edges) {
            if (edge.getFrom().equals(from)
                    && edge.getTo().equals(to)) {

                throw new IllegalArgumentException(
                        "Нельзя создавать мультиребро."
                );
            }
        }
        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        return edge;
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Возвращает все рёбра, инцидентные данной вершине — пригодится
     * позже алгоритму Дейкстры для перебора соседей.
     */
    public List<Edge> getIncidentEdges(Vertex vertex) {
        List<Edge> result = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getFrom().equals(vertex) || edge.getTo().equals(vertex)) {
                result.add(edge);
            }
        }
        return result;
    }

    public Vertex findVertexByName(String name) {
        for (Vertex vertex : vertices) {
            if (vertex.getName().equals(name)) {
                return vertex;
            }
        }
        return null;
    }

    public List<Edge> getOutgoingEdges(Vertex vertex) {
        List<Edge> result = new ArrayList<>();

        for (Edge edge : edges) {
            if (edge.getFrom().equals(vertex)) {
                result.add(edge);
            }
        }

        return result;
    }
}
