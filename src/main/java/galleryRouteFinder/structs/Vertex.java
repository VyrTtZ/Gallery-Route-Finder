package galleryRouteFinder.structs;

import galleryRouteFinder.controllers.MainController;
import galleryRouteFinder.main.Main;
import javafx.scene.image.Image;
import javafx.util.Pair;

import java.util.*;

import static galleryRouteFinder.controllers.MainController.NAMES;

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

    public static LinkedList<Vertex> BFS(Vertex start, Vertex end, LinkedList<Vertex> avoid, LinkedList<Vertex> include) {
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

            if(include == null && currentNode == end)
                return currentPath;
            else if(include != null && currentNode == end && currentPath.containsAll(include))
                return currentPath;

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
        LinkedList<int[]> path = new LinkedList<>();
        Queue<int[]> agenda = new LinkedList<>();
        HashMap<int[], int[]> parent = new HashMap<>();

        parent.put(start, null);
        agenda.add(start);
        bin.add(start);

        int counterTesting2 = 0;



        while(!agenda.isEmpty()){
            int[] current = agenda.poll();

            if(current[0] == end[0] && current[1] == end[1]) {
                int[] back = current;
                while(back != null) {
                    path.addFirst(back);
                    back = parent.get(back);
                }
                return path;
            }
            int[][] neighbors = {
                    {current[0] - 1, current[1] - 1},
                    {current[0], current[1] - 1},
                    {current[0] + 1, current[1] - 1},
                    {current[0] - 1, current[1]},
                    {current[0] + 1, current[1]},
                    {current[0] - 1, current[1] + 1},
                    {current[0], current[1] + 1},
                    {current[0] + 1, current[1] + 1}
            };
            for(int[] i : neighbors){ // helper bc concModException
                boolean found = false;
                for(int[] j : bin){
                    if(i[0] == j[0] && i[1] == j[1]){
                        counterTesting2++;
                        found = true;
                        break;
                    }
                }
                for(int[] k : wallsAndObjects){
                    if(i[0] == k[0] && i[1] == k[1]){
                        System.out.println("gropious");
                    }
                }
                if(!found){
                    bin.add(i);
                    parent.put(i, current);
                    agenda.add(i);
                    System.out.println("broski" + counterTesting2);
                    System.out.println(wallsAndObjects.size());
                }
            }
        }
        return null;
    }

    public static ArrayList <Integer> inclusiveDijkstra(ArrayList<Vertex> included, ArrayList<Integer> excluded, double shorterDistance, boolean[] artEras) {
        ArrayList <Integer> res=new ArrayList<>();
        for (int i=0; i<included.size()-1; i++){
            ArrayList<Integer> temp=dijkstra(included.get(i), included.get(i+1), excluded, shorterDistance, artEras);
            res.addAll(temp);
        }
        return res;
    }

    private static ArrayList<Integer> dijkstra(Vertex startVertex, Vertex endVertex, ArrayList<Integer> excluded, double shorterDistance, boolean[] artEras)
    {
        HashMap <Integer, Boolean> visited = new HashMap<>();
        if (excluded!=null)
            for (int i : excluded)
                visited.put(i, true);
        HashMap <Integer, Vertex> prev = new HashMap<>();
        PriorityQueue<Pair<Pair<Vertex, Vertex>, Pair <Double, Integer>>> queue=new PriorityQueue<>((o1, o2) -> {
            if (shorterDistance > 0) { //Interesting, weight interesting count first
                if (o1.getValue().getValue() < o2.getValue().getValue())
                    return 1;
                if (o1.getValue().getValue() > o2.getValue().getValue())
                    return -1;
            }
            return -o1.getValue().getKey().compareTo(o2.getValue().getKey());
        });
        queue.add(new Pair<>(new Pair<>(startVertex, startVertex), new Pair<>(0.0, 0)));

        while (!queue.isEmpty())
        {
            Pair<Pair<Vertex, Vertex>, Pair <Double, Integer>> pair=queue.poll();
            Vertex currentVertex=pair.getKey().getValue();
            Vertex prevVertex=pair.getKey().getKey();
            double cost=pair.getValue().getKey();
            int interestingCount=pair.getValue().getValue();
            visited.put(currentVertex.getId(), true);
            prev.put(currentVertex.getId(), prevVertex);
            if (currentVertex.equals(endVertex))
                break;
            for (Edge edge: currentVertex.getBranches()) {
                for (Vertex tmp : edge.getNodes()) {
                    if (!tmp.equals(currentVertex) && !visited.containsKey(tmp.getId())) {
                        if (shorterDistance<0)
                        {
                            queue.add(new Pair<>(new Pair<>(currentVertex, tmp), new Pair<>(cost + edge.getWeight(),  interestingCount)));
                        }
                        else
                        {
                        boolean flag=false;
                        for (int i = 0; i< NAMES.length; i++)
                        {
                            if (tmp.getCategory().equals(NAMES[i]) && artEras[i]) //Is interesting, decrease by amount
                            {
                                queue.add(new Pair<>(new Pair<>(currentVertex, tmp), new Pair<>(cost + edge.getWeight() * (1 - shorterDistance / 100), interestingCount+1)));
                                flag=true;
                                break;
                            }
                        }
                        if (!flag)
                            queue.add(new Pair<>(new Pair<>(currentVertex, tmp), new Pair<>(cost + edge.getWeight() * (1 + shorterDistance / 100), interestingCount)));
                        }
                    }
                }
            }
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

    public static LinkedList<LinkedList<Vertex>> DFS(Vertex start, Vertex end, LinkedList<Vertex> visited, LinkedList<Vertex> avoid){

        if (start == end) {
            LinkedList<Vertex> path = new LinkedList<>();
            path.add(start);
            LinkedList<LinkedList<Vertex>> result = new LinkedList<>();
            result.add(path);
            return result;
        }


        if (visited == null) visited = new LinkedList<>();
        if(avoid != null)
            visited.addAll(avoid);

        visited.add(start);
        LinkedList<LinkedList<Vertex>> res = new LinkedList<>();

        for (Vertex v : start.neighbors) {
            if (!visited.contains(v)) {

                LinkedList<LinkedList<Vertex>> temp2 = DFS(v, end, visited, avoid);
                for (LinkedList<Vertex> x : temp2) {
                    LinkedList<Vertex> tempPath = new LinkedList<>(x);
                    tempPath.addFirst(start);
                    res.add(tempPath);
                    System.out.println(res.size());
                }
            }
        }


        visited.removeLast();

        return res;
    }

    public static LinkedList<LinkedList<Vertex>> dfsSivHelper(LinkedList<LinkedList<Vertex>> v, LinkedList<Vertex> incl){
        LinkedList<LinkedList<Vertex>> res = new LinkedList<>();
        for(LinkedList<Vertex> x : v)
            if(x.containsAll(incl)) res.add(x);

        return res;
    }








}
