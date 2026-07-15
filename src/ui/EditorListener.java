package ui;

import model.Vertex;

public interface EditorListener {

    /**
     * Завершение текущего режима.
     */
    void modeFinished();

    /**
     * Запускк алгоритма
     */
    void sourceVertexSelected(Vertex sourceVertex);
}