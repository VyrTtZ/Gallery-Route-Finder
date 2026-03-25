package galleryRouteFinder.structs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VertexTest {

    Vertex a, b, c, d, e, f, g, h, i, j, k;

    @BeforeEach
    void setUp() {
         a = new Vertex(1, 0, 0, "A");
        b = new Vertex(2, 1, 2, "B");
        c = new Vertex(3, 2, 1, "C");
        d = new Vertex(4, 3, 3, "D");
        e = new Vertex(5, 4, 0, "E");
        f = new Vertex(6, 2, 4, "F");
        g = new Vertex(7, 5, 2, "G");
        h = new Vertex(8, 6, 1, "H");
        i = new Vertex(9, 3, 1, "I");
        j = new Vertex(10, 4, 3, "J");
        k = new Vertex(11, 5, 4, "K");

        a.addNeighbor(b);
        a.addNeighbor(c);
        b.addNeighbor(d);
        b.addNeighbor(e);
        c.addNeighbor(e);
        d.addNeighbor(f);
        d.addNeighbor(g);
        e.addNeighbor(g);
        g.addNeighbor(h);
        b.addNeighbor(i);
        c.addNeighbor(i);
        d.addNeighbor(j);
        i.addNeighbor(j);
        j.addNeighbor(k);
        e.addNeighbor(k);
        k.addNeighbor(h);
        f.addNeighbor(h);
    }

    @AfterEach
    void tearDown() {
        a = b = c = d = e = f = g = h = null;
    }

    @Test
    void getPosX() {
        assertEquals(0, a.getPosX());
        assertEquals(4, e.getPosX());
    }

    @Test
    void setPosX() {
        a.setPosX(99);
        assertEquals(99, a.getPosX());
    }

    @Test
    void getPosY() {
        assertEquals(0, a.getPosY());
        assertEquals(4, f.getPosY());
    }

    @Test
    void setPosY() {
        a.setPosY(42);
        assertEquals(42, a.getPosY());
    }

    @Test
    void getId() {
        System.out.println(a.getId());
        System.out.println(b.getId());

        assertNotEquals(a.getId(), b.getId());
        assertNotEquals(c.getId(), d.getId());
    }

    @Test
    void getName() {
        assertEquals("A", a.getName());
        assertEquals("H", h.getName());
    }

    @Test
    void setName() {
        a.setName("Z");
        assertEquals("Z", a.getName());
    }

    @Test
    void getNeighbors() {
        assertTrue(a.getNeighbors().contains(b));
        assertTrue(a.getNeighbors().contains(c));
    }

    @Test
    void getBranches() {
        for(Edge e : a.getBranches()) System.out.println(e.getNode1() + "!!!!!!!!!!!" + e.getNode2());
        assertEquals(2, a.getBranches().size());
        assertEquals(1, g.getBranches().size());
    }

    @Test
    void addNeighbor() {
        a.addNeighbor(h);
        assertTrue(a.getNeighbors().contains(h));
    }

    @Test
    void removeNeighbor() {
        a.removeNeighbor(b);
        assertFalse(a.getNeighbors().contains(b));
        assertTrue(a.getNeighbors().contains(c));
    }

    @Test
    void BFS() {
        LinkedList<Vertex> result = a.BFS(a, c, null);
        assertEquals(a, result.getFirst());
        assertEquals(c, result.getLast());

        for(Vertex v : result) System.out.println(v.getName());
        LinkedList<Vertex> avoid = new LinkedList<>();
        avoid.add(g);
        LinkedList<Vertex> avoided = a.BFS(a, k, avoid);
        assertFalse(avoided.contains(g));
    }

//    @Test
//    void BFSpx(){
//        LinkedList<int[]> wallsAndObjects = new LinkedList<>();
//
//        for (int i = 0; i < 7; i++) {
//            wallsAndObjects.add(new int[]{0, i});
//            wallsAndObjects.add(new int[]{6, i});
//            wallsAndObjects.add(new int[]{i, 0});
//            wallsAndObjects.add(new int[]{i, 6});
//        }
//        System.out.println("test1");
//        wallsAndObjects.add(new int[]{3, 3});
//
//        int[] start = {1, 1};
//        int[] end   = {5, 5};
//        System.out.println("test2");
//        LinkedList<int[]> path = Vertex.BFS(start, end, wallsAndObjects);
//        assertFalse(path.isEmpty());
//
//        boolean flag = false;
//        for(int[] x : path){
//            if(x[0] == 3 && x[1] == 3) flag = true;
//        }
//        assertFalse(flag);
//
//    }

    @Test
    void DFS() {
        LinkedList<Vertex> result = a.DFS(a, k, null);
        for(Vertex v : result) System.out.println(v.getName());
        assertEquals(a, result.getFirst());
        assertEquals(result.getLast(), k);


        LinkedList<Vertex> avoid = new LinkedList<>();
        avoid.add(g);
        LinkedList<Vertex> avoided = a.DFS(a, c, avoid);
        for(Vertex v : avoided) System.out.println(v.getName());
        assertFalse(avoided.contains(h));
        assertEquals(avoided.getLast(), c);
    }

    @Test
    void BFSandDijkstra() {
        LinkedList<Vertex> result = Vertex.BFS(a, k, null);
        List<Integer> BFSRes = new ArrayList<>();
        for (Vertex v : result) BFSRes.add(v.getId());

        ArrayList <Vertex> tmp=new ArrayList <>();
        tmp.add(a);
        tmp.add(k);
        ArrayList <Integer> dijkstraRes = Vertex.inclusiveDijkstra(tmp, null);
        for (int i = 0; i < dijkstraRes.size(); i++) {
            assertSame(dijkstraRes.get(i), BFSRes.get(i));
        }
    }
}