package galleryRouteFinder.main;

import galleryRouteFinder.structs.Edge;
import galleryRouteFinder.structs.Vertex;
import galleryRouteFinder.utilities.Utils;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;
import java.io.IOException;

public class MainController {
    //Front end
    public TextField startingRoom, endingRoom;
    public ImageView imageView;

    //Back end
    private static final double SCALE=0.7; //Scale of the map in the program
    private boolean shortestPathAlgorithm=false; //False=Dijkstra, true=bfs,

    ArrayList <Vertex> vertices=new ArrayList <>();
    ArrayList <Edge> edges;

    public void initialize()
    {
        getVertices();
        getEdges();
        imageView.setImage(new Image("map.jpg"));
    }

    public void getVertices()
    {
        File file=new File("vertices.csv");
        try (Scanner read=new Scanner(file))
        {
            while (read.hasNextLine())
            {
                String line=read.nextLine();
                //Format: ID, x, y, name
                int start=0;
                Vertex vertex=new Vertex();
                for (int i=0; i<4; i++)
                {
                    String tmp=Utils.commaStringExtraction(line, start);
                    start+=tmp.length()+1;
                    int data=0;
                    if (i!=3)
                        data=Integer.parseInt(tmp);
                    switch (i)
                    {
                        case 0 -> vertex.setId(data);
                        case 1 -> vertex.setPosX(data);
                        case 2 -> vertex.setPosY(data);
                        case 3 -> vertex.setName(tmp);
                    }
                }
                vertices.add(vertex);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void getEdges()
    {
        File file=new File("edges.csv");
        try (Scanner read=new Scanner(file))
        {
            while (read.hasNextLine())
            {
                String line=read.nextLine();
                //Format: id1, id2, x, y, x, y,...
                int start=0, i=0;
                Edge edge=new Edge(getVertex(0), getVertex(0));
                while (start<line.length())
                {
                    String tmp=Utils.commaStringExtraction(line, start);
                    start+=tmp.length()+1;
                    int data=Integer.parseInt(tmp);
                    if (i==0)
                        edge.setNode1(getVertex(data));
                    else if (i==1)
                        edge.setNode2(getVertex(data));
                    else
                    {
                        int[] tmpArray=new int[2];
                        if (i%2==0) {
                            tmpArray[0] = data;
                            edge.getIntermediaryNodes().add(tmpArray);
                        }
                        else
                            edge.getIntermediaryNodes().getLast()[1]=data;
                    }
                    i++;
                }
                edges.add(edge);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        for (Vertex vertex : vertices)
        {
            vertex.getBranches().clear();
            for (Edge edge : edges)
            {
                if (edge.getNode1().equals(vertex) || edge.getNode2().equals(vertex))
                {
                    vertex.getBranches().add(edge);
                }
            }
        }
    }

    public void shortestPath()
    {
        if (shortestPathAlgorithm) //Diji
        {
            ArrayList <Integer> res=findShortestPathDijkstra();
            for (int i=0; i<res.size(); i++)
                System.out.println(res.get(i));
        }
        else
        {
            int startId=Integer.parseInt(startingRoom.getText()), endId=Integer.parseInt(endingRoom.getText());
            Vertex startV = getVertex(startId), endV=getVertex(endId);
            LinkedList<Vertex> path = Vertex.BFS(startV, endV, null);
            for(Vertex v : path) System.out.println(v.getId());
        }
    }

    private ArrayList<Integer> findShortestPathDijkstra()
    {
        //TODO Add validation
        int startId=Integer.parseInt(startingRoom.getText()), endId=Integer.parseInt(endingRoom.getText());
        boolean[] visited=new boolean[vertices.size()];
        Vertex[] prev=new Vertex[vertices.size()];
        Vertex startVertex=getVertex(startId), endVertex=getVertex(endId);
        PriorityQueue <Pair<Pair<Vertex, Vertex>, Double>> queue=new PriorityQueue<>((o1, o2) -> {
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
        return path;
    }

    private Vertex getVertex(int id)
    {
        for (Vertex v : vertices)
        {
            if (v.getId()==id)
                return v;
        }
        return null;
    }
}
