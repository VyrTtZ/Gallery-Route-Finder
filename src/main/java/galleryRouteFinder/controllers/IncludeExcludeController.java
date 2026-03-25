package galleryRouteFinder.controllers;

import galleryRouteFinder.structs.Vertex;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class IncludeExcludeController {
    //Front end
    public ListView <Vertex> listView=new ListView<>();

    //Back end
    public HashMap <Integer, Vertex> vertices=new HashMap<>();
    public ArrayList <Integer> ids=new ArrayList<>();
    private HashMap <Integer, String> states=new HashMap<>();

    public void initialize(){
        listView.setCellFactory(lv -> new RoomCell());
    }

    public void setIdsAndVertices(ArrayList<Vertex> vertices){
        for (Vertex vertex : vertices)
        {
            ids.add(vertex.getId());
            this.vertices.put(vertex.getId(), vertex);
        }
        populateData();
    }

    public void populateData(){
        for (Integer id : ids){
            states.put(id, "Neutral");
            listView.getItems().add(vertices.get(id));
        }
    }

    public class RoomCell extends ListCell <Vertex> {
        private HBox hBox=new HBox(10);
        private Button button=new Button("Neutral");
        private ImageView imageView=new ImageView();
        private Label info=new Label();
        public RoomCell() {
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            info.setWrapText(true);
            button.setOnAction(e -> {
                switch (button.getText()) {
                    case "Exclude" -> button.setText("Neutral");
                    case "Neutral" -> button.setText("Include");
                    case "Include" -> button.setText("Exclude");
                }
                states.put(getIdFromInfo(info.getText()), button.getText());
            });
            hBox.getChildren().addAll(button, imageView, info);
        }

        @Override
        protected void updateItem(Vertex item, boolean empty) {
            super.updateItem(item, empty);
            if (empty ||  item == null) {
                setGraphic(null);
            }
            else
            {
                info.setText("ID: "+item.getId()+"\n" +
                        "Name: "+item.getName()+"\n" +
                        "Category: "+item.getCategory());
                if (item.getImage() != null)
                    imageView.setImage(item.getImage());
                button.setText(states.get(getIdFromInfo(info.getText())));
                setGraphic(hBox);
            }
        }
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
