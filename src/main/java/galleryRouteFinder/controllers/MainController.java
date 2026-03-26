package galleryRouteFinder.controllers;

import galleryRouteFinder.structs.Edge;
import galleryRouteFinder.structs.Vertex;
import galleryRouteFinder.utilities.Utils;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
    ///Front end
    public TextField startingRoom, endingRoom, maxDFSField;
    public ImageView imageView;
    public AnchorPane imagePane;
    public Button shortestPathToggle;
    public Label warningLabel;

    ///Back end
    //Constants
    private static final double SCALE=0.7; //Scale of the map in the program
    private static final String[] NAMES = new String[]{"Medieval and Early Renaissance (1260-1550)", "Renaissance (1500-1600)",  "Baroque (1600-1700)", "Rococo to Romanticism (1700-1800)", "Towards Modernism (1800+)"};

    private boolean shortestPathAlgorithm=true; //True=Dijkstra, false=bfs,
    private boolean clickedOnce=false, drawing=false;
    private double walkWillingness=0;
    private Pair <Integer, Integer> firstCoords; //Of a click for bfs pixel path
    private boolean[] artEras=new boolean[5];

    private ArrayList <Vertex> vertices=new ArrayList <>();
    private ArrayList <Edge> edges=new ArrayList <>();
    private ArrayList <Vertex> included=new ArrayList <>();
    private ArrayList <Integer> excluded=new ArrayList <>();

    public void initialize()
    {
        getVertices();
        getEdges();
        shortestPathToggle.setOnAction(e->{
            shortestPathAlgorithm=!shortestPathAlgorithm;
            shortestPathToggle.setText(shortestPathAlgorithm? "Dijkstra":"BFS");
        });
        startingRoom.setOnKeyPressed(e->{
            if (e.getCode()== KeyCode.ENTER)
                shortestPath();
        });
        endingRoom.setOnKeyPressed(e->{
            if (e.getCode()== KeyCode.ENTER)
                shortestPath();
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
        imageView.setImage(new Image(getClass().getResourceAsStream("/images/map.jpg")));
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
            //Format: ID, x, y, category, name
            int start=0;
            Vertex vertex=new Vertex();
            for (int i=0; i<5; i++)
            {
                String tmp=Utils.commaStringExtraction(line, start);
                int data=0;
                if (i<3)
                    data=Integer.parseInt(tmp);
                switch (i)
                {
                    case 0 -> vertex.setId(data);
                    case 1 -> vertex.setPosX(data);
                    case 2 -> vertex.setPosY(data);
                    case 3 -> vertex.setCategory(tmp);
                    case 4 -> vertex.setName(line.substring(start));
                }
                start+=tmp.length()+1;
            }
            vertex.setImage(new Image(getClass().getResourceAsStream("/images/"+vertex.getId()+".jpg")));
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
        boolean containsEnd=included.contains(endV), containsStart=included.contains(startV);
        if (!containsStart)
            included.addFirst(startV);
        if (!containsEnd)
            included.add(endV);
        if (shortestPathAlgorithm) //Diji
            res = Vertex.inclusiveDijkstra(included, excluded);
        else {
            LinkedList<Vertex> excludeLL = new LinkedList<>();
            for(int i : excluded) excludeLL.add(getVertex(i));
            LinkedList<Vertex> path = Vertex.BFS(startV, endV, excludeLL, new LinkedList<>(included));
            for (Vertex v : path) res.add(v.getId());
        }
        if (!containsStart)
            included.remove(startV);
        if (!containsEnd)
            included.remove(endV);
        visualizeShortestPath(res);
    }

    public void interestingSettings()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Route Preferences");
        alert.setHeaderText("Select your art interests and walking preferences");

        CheckBox[] checkBoxes = new CheckBox[5];
        for (int i = 0; i < checkBoxes.length; i++)
        {
            checkBoxes[i] = new CheckBox(NAMES[i]);
            checkBoxes[i].setSelected(artEras[i]);
        }

        Slider slider=new Slider(0, 100, 50);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(10);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setValue(walkWillingness);

        Label label=new Label("Care about shorter distance x% more:");

        VBox vbox=new VBox(10);
        for  (CheckBox checkBox : checkBoxes)
            vbox.getChildren().add(checkBox);
        vbox.getChildren().addAll(label, slider);
        vbox.setPadding(new Insets(10));
        alert.getDialogPane().setContent(vbox);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            for (int i=0; i<checkBoxes.length; i++)
                artEras[i]=checkBoxes[i].isSelected();
            walkWillingness=slider.getValue();
        }
    }

    public void interestingRoute()
    {
        if (drawing)
            return;
    }

    public void dfsRouting()
    {
        //TODO
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
            controller.setMainController(this);
            controller.setIdsVerticesAndStates(vertices, included, excluded);
            stage.setScene(scene);
            stage.setTitle("Include, Exclude View");
            stage.setOnCloseRequest(e -> {
                updateIncludeExclude(controller.getStates(), controller.getOrderSet(), controller.getVertices());
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        PauseTransition tmp =new PauseTransition(Duration.millis(10));
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
        PauseTransition tmp =new PauseTransition(Duration.millis(10));
        tmp.setOnFinished(e -> {
            drawing=false;
        });
        sequence.getChildren().add(tmp);
        sequence.playFromStart();
    }

    public void updateIncludeExclude(HashMap <Integer, String> states, HashSet <Integer> orderSet, HashMap <Integer, Pair <Vertex, Integer>> vertices)
    {
        included=new ArrayList<>();
        excluded=new ArrayList<>();
        for (Vertex vertex : this.vertices)
            if (states.get(vertex.getId()).equals("Exclude"))
                excluded.add(vertex.getId());
        for (Integer i : orderSet)
            for (Vertex vertex : this.vertices)
                if (states.get(vertex.getId()).equals("Include") && vertices.get(vertex.getId()).getValue().equals(i))
                    included.add(vertex);
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
