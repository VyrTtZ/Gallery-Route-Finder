package galleryRouteFinder.main;

import galleryRouteFinder.structs.Vertex;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class IncludeExcludeController {
    //Front end
    ListView <Vertex> listView=new ListView<>();

    //Back end


    public void initialize(){
        listView.setCellFactory(lv -> new RoomCell());
    }

    public void populateData(){
        //TODO Populate stuff
    }

    public class RoomCell extends ListCell <Vertex> {
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
            });
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
                        "Location: "+"nothing for now");
                //TODO set image
                setGraphic(hBox);
            }
        }
    }
}
