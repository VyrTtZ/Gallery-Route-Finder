package galleryRouteFinder.structs;

import javafx.scene.image.Image;
import javafx.util.Pair;

import java.util.*;

public class Vertex {
    private int posX;
    private int posY;
    private String name, category;
    private LinkedList<Vertex> neighbors;
    private LinkedList<Edge> branches;
    private Image image;
    private int id = 0;
    private static int nextId = 1;

    public Vertex(int id, int posX, int posY, String name, String category, Image image) {
        setPosX(posX);
        setPosY(posY);
        setId(id);
        setName(name);
        setCategory(category);
        this.neighbors = new LinkedList<>();
        this.branches = new LinkedList<>();
    }

    public Vertex(){
        setPosX(0);
        setPosY(0);
        setId(8888);
        image=null;
        category="DEFAULT";
        this.name = "DEFAULT";
        this.neighbors = new LinkedList<>();
        this.branches = new LinkedList<>();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
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
        LinkedList<int[]> bin = new LinkedList<>();
        if(wallsAndObjects != null)
            bin.addAll(wallsAndObjects);
        LinkedList<int[]> res = new LinkedList<>();
        Queue<LinkedList<int[]>> agenda = new LinkedList<>(new LinkedList<>());

        System.out.println("test1");

        bin.add(start);
        LinkedList<int[]> current = new LinkedList<>();
        current.add(start);

        System.out.println("test2");

        while(!agenda.isEmpty()){
            current = agenda.poll();
            int[] currentLast = current.getLast();


            if(currentLast == end) {
                return current;
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
                    current.add(i);
                    agenda.add(current);
                }
            }
        }

        return res;
    }

    public static ArrayList <Integer> inclusiveDijkstra(ArrayList<Vertex> included, ArrayList<Integer> excluded) {
        ArrayList <Integer> res=new ArrayList<>();
        for (int i=0; i<included.size()-1; i++){
            ArrayList<Integer> temp=dijkstra(included.get(i), included.get(i+1), excluded);
            res.addAll(temp);
        }
        return res;
    }

    private static ArrayList<Integer> dijkstra(Vertex startVertex, Vertex endVertex, ArrayList<Integer> excluded)
    {
        HashMap <Integer, Boolean> visited = new HashMap<>();
        if (excluded!=null)
            for (int i : excluded)
                visited.put(i, true);
        HashMap <Integer, Vertex> prev = new HashMap<>();
        PriorityQueue<Pair<Pair<Vertex, Vertex>, Double>> queue=new PriorityQueue<>((o1, o2) -> {
            if (o1.getValue()<o2.getValue())
                return -1;
            else if (o1.getValue()>o2.getValue())
                return 1;
            return 0;
        });
        queue.add(new Pair<>(new Pair<>(startVertex, startVertex), 0.0));

        while (!queue.isEmpty())
        {
            Pair<Pair<Vertex, Vertex>, Double> pair=queue.poll();
            Vertex currentVertex=pair.getKey().getValue();
            Vertex prevVertex=pair.getKey().getKey();
            double cost=pair.getValue();
            visited.put(currentVertex.getId(), true);
            prev.put(currentVertex.getId(), prevVertex);
            if (currentVertex.equals(endVertex))
                break;
            for (Edge edge: currentVertex.getBranches())
                for (Vertex tmp: edge.getNodes())
                    if (!tmp.equals(currentVertex) && (!visited.containsKey(tmp.getId()) || !visited.get(tmp.getId())))
                        queue.add(new Pair<>(new Pair<>(currentVertex, tmp), cost+edge.getWeight()));
        }
        ArrayList <Integer> path=new ArrayList<>();
        while (!endVertex.equals(startVertex))
        {
            path.add(endVertex.getId());
            endVertex=prev.get(endVertex.getId());
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
