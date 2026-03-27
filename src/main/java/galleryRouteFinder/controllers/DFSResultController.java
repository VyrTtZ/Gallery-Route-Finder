package galleryRouteFinder.controllers;

import galleryRouteFinder.structs.Vertex;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.LinkedList;

public class DFSResultController {
    public TreeView<String> treeView;

    public void displayRoutes(LinkedList <LinkedList <Vertex>> list)
    {
        TreeItem<String> root = new TreeItem<>("Routes");
        root.setExpanded(true);
        for (int i = 0; i < list.size(); i++)
        {
            LinkedList <Vertex> path= list.get(i);
            TreeItem<String> item = new TreeItem<>("Route "+(i+1));
            TreeItem<String> current=item;
            for (Vertex v : path)
            {
                TreeItem<String> child = new TreeItem<>("Room "+v.getId());
                current.getChildren().add(child);
                current=child;
            }
            root.getChildren().add(item);
        }
        treeView.setRoot(root);
    }
}
