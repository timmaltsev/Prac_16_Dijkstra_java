import model.Graph;
import ui.MainFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Graph graph = new Graph();

            MainFrame frame = new MainFrame(graph);
            frame.setVisible(true);

        });
    }
}