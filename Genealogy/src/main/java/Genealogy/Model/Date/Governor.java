package Genealogy.Model.Date;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Dan on 20/12/2017.
 */
public class Governor {

    private String name;
    private int beginDate;
    private int endDate;
    private ImageIcon image;

    public Governor(String name, int beginDate, int endDate) {
        this.name = name;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(int beginDate) {
        this.beginDate = beginDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    public void addImage(ImageIcon image, int height, int width){
        if (height != 0 && width != 0){
            Image newImage = image.getImage().getScaledInstance(height,width, Image.SCALE_DEFAULT);
            this.image = new ImageIcon(newImage);
        } else {
            System.out.println("Erreur : les dimensions de l'image ne sont pas correctes");
        }
    }

    @Override
    public String toString() {
        return "Governor{" +
                "name='" + name + '\'' +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", image=" + image +
                '}';
    }
}
