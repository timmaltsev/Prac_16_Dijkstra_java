package algorithm;

import model.Vertex;

public class VertexDistance {

    private final Vertex vertex;

    private final double distance;

    public VertexDistance(
            Vertex vertex,
            double distance) {

        this.vertex = vertex;
        this.distance = distance;

    }

    public Vertex getVertex() {
        return vertex;
    }

    public double getDistance() {
        return distance;
    }

}