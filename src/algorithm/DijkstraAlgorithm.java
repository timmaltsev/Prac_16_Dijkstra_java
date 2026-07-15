package algorithm;

import model.Edge;
import model.Graph;
import model.Vertex;

import java.util.*;

/*
 * Пошаговая реализация алгоритма Дейкстры
 * с использованием очереди с приоритетом.
 *
 * Алгоритм не занимается визуализацией. GUI после каждого шага
 * получает состояние через геттеры.
 */
public class DijkstraAlgorithm {

    private final Graph graph;

    private final Vertex source;

    /*
     * Минимальные найденные расстояния.
     */
    private final Map<Vertex, Double> distances = new HashMap<>();

    /*
     * Дерево кратчайших путей.
     */
    private final Map<Vertex, Vertex> predecessors = new HashMap<>();

    /*
     * Уже окончательно обработанные вершины.
     */
    private final Set<Vertex> visited = new HashSet<>();

    /*
     * Очередь с приоритетом.
     * Может содержать несколько записей одной вершины —
     * актуальной считается запись с минимальным расстоянием.
     */
    private final PriorityQueue<VertexDistance> queue =
            new PriorityQueue<>(
                    Comparator.comparingDouble(VertexDistance::getDistance));

    /*
     * Вершина, обработанная на текущем шаге.
     */
    private Vertex currentVertex;

    /*
     * Закончил ли работу алгоритм.
     */
    private boolean finished = false;

    public DijkstraAlgorithm(Graph graph, Vertex source) {

        this.graph = graph;
        this.source = source;

        for (Vertex vertex : graph.getVertices()) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }

        distances.put(source, 0.0);

        queue.add(new VertexDistance(source, 0.0));
    }

    /*
     * Выполнить один шаг алгоритма.
     *
     * Один шаг =
     * 1. взять вершину с минимальным расстоянием;
     * 2. сделать её посещённой;
     * 3. выполнить релаксацию всех исходящих рёбер.
     *
     * @return true, если шаг выполнен.
     */
    public boolean step() {

        if (finished) {
            return false;
        }

        System.out.println("шаг");

        VertexDistance current = null;

        while (!queue.isEmpty()) {

            VertexDistance candidate = queue.poll();

            Vertex vertex = candidate.getVertex();

            // вершина уже окончательно обработана
            if (visited.contains(vertex)) {
                continue;
            }

            // запись устарела
            if (candidate.getDistance()
                    != distances.get(vertex)) {

                continue;
            }

            current = candidate;
            break;
        }

        // очередь закончилась
        if (current == null) {

            finished = true;
            currentVertex = null;

            return false;
        }

        currentVertex = current.getVertex();

        visited.add(currentVertex);

        for (Edge edge : graph.getIncidentEdges(currentVertex)) {

            Vertex neighbour =
                    edge.getOtherEnd(currentVertex);

            if (visited.contains(neighbour)) {
                continue;
            }

            double newDistance =
                    distances.get(currentVertex)
                            + edge.getWeight();

            if (newDistance < distances.get(neighbour)) {

                distances.put(
                        neighbour,
                        newDistance);

                predecessors.put(
                        neighbour,
                        currentVertex);

                queue.add(
                        new VertexDistance(
                                neighbour,
                                newDistance));

            }

        }

        if (visited.size() == graph.getVertices().size()) {

            finished = true;

        }

        return true;

    }

    /**
     * Выполнить алгоритм полностью.
     */
    public void runToCompletion() {

        while (step()) {
            // Выполняем шаги до завершения
        }

    }

    /**
     * Восстановление кратчайшего пути
     * от source до target.
     */
    public PathResult getPath(Vertex target) {

        Double distance = distances.get(target);

        if (distance == null ||
                 distance == Double.POSITIVE_INFINITY) {

            return new PathResult(
                    Collections.emptyList(),
                    Double.POSITIVE_INFINITY);

        }

        List<Vertex> path = new ArrayList<>();

        Vertex current = target;

        while (current != null) {

            path.add(current);

            current = predecessors.get(current);

        }

        Collections.reverse(path);

        return new PathResult(path, distance);

    }

    /**
     * Возвращает содержимое очереди
     * в виде, удобном для отображения.
     *
     * Для каждой вершины оставляется
     * только минимальная запись.
     *
     * Уже посещённые вершины
     * в очередь не включаются.
     */
    public List<VertexDistance> getQueueSnapshot() {

        Map<Vertex, VertexDistance> best =
                new HashMap<>();

        for (VertexDistance vd : queue) {

            Vertex vertex = vd.getVertex();

            if (visited.contains(vertex)) {
                continue;
            }

            VertexDistance old = best.get(vertex);

            if (old == null ||
                     vd.getDistance() < old.getDistance()) {

                best.put(vertex, vd);

            }

        }

        List<VertexDistance> snapshot =
                new ArrayList<>(best.values());

        snapshot.sort(
                Comparator.comparingDouble(
                        VertexDistance::getDistance));

        return snapshot;

    }

    /**
     * Минимальные найденные расстояния.
     */
    public Map<Vertex, Double> getDistances() {
        return Collections.unmodifiableMap(distances);
    }

    /**
     * Дерево кратчайших путей.
     */
    public Map<Vertex, Vertex> getPredecessors() {
        return Collections.unmodifiableMap(predecessors);
    }

    /**
     * Посещённые вершины.
     */
    public Set<Vertex> getVisited() {
        return Collections.unmodifiableSet(visited);
    }

    /**
     * Очередная обработанная вершина.
     */
    public Vertex getCurrentVertex() {
        return currentVertex;
    }

    /**
     * Исходная вершина.
     */
    public Vertex getSource() {
        return source;
    }

    /**
     * Закончил ли работу алгоритм.
     */
    public boolean isFinished() {
        return finished;
    }

}