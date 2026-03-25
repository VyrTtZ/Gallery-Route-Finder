package galleryRouteFinder.controllers;

import galleryRouteFinder.structs.Vertex;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

import java.util.ArrayList;

public class IncludeExcludeController {
    //Front end
    public ListView <Pair <Vertex, String>> listView=new ListView<>();

    //Back end
    public ArrayList<Vertex> vertices=new ArrayList<>();
    private ArrayList <String> states=new ArrayList<>();

    public void initialize(){
        listView.setCellFactory(lv -> new RoomCell());
    }

    public void populateData(){
        for (Vertex v : vertices){
            states.add("Neutral");
            listView.getItems().add(new Pair<>(v, "Neutral"));
        }
    }

    public class RoomCell extends ListCell <Pair<Vertex, String>> {
        private HBox hBox=new HBox(10);
        private Button button=new Button("Neutral");
        private int state=0; //0: neutral, 1: include, 2: exclude
        private ImageView imageView=new ImageView();
        private Label info=new Label();
        public RoomCell() {
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            info.setWrapText(true);
            button.setOnAction(e -> {
                state=(state+1)%3;
                switch (state) {
                    case 0 -> button.setText("Neutral");
                    case 1 -> button.setText("Include");
                    case 2 -> button.setText("Exclude");
                }
                states.set(getIdFromInfo(info.getText()), button.getText());
            });
            hBox.getChildren().addAll(button, imageView, info);
        }

        @Override
        protected void updateItem(Pair <Vertex, String> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty ||  item == null) {
                setGraphic(null);
            }
            else
            {
                info.setText("ID: "+item.getKey().getId()+"\n" +
                        "Name: "+item.getKey().getName()+"\n" +
                        "Location: "+"nothing for now");
                //TODO set image
                button.setText(item.getValue());
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
