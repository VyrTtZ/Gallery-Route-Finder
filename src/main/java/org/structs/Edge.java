package org.structs;

public class Edge {
    private Vertex start;
    private Vertex end;
    private int weight;
    private int id;

    public Edge(Vertex start, Vertex end, int weight, int id) {
        this.start = start;
        this.end = end;
        this.weight = weight;
        this.id = id;
    }

    public Vertex getEnd() {
        return end;
    }

    public void setEnd(Vertex end) {
        this.end = end;
    }

    public Vertex getStart() {
        return start;
    }

    public void setStart(Vertex start) {
        this.start = start;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
