package Genealogy.Model.GUI;

import Genealogy.GUI.WelcomeScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    /**
     * Class logger
     */
    public final static Logger logger = LogManager.getLogger(WelcomeScreen.class);

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

    public void addImage(ImageIcon image, int height, int width) {
        if (height != 0 && width != 0) {
            Image newImage = image.getImage().getScaledInstance(height, width, Image.SCALE_DEFAULT);
            this.image = new ImageIcon(newImage);
        } else {
            logger.info("Erreur : les dimensions de l'image ne sont pas correctes");
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
