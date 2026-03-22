package galleryRouteFinder.structs;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Vertex {
    private int posX;
    private int posY;
    private String name;
    private LinkedList<Vertex> neighbors;
    private LinkedList<Edge> branches;
    private int id = 0;
    private static int nextId = 1;

    public Vertex(int posX, int posY, String name) {
        setPosX(posX);
        setPosY(posY);
        setId();
        this.name = name;
        this.neighbors = new LinkedList<>();
        this.branches = new LinkedList<>();
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

    public void setId(){
        this.id = nextId++;
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

    public void setNeighbors() {
        neighbors.clear();
        for(Edge i : branches){
            neighbors.add(i.getNode2());
        }

    }

    public LinkedList<Edge> getBranches() {
        return branches;
    }

    public void setBranches() {
        branches.clear();
        for(Vertex i : neighbors){
                branches.add(new Edge(this, i));
        }
    }

    public void addNeighbor(Vertex node){
        neighbors.add(node);
        setBranches();
        setNeighbors();

    }

    public void removeNeighbor(Vertex v){
        for(Edge e : branches){
            if(e.getNode2() == v) branches.remove(e);
        }
        neighbors.remove(v);
        setNeighbors();
        setBranches();

    }

    public LinkedList<Vertex> BFS(Vertex start, Vertex end, LinkedList<Vertex> avoid){
        Vertex current;
        HashSet<Vertex> bin = new HashSet<>();
        LinkedList<Vertex> res = new LinkedList<>();
        Queue<Vertex> neighborContainer = new LinkedList<>();

        if(avoid != null)
            bin.addAll(avoid);
        bin.add(start);
        neighborContainer.add(start);

        while(!neighborContainer.isEmpty()){
            current = neighborContainer.poll();
            res.add(current);
            if(current == end) {
                return res;
            }
            for(Vertex v : current.getNeighbors()){
                if(!bin.contains(v)) {
                    bin.add(v);
                    neighborContainer.add(v);
                }
            }
        }

        return res;
    }

    public LinkedList<Vertex> DFS(Vertex start, Vertex end, LinkedList<Vertex> avoid){
        HashSet<Vertex> bin = new HashSet<>();
        LinkedList<Vertex> res = new LinkedList<>();
        dfsSlave(start, bin,end, res, avoid);
        return res;
    }

    public boolean dfsSlave(Vertex v, HashSet<Vertex> bin, Vertex end, LinkedList<Vertex> res, LinkedList<Vertex> avoidSet){
        if(avoidSet != null) bin.addAll(avoidSet);
        bin.add(v);
        res.add(v);
        if(v == end) return true;

        for(Vertex x : v.getNeighbors()){
            if(!bin.contains(x)){
                if(dfsSlave(x, bin, end, res, avoidSet)) return true;
            }
        }

        res.removeLast();
        return false;
    }








}
