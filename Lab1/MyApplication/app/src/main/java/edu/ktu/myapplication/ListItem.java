package edu.ktu.myapplication;

import java.io.Serializable;

public class ListItem implements Serializable {
    private String title;
    private int imageID;
    private String description;

    public ListItem(){

    }
    public ListItem(String title,int imageID,String description){
        this.title=title;
        this.imageID=imageID;
        this.description=description;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public int getImageID(){
        return imageID;
    }
    public void setImageID(int imageID){
        this.imageID=imageID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
