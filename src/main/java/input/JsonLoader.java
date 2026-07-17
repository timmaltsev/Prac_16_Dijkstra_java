package input;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Graph;
import model.Vertex;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JsonLoader {

    private static final int MARGIN = 40;
    private static final int MAX_ATTEMPTS = 1000;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();

    /**
     * Загружает граф из JSON.
     *
     * @param drawingArea размер области рисования графа
     */
    public Graph load(File file, Dimension drawingArea) throws IOException {

        Map<String, List<EdgeData>> adjacency =
                mapper.readValue(
                        file,
                        new TypeReference<Map<String, List<EdgeData>>>() {
                        });

        Graph graph = new Graph();

        /*
         * Сначала создаем все вершины.
         */
        for (String name : adjacency.keySet()) {

            Point position = generatePosition(graph, drawingArea);

            graph.addVertex(
                    name,
                    position.x,
                    position.y);

        }

        /*
         * Затем создаем все ребра.
         */
        for (Map.Entry<String, List<EdgeData>> entry : adjacency.entrySet()) {

            Vertex from =
                    graph.findVertexByName(entry.getKey());

            for (EdgeData edge : entry.getValue()) {

                Vertex to =
                        graph.findVertexByName(edge.to);

                if (to == null) {
                    throw new IOException(
                            "Вершина \"" + edge.to + "\" отсутствует.");
                }

                graph.addEdge(from, to, edge.weight);
            }
        }

        return graph;
    }

    /**
     * Генерирует координаты новой вершины.
     */
    private Point generatePosition(
            Graph graph,
            Dimension drawingArea) {

        int width = drawingArea.width;
        int height = drawingArea.height;

        for (int attempt = 0;
             attempt < MAX_ATTEMPTS;
             attempt++) {

            int x = random.nextInt(
                    width - 2 * MARGIN)
                    + MARGIN;

            int y = random.nextInt(
                    height - 2 * MARGIN)
                    + MARGIN;

            boolean ok = true;

            for (Vertex vertex : graph.getVertices()) {

                double distance = Math.hypot(
                        x - vertex.getX(),
                        y - vertex.getY());

                if (distance < Graph.MIN_VERTEX_DISTANCE) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                return new Point(x, y);
            }
        }

        throw new IllegalStateException(
                "Недостаточно места для размещения вершин.");
    }

    /**
     * Один элемент списка смежности.
     */
    public static class EdgeData {

        public String to;

        public double weight;

    }

}