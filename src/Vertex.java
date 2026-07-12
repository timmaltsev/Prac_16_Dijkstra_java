package model;

/**
 * Вершина графа. Хранит имя (уникальный идентификатор для пользователя)
 * и координаты для отрисовки на канвасе.
 */
public class Vertex {
    private final String name;
    private int x;
    private int y;

    public Vertex(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vertex)) return false;
        Vertex other = (Vertex) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
