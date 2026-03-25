package galleryRouteFinder.controllers;

import galleryRouteFinder.structs.Edge;
import galleryRouteFinder.structs.Vertex;
import galleryRouteFinder.utilities.Utils;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
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
    private boolean clickedOnce=false, drawing=false;
    private Pair <Integer, Integer> firstCoords; //Of a click for bfs pixel path

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
        imageView.setOnMouseClicked(e->{
            if (drawing)
                return;
            int tmpX=(int)e.getX(), tmpY=(int)e.getY();
            if (!clickedOnce)
            {
                clickedOnce=true;
                warningLabel.setText("First pixel at: "+tmpX+", "+tmpY+". Specific second pixel");
                firstCoords=new Pair<>(tmpX, tmpY);
            }
            else
            {
                bfsPixelPath(tmpX, tmpY);
                clickedOnce=false;
            }
        });
        imageView.setImage(new Image("images/map.jpg"));
    }

    public void getVertices()
    {
        InputStream is = getClass().getResourceAsStream("/csv/vertices.csv");
        int skips=1;
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
                int data=0;
                if (i!=3)
                    data=Integer.parseInt(tmp);
                switch (i)
                {
                    case 0 -> vertex.setId(data);
                    case 1 -> vertex.setPosX(data);
                    case 2 -> vertex.setPosY(data);
                    case 3 -> vertex.setName(line.substring(start, line.length()-1));
                }
                start+=tmp.length()+1;
            }
            vertices.add(vertex);
        }
    }

    public void getEdges()
    {
        InputStream is = getClass().getResourceAsStream("/csv/edges.csv");
        int skips=1;
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
                if (Utils.checkStringInvalidInteger(tmp))
                    break;
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
        for(Vertex v : vertices){

            for(Edge e : edges){
                if(e.getNode1().equals(v)) v.addNeighbor(e.getNode2());
                else if(e.getNode2().equals(v)) v.addNeighbor(e.getNode1());
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
        if (drawing)
            return;
        int startId = Integer.parseInt(startingRoom.getText()), endId = Integer.parseInt(endingRoom.getText());
        Vertex startV = getVertex(startId), endV = getVertex(endId);
        ArrayList <Integer> res=new ArrayList<>();
        ArrayList <Integer> excluded=null;
        ArrayList <Vertex> included=new ArrayList<>();
        included.add(endV);
        included.addFirst(startV);
        if (shortestPathAlgorithm) //Diji
            res = Vertex.inclusiveDijkstra(included, excluded);
        else {
            LinkedList<Vertex> path = Vertex.BFS(startV, endV, null);
            for (Vertex v : path) res.add(v.getId());
        }
        visualizeShortestPath(res);
    }

    public void interestingPath()
    {
        if (drawing)
            return;
    }

    public void bfsPixelPath(int secondX, int secondY)
    {
        if (drawing)
            return;
        int[] start=new int[2];
        int[] end=new int[2];
        start[0]=firstCoords.getKey();
        start[1]=firstCoords.getValue();

        end[0]=secondX;
        end[1]=secondY;

        System.out.println("start:"+start[0]+","+start[1]+","+end[0]+","+end[1]);
        //TODO fix bfs pixel

        LinkedList<int[]> res=Vertex.BFS(start, end, null);
        for (int[] v : res)
            System.out.println(v[0]+","+v[1]);
        visualizePixelPath(res);
    }

    public void switchIncludeExcludeView()
    {
        Stage stage=new Stage();
        Scene scene;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/includeExclude.fxml"));
            scene = new Scene(loader.load());
            IncludeExcludeController controller=loader.getController();
            controller.vertices=vertices;
            controller.populateData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(scene);
        stage.setTitle("Include, Exclude View");
        stage.show();
    }

    private void visualizePixelPath(LinkedList<int[]> res)
    {
        SequentialTransition sequence=new SequentialTransition();
        imagePane.getChildren().removeIf(node -> node instanceof Rectangle);
        drawing=true;
        for (int[] point : res)
        {
            PauseTransition pause=new PauseTransition(Duration.millis(10));
            Rectangle rect=new Rectangle(point[0]*SCALE, point[1]*SCALE, 1, 1);
            rect.setFill(Color.RED);
            rect.setStrokeWidth(0);

            pause.setOnFinished(event -> {imagePane.getChildren().add(rect);});
            sequence.getChildren().add(pause);
        }
        PauseTransition tmp =new PauseTransition(Duration.millis(0));
        tmp.setOnFinished(e -> drawing=false);
        sequence.getChildren().add(tmp);
        sequence.playFromStart();
    }

    private void visualizeShortestPath(List<Integer> path)
    {
        SequentialTransition sequence=new SequentialTransition();
        imagePane.getChildren().removeIf(node -> node instanceof Circle || node instanceof Label);
        drawing=true;
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
        PauseTransition tmp =new PauseTransition(Duration.millis(0));
        tmp.setOnFinished(e -> drawing=false);
        sequence.getChildren().add(tmp);
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
