package algorithm;

import model.Edge;
import model.Graph;
import model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Пошаговая реализация алгоритма Дейкстры.
 *
 * Алгоритм НЕ занимается отрисовкой — только считает и хранит состояние.
 * GUI вызывает step() на каждый клик "Шаг" (или в цикле для "Мгновенно"),
 * и после каждого шага может спросить getCurrentVertex()/getDistances()/getVisited(),
 * чтобы обновить подсветку и текстовые пояснения.
 */
public class DijkstraAlgorithm {
    private final List<String> log = new ArrayList<>();
    private int lastLogIndex = 0;
    private int stepNumber = 0;

    private final Graph graph;
    private final Vertex source;

    private final Map<Vertex, Double> distances = new HashMap<>();
    private final Map<Vertex, Vertex> predecessors = new HashMap<>();
    private final Set<Vertex> visited = new HashSet<>();

    private Vertex currentVertex;
    private boolean finished = false;

    public DijkstraAlgorithm(Graph graph, Vertex source) {
        this.graph = graph;
        this.source = source;

        for (Vertex vertex : graph.getVertices()) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }
        distances.put(source, 0.0);
    }

    /**
     * Выполняет ровно один шаг алгоритма: находит ближайшую непосещённую
     * вершину, помечает её посещённой и пересчитывает расстояния до соседей.
     *
     * @return true, если шаг был выполнен; false, если работать больше не над чем
     */
    public boolean step() {

        if (finished) {
            return false;
        }

        Vertex next = findClosestUnvisited();

        if (next == null || distances.get(next) == Double.POSITIVE_INFINITY) {
            finished = true;
            currentVertex = null;
            return false;
        }

        stepNumber++;

        addLog("Шаг " + stepNumber);
        addLog("Рассматриваемая вершина: " + next.getName());
        currentVertex = next;
        visited.add(next);
        addLog("Очередь: " + formatQueue());
        addLog("Путь: " + formatPath(next));
        for (Edge edge : graph.getIncidentEdges(next)) {

            Vertex neighbor = edge.getOtherEnd(next);

            if (visited.contains(neighbor)) {
                continue;
            }

            addLog("Проверяем вершину " + neighbor.getName());

            double newDistance = distances.get(next) + edge.getWeight();

            if (newDistance < distances.get(neighbor)) {

                addLog("Расстояние обновлено: "
                        + distances.get(neighbor)
                        + " -> "
                        + newDistance);

                distances.put(neighbor, newDistance);
                predecessors.put(neighbor, next);

            } else {

                addLog("Расстояние не изменилось.");

            }
        }

        addLog("Обновленная очередь: " + formatQueue());
        addLog("");

        if (visited.size() == graph.getVertices().size()) {
            finished = true;
        }

        return true;
    }
    private String formatQueue() {

        List<Vertex> queue = new ArrayList<>();

        for (Vertex vertex : graph.getVertices()) {
            if (!visited.contains(vertex)) {
                queue.add(vertex);
            }
        }

        queue.sort((v1, v2) ->
                Double.compare(distances.get(v1), distances.get(v2)));

        StringBuilder builder = new StringBuilder();

        for (Vertex vertex : queue) {

            if (builder.length() > 0) {
                builder.append(", ");
            }

            builder.append(vertex.getName())
                   .append(" (");

            double distance = distances.get(vertex);

            if (Double.isInfinite(distance)) {
                builder.append("∞");
            } else {
                builder.append(distance);
            }

            builder.append(")");
        }

        return builder.toString();
    }
    private String formatPath(Vertex target) {

        List<Vertex> path = new ArrayList<>();

        Vertex current = target;

        while (current != null) {
            path.add(current);
            current = predecessors.get(current);
        }

        Collections.reverse(path);

        StringBuilder builder = new StringBuilder();

        for (Vertex vertex : path) {

            if (builder.length() > 0) {
                builder.append(" -> ");
            }

            builder.append(vertex.getName());
        }

        return builder.toString();
    }

    /**
     * Выполняет все оставшиеся шаги разом — используется для режима "Мгновенно".
     */
    public void runToCompletion() {
        while (step()) {
            // просто крутим шаги, пока не закончится
        }
    }

    private Vertex findClosestUnvisited() {
        Vertex closest = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (Vertex vertex : graph.getVertices()) {
            if (visited.contains(vertex)) {
                continue;
            }
            double d = distances.get(vertex);
            if (d < bestDistance) {
                bestDistance = d;
                closest = vertex;
            }
        }
        return closest;
    }

    /**
     * Восстанавливает кратчайший путь от исходной вершины до target.
     * Можно вызывать в любой момент, но осмысленный результат — только
     * после завершения работы алгоритма (runToCompletion() или step() до false).
     */
    public PathResult getPath(Vertex target) {
        if (!distances.containsKey(target) || distances.get(target) == Double.POSITIVE_INFINITY) {
            return new PathResult(Collections.emptyList(), Double.POSITIVE_INFINITY);
        }

        List<Vertex> path = new ArrayList<>();
        Vertex step = target;
        while (step != null) {
            path.add(step);
            step = predecessors.get(step);
        }
        Collections.reverse(path);

        return new PathResult(path, distances.get(target));
    }

    // ==== геттеры состояния — для GUI, чтобы рисовать текущий шаг ====

    public Vertex getCurrentVertex() {
        return currentVertex;
    }

    public Map<Vertex, Double> getDistances() {
        return distances;
    }

    public Set<Vertex> getVisited() {
        return visited;
    }

    public boolean isFinished() {
        return finished;
    }

    public Vertex getSource() {
        return source;
    }

    private void addLog(String message) {
        log.add(message);
    }

    public List<String> consumeLog() {

        List<String> result = new ArrayList<>(
                log.subList(lastLogIndex, log.size())
        );

        lastLogIndex = log.size();

        return result;
    }
}
