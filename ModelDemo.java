import model.Edge;
import model.Graph;
import model.Vertex;

/**
 * Временная консольная проверка модели графа, БЕЗ какого-либо GUI.
 * Нужна только чтобы убедиться, что классы model.* компилируются и работают
 * корректно, прежде чем Тимур подключит к ним отрисовку.
 * Можно удалить/заменить, когда появится реальная точка входа с Swing-окном.
 */

public class ModelDemo {
    public static void main(String[] args) {
        Graph graph = new Graph();

        Vertex a = graph.addVertex("A", 50, 50);
        Vertex b = graph.addVertex("B", 150, 50);
        Vertex c = graph.addVertex("C", 100, 150);

        Edge ab = graph.addEdge(a, b, 4.0);
        graph.addEdge(b, c, 2.5);
        graph.addEdge(a, c, 9.0);

        System.out.println("Вершины: " + graph.getVertices());
        System.out.println("Рёбра: " + graph.getEdges());
        System.out.println("Рёбра, инцидентные B: " + graph.getIncidentEdges(b));

        System.out.println("Другой конец ребра A--B относительно A: " + ab.getOtherEnd(a));

        graph.removeVertex(b);
        System.out.println("После удаления B:");
        System.out.println("Вершины: " + graph.getVertices());
        System.out.println("Рёбра (должны остаться только не связанные с B): " + graph.getEdges());
    }
}
