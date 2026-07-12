package model;

/**
 * Ребро графа, соединяющее две вершины с заданным весом (длиной).
 * На прототипе валидация веса (отрицательные значения, петли, мультирёбра)
 * ещё не выполняется — появится в версии 2.
 */
public class Edge {
    private final Vertex from;
    private final Vertex to;
    private double weight;

    public Edge(Vertex from, Vertex to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Vertex getFrom() {
        return from;
    }

    public Vertex getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Возвращает вершину на другом конце ребра относительно переданной.
     * Пригодится позже в алгоритме Дейкстры для обхода соседей.
     */
    public Vertex getOtherEnd(Vertex vertex) {
        if (vertex.equals(from)) {
            return to;
        }
        if (vertex.equals(to)) {
            return from;
        }
        throw new IllegalArgumentException("Вершина " + vertex + " не принадлежит этому ребру");
    }

    @Override
    public String toString() {
        return from + " -- " + to + " (" + weight + ")";
    }
}
