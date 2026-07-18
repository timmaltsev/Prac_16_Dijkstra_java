package algorithm;

import model.Vertex;

import java.util.Collections;
import java.util.List;

/**
 * Результат восстановления кратчайшего пути.
 *
 * Хранит:
 * - последовательность вершин пути;
 * - общую длину пути.
 */
public class PathResult {

    private final List<Vertex> path;
    private final double distance;

    public PathResult(List<Vertex> path, double distance) {
        this.path = List.copyOf(path);
        this.distance = distance;
    }

    /**
     * Последовательность вершин кратчайшего пути.
     */
    public List<Vertex> getPath() {
        return Collections.unmodifiableList(path);
    }

    /**
     * Общая длина пути.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Существует ли путь.
     */
    public boolean exists() {
        return distance != Double.POSITIVE_INFINITY;
    }

    @Override
    public String toString() {
        if (!exists()) {
            return "Путь отсутствует";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < path.size(); i++) {

            sb.append(path.get(i).getName());

            if (i != path.size() - 1) {
                sb.append(" → ");
            }
        }

        sb.append(" (").append(distance).append(")");

        return sb.toString();
    }
}
