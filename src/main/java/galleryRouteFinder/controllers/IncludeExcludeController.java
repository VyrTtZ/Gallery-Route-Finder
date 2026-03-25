package galleryRouteFinder.controllers;

import galleryRouteFinder.structs.Vertex;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IncludeExcludeController {
    //Front end
    public ListView <Pair <Vertex, Integer>> listView=new ListView<>();
    public Label mainLabel;
    public Button reorderToggle;

    //Back end
    private MainController mainController;
    private HashMap <Integer, Pair<Vertex, Integer>> vertices=new HashMap<>();
    private HashSet <Integer> orderSet=new HashSet<>();
    private ArrayList <Integer> ids=new ArrayList<>();
    private HashMap <Integer, String> states=new HashMap<>();
    private boolean reordering=false;

    public void initialize(){
        listView.setCellFactory(lv -> new RoomCell());
        reorderToggle.setOnAction(e -> {
            reordering=!reordering;
            if (reordering) {
                reorderToggle.setText("Filter");
                mainLabel.setText("Reorder the rooms in visiting order!");
            }
            else {
                reorderToggle.setText("Reorder");
                mainLabel.setText("Include, Exclude, or leave it Neutral");
            }
            populateData();
        });
    }

    public void setMainController(MainController mainController){
        this.mainController=mainController;
    }

    public void setIdsVerticesAndStates(ArrayList<Vertex> vertices, ArrayList<Vertex> include, ArrayList<Integer> exclude){
        for (Vertex vertex : vertices)
        {
            ids.add(vertex.getId());
            states.put(vertex.getId(), "Neutral");
            this.vertices.put(vertex.getId(), new Pair<>(vertex, 0));
        }
        for (Vertex vertex : include)
            states.put(vertex.getId(), "Include");
        for (Integer id : exclude)
            states.put(id, "Exclude");
        populateData();
    }

    public void populateData(){
        listView.getItems().clear();
        if (reordering){
            for (int i=0; i<orderSet.size(); i++){
                for (Integer id : ids){
                    if (states.get(id).equals("Include") && vertices.get(id).getValue()==i)
                    {
                        listView.getItems().add(vertices.get(id));
                        break;
                    }
                }
            }
        }
        else {
            for (Integer id : ids)
                listView.getItems().add(vertices.get(id));
        }
    }

    public class RoomCell extends ListCell <Pair<Vertex, Integer>> {
        private HBox hBox=new HBox(10);
        private Button button=new Button();
        private ImageView imageView=new ImageView();
        private Label info=new Label();
        public RoomCell() {
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            info.setWrapText(true);
            button.setCursor(Cursor.HAND);
            button.setOnAction(e -> {
                int vertexId=getIdFromInfo(info.getText());
                if (reordering){
                    int index=vertices.get(vertexId).getValue();
                    if (index==0)
                        return;
                    for (Integer id: ids)
                    {
                        if (states.get(id).equals("Include") && vertices.get(id).getValue()==index-1)
                        {
                            Pair <Vertex, Integer> temp=new Pair <> (vertices.get(id).getKey(), index); //Move down
                            vertices.put(id, temp);
                            break;
                        }
                    }
                    Pair <Vertex, Integer> up=new Pair <> (vertices.get(vertexId).getKey(), index-1); //Move up
                    vertices.put(vertexId, up);
                    populateData();
                }
                else {
                    switch (button.getText()) {
                        case "Exclude" -> {
                            button.setText("Neutral");
                            button.setBackground(Background.fill(Color.DARKGRAY));
                        }
                        case "Neutral" -> {
                            button.setText("Include");
                            button.setBackground(Background.fill(Color.GREEN));
                        }
                        case "Include" -> {
                            button.setText("Exclude");
                            button.setBackground(Background.fill(Color.RED));
                        }
                    }
                    if (button.getText().equals("Include"))
                    {
                        int index=0;
                        while (orderSet.contains(index))
                            index++;
                        orderSet.add(index);
                        Pair <Vertex, Integer> tmp=new Pair <> (vertices.get(vertexId).getKey(), index);
                        vertices.put(vertexId, tmp);
                    }
                    else if (button.getText().equals("Exclude"))
                        orderSet.remove(vertices.get(vertexId).getValue());
                    states.put(vertexId, button.getText());
                }
            });
            hBox.getChildren().addAll(button, imageView, info);
        }

        @Override
        protected void updateItem(Pair <Vertex, Integer> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty ||  item == null) {
                setGraphic(null);
            }
            else
            {
                info.setText("ID: " + item.getKey().getId() + "\n" +
                        "Name: " + item.getKey().getName() + "\n" +
                        "Category: " + item.getKey().getCategory());
                if (item.getKey().getImage() != null)
                    imageView.setImage(item.getKey().getImage());
                button.setBackground(Background.fill(Color.DARKGRAY));
                if (reordering)
                    button.setText("Move up 1 pos");
                else
                    button.setText(states.get(getIdFromInfo(info.getText())));
                if (button.getText().equals("Include"))
                    button.setBackground(Background.fill(Color.GREEN));
                else if (button.getText().equals("Exclude"))
                    button.setBackground(Background.fill(Color.RED));
                setGraphic(hBox);
            }
        }
    }

    public HashMap <Integer, String> getStates(){
        return states;
    }

    public HashSet <Integer> getOrderSet(){
        return orderSet;
    }

    public HashMap <Integer, Pair <Vertex, Integer>> getVertices(){
        return vertices;
    }

    public void update()
    {
        mainController.updateIncludeExclude(states, orderSet, vertices);
        mainLabel.setText("Updated!");
    }

    private int getIdFromInfo(String s) //Specifically only for this class
    {
        int i=4, num=0;
        while (i<s.length() && s.charAt(i)!='\n')
        {
            num*=10;
            num+=s.charAt(i)-48;
            i++;
        }
        return num;
    }
}
