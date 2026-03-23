package galleryRouteFinder.structs;

import java.util.LinkedList;

public class Edge {
    private Vertex node1;
    private Vertex node2;
    private double weight;
    private double direction;
    private static int id = 0;
    private LinkedList<int[]> intermediaryNodes;

    public Edge(Vertex node1, Vertex node2) {
        this.node1 = node1;
        this.node2 = node2;
        this.intermediaryNodes = new LinkedList<>();
        setWeight();
        setId();
    }


    public Vertex[] getNodes(){
        return new Vertex[]{getNode1(), getNode2()};
    }

    public Vertex getNode2() {
        return node2;
    }

    public void setNode2(Vertex node2) {
        this.node2 = node2;
    }

    public Vertex getNode1() {
        return node1;
    }

    public void setNode1(Vertex node1) {
        this.node1 = node1;
    }

    public double getWeight() {
        return weight;
    }

    public LinkedList<int[]> getIntermediaryNodes() {
        return intermediaryNodes;
    }

    public void setIntermediaryNodes(LinkedList<int[]> intermediaryNodes) {
        this.intermediaryNodes = intermediaryNodes;
    }
    public void addIntermediarynode(int[] coords){
        getIntermediaryNodes().add(coords);
        setWeight();
    }

    public void setWeight() {
        double totalDist = 0;
        int curX = node1.getPosX();
        int curY = node1.getPosY();

        for(int[] i : intermediaryNodes){
            totalDist += Math.sqrt(Math.pow(curX - i[0], 2) + Math.pow(curY-i[1], 2));
            curX = i[0];
            curY = i[1];
        }
        totalDist += Math.sqrt(Math.pow(curX -node2.getPosX(),2) + Math.pow(curY-node2.getPosY(),2));
        this.weight = totalDist;
    }

    public int getId() {
        return id;
    }

    public void setId() {
        this.id = id++;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = Math.tan((double) (node2.getPosY() - node1.getPosY()) /(node2.getPosX() - node1.getPosX()));
    }
}
