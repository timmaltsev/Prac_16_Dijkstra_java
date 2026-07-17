package ui;

import javax.swing.*;
import java.awt.*;

public final class Tips {

    private static boolean addVertexShown;
    private static boolean deleteVertexShown;
    private static boolean addEdgeShown;
    private static boolean deleteEdgeShown;

    private Tips() {}

    public static void showAddVertex(Component parent) {
        if (addVertexShown)
            return;

        JOptionPane.showMessageDialog(
                parent,
                "Щёлкните по свободному месту на графе, чтобы добавить вершину.",
                "Добавление вершины",
                JOptionPane.INFORMATION_MESSAGE);

        addVertexShown = true;
    }

    public static void showDeleteVertex(Component parent) {
        if (deleteVertexShown)
            return;

        JOptionPane.showMessageDialog(
                parent,
                "Щёлкните по вершине на графе, чтобы удалить ее.",
                "Удаление вершины",
                JOptionPane.INFORMATION_MESSAGE);

        deleteVertexShown = true;
    }

    public static void showAddEdge(Component parent) {
        if (addEdgeShown)
            return;

        JOptionPane.showMessageDialog(
                parent,
                "Выберите сначала выберите начало и конец ребра, потом укажите вес ребра",
                "Добавление ребра",
                JOptionPane.INFORMATION_MESSAGE);

        addEdgeShown = true;
    }

    public static void showDeleteEdge(Component parent) {
        if (deleteEdgeShown)
            return;

        JOptionPane.showMessageDialog(
                parent,
                "Кликните на ребро, которое хотите удалить",
                "Удаление ребра",
                JOptionPane.INFORMATION_MESSAGE);

        deleteEdgeShown = true;
    }
}
