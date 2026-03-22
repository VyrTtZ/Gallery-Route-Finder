package galleryRouteFinder.structs;

public class Edge {
    private Vertex node1;
    private Vertex node2;
    private double weight;
    private double direction;
    private static int id = 0;

    public Edge(Vertex node1, Vertex node2) {
        this.node1 = node1;
        this.node2 = node2;
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

    public void setWeight() {
        this.weight = Math.sqrt((double)(Math.pow(node2.getPosY()-node1.getPosY(), 2) + Math.pow(node2.getPosX()-node1.getPosX(), 2)));
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
