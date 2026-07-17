package algorithm;

import model.Edge;
import model.Graph;
import model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Пошаговая реализация алгоритма Дейкстры.
 *
 * Изменения по сравнению с предыдущей версией:
 * 1. Поиск ближайшей непосещённой вершины теперь через PriorityQueue
 *    (ленивое удаление устаревших записей), а не линейный перебор всех вершин.
 * 2. Появилась история снимков состояния — step() двигает алгоритм вперёд
 *    и запоминает снимок, stepBack() листает уже посчитанные снимки назад
 *    БЕЗ пересчёта. Реальные вычисления (distances/visited/predecessors)
 *    двигаются только вперёд — навигация назад работает поверх готовой истории.
 * 3. Лог (addLog/consumeLog) пишется только при РЕАЛЬНОМ вычислении шага,
 *    не при простом пролистывании уже посчитанной истории назад-вперёд —
 *    иначе в LogPanel задваивались бы одни и те же строки.
 */
public class DijkstraAlgorithm {
    private final List<String> log = new ArrayList<>();
    private int lastLogIndex = 0;
    private int stepNumber = 0;

    private final Graph graph;
    private final Vertex source;

    // "рабочее" состояние — то, чем реально считает алгоритм, двигается только вперёд
    private final Map<Vertex, Double> distances = new HashMap<>();
    private final Map<Vertex, Vertex> predecessors = new HashMap<>();
    private final Set<Vertex> visited = new HashSet<>();
    private boolean finished = false;

    // приоритетная очередь для быстрого выбора следующей вершины (ленивое удаление устаревших записей)
    private final PriorityQueue<VertexDistance> queue =
            new PriorityQueue<>(Comparator.comparingDouble(VertexDistance::getDistance));

    // история снимков состояния — по ней листает stepBack()/step(), не влияет на вычисления
    private final List<StepState> history = new ArrayList<>();
    private int viewIndex;

    private static final class StepState {
        final Vertex currentVertex;
        final Map<Vertex, Double> distances;
        final Set<Vertex> visited;
        final ArrayList<VertexDistance> queue;

        StepState(Vertex currentVertex, Map<Vertex, Double> distances, Set<Vertex> visited,
                    ArrayList<VertexDistance> queue) {
            this.currentVertex = currentVertex;
            this.distances = distances;
            this.visited = visited;
            this.queue = queue;
        }
    }

    public DijkstraAlgorithm(Graph graph, Vertex source) {
        this.graph = graph;
        this.source = source;

        for (Vertex vertex : graph.getVertices()) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }
        distances.put(source, 0.0);
        queue.add(new VertexDistance(source, 0.0));

        history.add(new StepState(null, new HashMap<>(distances), new HashSet<>(visited),
                    new ArrayList<>(queue)));
        viewIndex = 0;
    }

    /**
     * Шаг вперёд. Если пользователь до этого нажимал "назад" и мы находимся
     * внутри уже посчитанной истории — просто листаем её дальше, без пересчёта.
     * Если мы на границе истории — реально считаем новый шаг алгоритма.
     *
     * @return true, если реально удалось продвинуться (вычислить новый шаг или
     *         перейти к уже посчитанному); false, если двигаться больше некуда
     */
    public boolean step() {
        if (viewIndex < history.size() - 1) {
            viewIndex++;
            addLog("\u2192 Шаг вперёд (повтор из истории): " + describeState(history.get(viewIndex)));
            return true;
        }
        boolean computed = computeStep();
        if (computed) {
            viewIndex = history.size() - 1;
        }
        return computed;
    }

    /**
     * Шаг назад — листает историю снимков, вычисления не трогает.
     *
     * @return true, если получилось отступить назад; false, если мы уже в самом начале
     */
    public boolean stepBack() {
        if (viewIndex <= 0) {
            return false;
        }
        viewIndex--;
        addLog("\u2190 Шаг назад: " + describeState(history.get(viewIndex)));
        return true;
    }

    private String describeState(StepState state) {
        return state.currentVertex == null
                ? "начальное состояние (до первого шага)"
                : "вершина " + state.currentVertex.getName();
    }

    public boolean canStepForward() {
        return viewIndex < history.size() - 1 || !finished;
    }

    public boolean canStepBack() {
        return viewIndex > 0;
    }

    /**
     * Реальное вычисление одного шага — вызывается только когда мы на границе
     * истории (нечего листать дальше без пересчёта).
     */
    private boolean computeStep() {
        if (finished) {
            return false;
        }

        Vertex next = null;
        while (!queue.isEmpty()) {
            VertexDistance candidate = queue.poll();
            if (!visited.contains(candidate.getVertex())) {
                next = candidate.getVertex();
                break;
            }
            // иначе это устаревшая запись для уже обработанной вершины — пропускаем (ленивое удаление)
        }

        if (next == null) {
            finished = true;
            addLog("Алгоритм завершён: оставшиеся вершины недостижимы из " + source.getName());
            history.add(new StepState(null, new HashMap<>(distances), new HashSet<>(visited),
                        new ArrayList<>(queue)));
            return true;
        }

        stepNumber++;

        addLog("Шаг " + stepNumber);
        addLog("Рассматриваемая вершина: " + next.getName());
        visited.add(next);
        addLog("Очередь: " + formatQueue());
        addLog("Путь: " + formatPath(next));

        for (Edge edge : graph.getOutgoingEdges(next)) {
            Vertex neighbor = edge.getTo();

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
                queue.add(new VertexDistance(neighbor, newDistance));

            } else {

                addLog("Расстояние не изменилось.");

            }
        }

        addLog("Обновленная очередь: " + formatQueue());
        addLog("");

        if (visited.size() == graph.getVertices().size()) {
            finished = true;
        }

        history.add(new StepState(next, new HashMap<>(distances), new HashSet<>(visited),
                    new ArrayList<>(queue)));
        return true;
    }

    private String formatQueue() {

        List<Vertex> queueView = new ArrayList<>();

        for (Vertex vertex : graph.getVertices()) {
            if (!visited.contains(vertex)) {
                queueView.add(vertex);
            }
        }

        queueView.sort((v1, v2) ->
                Double.compare(distances.get(v1), distances.get(v2)));

        StringBuilder builder = new StringBuilder();

        for (Vertex vertex : queueView) {

            if (builder.length() > 0) {
                builder.append(", ");
            }

            builder.append(vertex.getName())
                   .append(" (");

            double distance = distances.get(vertex);

            if (Double.isInfinite(distance)) {
                builder.append("\u221e");
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
        while (canStepForward()) {
            step();
        }

        finishAlgorithm();
    }

    /**
     * Восстанавливает кратчайший путь от исходной вершины до target.
     * Осмысленный результат — только после завершения работы алгоритма (isFinished()).
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

    // ==== геттеры состояния — читают из ИСТОРИИ (viewIndex), а не из живых полей,
    //      чтобы stepBack() реально показывал прошлое состояние ====

    public Vertex getCurrentVertex() {
        return history.get(viewIndex).currentVertex;
    }

    public Map<Vertex, Double> getDistances() {
        return history.get(viewIndex).distances;
    }

    public Set<Vertex> getVisited() {
        return history.get(viewIndex).visited;
    }

    public boolean isFinished() {
        return finished;
    }

    public Vertex getSource() {
        return source;
    }

    public boolean isInQueue(Vertex vertex) {
        
        for (VertexDistance point : history.get(viewIndex).queue) {
            if (point.getVertex().equals(vertex))
                return true;
        }

        return false;
    }

    public Map<Vertex, Vertex> getPredecessors() {
        return predecessors;
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

    public void finishAlgorithm() {

        finished = true;

        addLog("");
        addLog("Алгоритм завершен.");

        if (visited.size() == graph.getVertices().size()) {
            addLog("Все вершины графа были достигнуты.");
        } else {
            addLog("Граф полностью не достижим из стартовой вершины.");
            addLog("Посещено вершин: "
                    + visited.size()
                    + " из "
                    + graph.getVertices().size());
        }

        addLog("");
        addLog("Итоговые кратчайшие расстояния:");

        List<Vertex> vertices = new ArrayList<>(graph.getVertices());
        vertices.sort((v1, v2) -> v1.getName().compareTo(v2.getName()));

        for (Vertex vertex : vertices) {

            double distance = distances.get(vertex);

            if (Double.isInfinite(distance)) {
                addLog(vertex.getName() + " : недостижима");
            } else {
                addLog(vertex.getName() + " : " + distance);
            }
        }

    }

}
