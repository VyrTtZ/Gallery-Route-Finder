package galleryRouteFinder.structs;

import javafx.util.Pair;

import java.util.*;

public class Vertex {
    private int posX;
    private int posY;
    private String name;
    private LinkedList<Vertex> neighbors;
    private LinkedList<Edge> branches;
    private int id = 0;
    private static int nextId = 1;

    public Vertex(int id, int posX, int posY, String name) {
        setPosX(posX);
        setPosY(posY);
        setId(id);
        this.name = name;
        this.neighbors = new LinkedList<>();
        this.branches = new LinkedList<>();
    }

    public Vertex(){
        setPosX(0);
        setPosY(0);
        setId(8888);
        this.name = "DEFAULT";
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

    public void setId(int id){
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

    public static LinkedList<Vertex> BFS(Vertex start, Vertex end, LinkedList<Vertex> avoid) {
        Queue<LinkedList<Vertex>> agenda = new LinkedList<>();
        LinkedList<Vertex> visited = new LinkedList<>();

        if (avoid != null) visited.addAll(avoid);
        LinkedList<Vertex> firstPath = new LinkedList<>();
        firstPath.add(start);
        agenda.add(firstPath);
        visited.add(start);

        while (!agenda.isEmpty()) {
            LinkedList<Vertex> currentPath = agenda.poll();
            Vertex currentNode = currentPath.getLast();

            if (currentNode == end) {
                return currentPath;
            }

            for (Vertex neighbor : currentNode.getNeighbors()) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    LinkedList<Vertex> nextPath = new LinkedList<>(currentPath);
                    nextPath.add(neighbor);
                    agenda.add(nextPath);
                }
            }
        }
        return null;
    }

    public static LinkedList<int[]> BFS(int[] start, int[] end, LinkedList<int[]> wallsAndObjects){
        int[] current;
        LinkedList<int[]> bin = new LinkedList<>();
        bin.addAll(wallsAndObjects);
        System.out.println("test1");
        LinkedList<int[]> res = new LinkedList<>();
        Queue<int[]> neighborContainer = new LinkedList<>();

        bin.add(start);
        neighborContainer.add(start);
        System.out.println("test2");
        while(!neighborContainer.isEmpty()){
            current = neighborContainer.poll();
            res.add(current);
            if(current == end) {
                return res;
            }
            int[][] neighbors = {
                    {start[0] - 1, start[1] - 1},
                    {start[0], start[1] - 1},
                    {start[0] + 1, start[1] - 1},
                    {start[0] - 1, start[1]},
                    {start[0] + 1, start[1]},
                    {start[0] - 1, start[1] + 1},
                    {start[0], start[1] + 1},
                    {start[0] + 1, start[1] + 1}
            };

            for(int[] i : neighbors){ // helper bc concModException
                boolean found = false;
                for(int[] j : bin){
                    if(i[0] == j[0] && i[1] == j[1]){
                        found = true;
                        break;
                    }
                }
                if(!found){
                    bin.add(i);
                    neighborContainer.add(i);
                }
            }
        }

        return res;
    }

    public static ArrayList<Integer> dijkstraShortestPath(Vertex startVertex, Vertex endVertex, int maxVertices, ArrayList <Integer> excluded, ArrayList<Integer> included)
    {
        boolean[] visited=new boolean[maxVertices+1];
        if (excluded!=null)
            for (int i : excluded)
                visited[i]=true;
        Vertex[] prev=new Vertex[maxVertices+1];
        PriorityQueue<Pair<Pair<Vertex, Vertex>, Double>> queue=new PriorityQueue<>((o1, o2) -> {
            if (o1.getValue()<o2.getValue())
                return 1;
            else if (o1.getValue()>o2.getValue())
                return -1;
            return 0;
        });
        queue.add(new Pair<>(new Pair<>(startVertex, startVertex), 0.0));

        while (!queue.isEmpty())
        {
            Pair<Pair<Vertex, Vertex>, Double> pair=queue.poll();
            Vertex currentVertex=pair.getKey().getValue();
            Vertex prevVertex=pair.getKey().getKey();
            double cost=pair.getValue();
            visited[currentVertex.getId()]=true;
            prev[currentVertex.getId()]=prevVertex;
            if (currentVertex.equals(endVertex))
                break;
            for (Edge edge: currentVertex.getBranches())
                for (Vertex tmp: edge.getNodes())
                    if (!tmp.equals(currentVertex) && !visited[tmp.getId()])
                        queue.add(new Pair<>(new Pair<>(currentVertex, tmp), cost+edge.getWeight()));
        }
        ArrayList <Integer> path=new ArrayList<>();
        while (!endVertex.equals(startVertex))
        {
            path.add(endVertex.getId());
            endVertex=prev[endVertex.getId()];
        }
        path.add(startVertex.getId());
        ArrayList<Integer> res=new ArrayList<>();
        for (int i=path.size()-1; i>=0; i--)
            res.add(path.get(i));
        return res;
    }

    public LinkedList<Vertex> DFS(Vertex start, Vertex end, LinkedList<Vertex> avoid){
        LinkedList<Vertex> bin = new LinkedList<>();
        LinkedList<Vertex> res = new LinkedList<>();
        if(avoid != null) bin.addAll(avoid);
        dfsHelper(start, bin,end, res);
        return res;
    }

    public void dfsHelper(Vertex v, LinkedList<Vertex> bin, Vertex end, LinkedList<Vertex> res){

        bin.add(v);
        res.add(v);
        if(v == end) return;

        for(Vertex x : v.getNeighbors()){
            if(!bin.contains(x)){
                dfsHelper(x, bin, end, res);
                if (res.getLast() == end)
                    return;
            }
        }

        if(res.getLast() != end) res.removeLast();


    }








}
