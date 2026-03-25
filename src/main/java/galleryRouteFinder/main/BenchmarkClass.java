package galleryRouteFinder.main;

import galleryRouteFinder.structs.Vertex;
import javafx.util.Pair;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
public class BenchmarkClass {
    private final int maxNodes=100; //1e5
    private final int maxNeighbors=10; //1e3
    private final int maxCoords=1000000000; //1e9

    private ArrayList <Vertex> vertices;

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Setup
    public void setup() {
        vertices = new ArrayList<>();
        HashSet <Pair<Integer, Integer>> coords = new HashSet <> ();
        for (int i = 1; i <= maxNodes; i++) {
            int x, y;
            do {
                x=(int) (Math.random()*maxCoords);
                y=(int) (Math.random()*maxCoords);
            }
            while (coords.contains(new Pair<>(x, y)));
            coords.add(new Pair<>(x, y));
            vertices.add(new Vertex(i, x, y, "", "", null));
        }
        for (int i=0; i<vertices.size(); i++) {
            for (int j=1; j<=Math.min(maxNeighbors, vertices.size()-i-1); j++) {
                if (Math.random()<0.5) { //Chance of becoming a neighbor
                    vertices.get(i).addNeighbor(vertices.get(i+j));
                }
            }
        }
    }
    @TearDown
    public void tearDown() {
        vertices=null;
    }

    @Benchmark
    public void dijkstraShortestPath(Blackhole bh) {
        ArrayList <Vertex> tmp=new ArrayList <>();
        tmp.add(vertices.getFirst());
        tmp.add(vertices.getLast());
        ArrayList <Integer> res=Vertex.inclusiveDijkstra(tmp, null);
        bh.consume(res);
    }

    //TODO Benchmark the BFS
}
