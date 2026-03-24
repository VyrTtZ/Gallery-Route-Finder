package galleryRouteFinder.controllers;

import galleryRouteFinder.structs.Edge;
import galleryRouteFinder.structs.Vertex;
import galleryRouteFinder.utilities.Utils;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.InputStream;
import java.util.*;

public class MainController {
    //Front end
    public TextField startingRoom, endingRoom;
    public ImageView imageView;
    public AnchorPane imagePane;
    public Button shortestPathToggle;
    public Label warningLabel;

    //Back end
    private static final double SCALE=0.7; //Scale of the map in the program
    private boolean shortestPathAlgorithm=true; //True=Dijkstra, false=bfs,

    ArrayList <Vertex> vertices=new ArrayList <>();
    ArrayList <Edge> edges=new ArrayList <>();

    public void initialize()
    {
        getVertices();
        getEdges();
        shortestPathToggle.setOnAction(e->{
            shortestPathAlgorithm=!shortestPathAlgorithm;
            shortestPathToggle.setText(shortestPathAlgorithm? "Dijkstra":"BFS");
        });
        imageView.setImage(new Image("map.jpg"));
    }

    public void getVertices()
    {
        InputStream is = getClass().getResourceAsStream("/vertices.csv");
        int skips=3;
        Scanner read=new Scanner(is);
        while (read.hasNextLine())
        {
            String line=read.nextLine();
            if (skips>0)
            {
                skips--;
                continue;
            }
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

    public void getEdges()
    {
        InputStream is = getClass().getResourceAsStream("/edges.csv");
        int skips=3;
        Scanner read=new Scanner(is);
        while (read.hasNextLine())
        {
            String line=read.nextLine();
            if (skips>0)
            {
                skips--;
                continue;
            }
            //Format: id1, id2, x, y, x, y,...
            int start=0, i=0;
            Edge edge=new Edge(getVertex(20), getVertex(20));
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
        if (Utils.checkStringInvalidInteger(startingRoom.getText()) || Utils.checkStringInvalidInteger(endingRoom.getText()))
        {
            warningLabel.setText("The input/output room must be a number");
            return;
        }
        int startId = Integer.parseInt(startingRoom.getText()), endId = Integer.parseInt(endingRoom.getText());
        Vertex startV = getVertex(startId), endV = getVertex(endId);
        ArrayList <Integer> res=new ArrayList<>();
        if (shortestPathAlgorithm) //Diji
        {
            res=Vertex.dijkstraShortestPath(startV, endV, 25);
        }
        else {
            LinkedList<Vertex> path = Vertex.BFS(startV, endV, null);
            for (Vertex v : path) res.add(v.getId());
        }
        visualizePath(res);
    }

    private ArrayList<Integer> findShortestPathDijkstra()
    {
        int startId=Integer.parseInt(startingRoom.getText()), endId=Integer.parseInt(endingRoom.getText());
        boolean[] visited=new boolean[25];
        Vertex[] prev=new Vertex[25]; //vertices.size()
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
        path.add(startVertex.getId());
        ArrayList<Integer> res=new ArrayList<>();
        for (int i=path.size()-1; i>=0; i--)
            res.add(path.get(i));
        return res;
    }

    private void visualizePath(List<Integer> path)
    {
        SequentialTransition sequence=new SequentialTransition();
        imagePane.getChildren().removeIf(node -> node instanceof Circle || node instanceof Label);
        for (Integer integer : path) {
            PauseTransition circleDraw = new PauseTransition(Duration.millis(200));
            Circle circle = new Circle();
            Vertex vertex = getVertex(integer);
            circle.setCenterX(vertex.getPosX() * SCALE);
            circle.setCenterY(vertex.getPosY() * SCALE);
            circle.setStroke(Color.YELLOW);
            circle.setStrokeWidth(2);
            circle.setFill(Color.TRANSPARENT);
            circle.setRadius(7);

            Label label = new Label(vertex.getId() + "");
            label.setLayoutX(vertex.getPosX() * SCALE-7);
            label.setLayoutY(vertex.getPosY() * SCALE-7);
            circleDraw.setOnFinished(event -> {imagePane.getChildren().addAll(circle, label);});
            sequence.getChildren().add(circleDraw);
        }
        sequence.playFromStart();
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
