package galleryRouteFinder.main;

import galleryRouteFinder.structs.Edge;
import galleryRouteFinder.structs.Vertex;
import galleryRouteFinder.utilities.Utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class MainController {
    //Front end

    //Back end
    private static final double SCALE=0.7; //Scale of the map in the program
    ArrayList <Vertex> vertices=new ArrayList <>();
    ArrayList <Edge> edges;

    public void initialize()
    {
        getVertices();
        getEdges();
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
                Vertex vertex=new Vertex(0, 0, 0, "");
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
                    else if (i%2==0)
                    {
                        //Waiting
                    }
                    else
                    {
                        //Waiting
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
