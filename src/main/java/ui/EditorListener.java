package ui;

import model.Vertex;
import algorithm.PathResult;

public interface EditorListener {
    /**
     * Завершение текущего режима.
     */
    void modeFinished();

    /**
     * Запуск алгоритма.
     */
    void sourceVertexSelected(Vertex sourceVertex);

    /**
     * Выбор вершины для просмотра пути.
     * @param result
     */
    public void onPathSelected(PathResult result);
}
