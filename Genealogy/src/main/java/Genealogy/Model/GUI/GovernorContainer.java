package Genealogy.Model.GUI;

import Genealogy.URLConnexion.Serializer;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Dan on 19/12/2017.
 */
public class GovernorContainer {

    private HashMap<Integer, String> mappingDate = new HashMap<>();
    private String propertiesFileName;
    private int beginningDate = Calendar.getInstance().get(Calendar.YEAR);
    private String path;
    private ArrayList<Governor> governors = new ArrayList<>();

    public int getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(int beginningDate) {
        this.beginningDate = beginningDate;
    }

    public GovernorContainer(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
        initGovernors();
    }

    public ArrayList<Governor> getGovernors() {
        return governors;
    }

    public void setGovernors(ArrayList<Governor> governors) {
        this.governors = governors;
    }

    public ImageIcon getImage(String name) {
        for (Governor governor : governors) {
            if (governor.getName().equals(name)) {
                return governor.getImage();
            }
        }
        return null;
    }

    public HashMap<Integer, String> getMappingDate() {
        return mappingDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPropertiesFileName() {
        return propertiesFileName;
    }

    public void setPropertiesFileName(String propertiesFileName) {
        this.propertiesFileName = propertiesFileName;
    }

    public void setMappingDate(HashMap<Integer, String> mappingDate) {
        this.mappingDate = mappingDate;
    }

    public String getGovernor(int date) {
        return mappingDate.get(date);
    }

    public void initGovernorsLists(String input) {
        if (input != null) {
            String[] tabInput = input.split(";");
            String governorName = tabInput[0];
            int beginDate = Integer.parseInt(tabInput[1]);
            int endDate;
            if (tabInput.length == 2) {
                endDate = Calendar.getInstance().get(Calendar.YEAR);
            } else {
                endDate = Integer.parseInt(tabInput[2]);
            }
            Governor governor = new Governor(governorName, beginDate, endDate);
            governors.add(governor);

            for (int j = beginDate; j < endDate + 1; j++) {
                mappingDate.put(j, governorName);
            }
            if (beginDate < beginningDate) {
                beginningDate = beginDate;
            }
        }
    }

    public void initGovernors() {
        path = Serializer.getPath();
        if (path == null) {
            Serializer serializer = new Serializer();
            path = serializer.getPath();
        }
        path += "Genealogy" + File.separator + "GUI" + File.separator + propertiesFileName + File.separator;
        File file = new File(path + propertiesFileName);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            while (scanner.hasNextLine()) {
                initGovernorsLists(scanner.nextLine());
            }
        } finally {
            scanner.close();
        }
    }
}
