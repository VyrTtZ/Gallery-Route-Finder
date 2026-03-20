package org.structs;

import java.util.LinkedList;

public class Vertex {
    private int posX;
    private int posY;
    private int id;
    private String name;
    private LinkedList<Vertex> neighbors;

    public Vertex(int posX, int posY, int id, String name) {
        setPosX(posX);
        setPosY(posY);
        setId(id);
        this.name = name;
        this.neighbors = new LinkedList<>();
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Vertex> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(LinkedList<Vertex> neighbors) {
        this.neighbors = neighbors;
    }
}
