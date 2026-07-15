package algorithm;

import model.Vertex;
import java.util.List;

/**
 * Результат восстановления кратчайшего пути между двумя вершинами:
 * сам путь по порядку и его суммарная длина.
 * Используется для отображения в версии 2 ("путь между двумя вершинами").
 */
public class PathResult {
    private final List<Vertex> path;
    private final double totalLength;

    public PathResult(List<Vertex> path, double totalLength) {
        this.path = path;
        this.totalLength = totalLength;
    }

    public List<Vertex> getPath() {
        return path;
    }

    public double getTotalLength() {
        return totalLength;
    }

    public boolean isReachable() {
        return !path.isEmpty();
    }
}
