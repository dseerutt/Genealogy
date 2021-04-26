package Genealogy.URLConnexion.Geneanet;

import Genealogy.URLConnexion.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GeneanetTreeManager implements Serializable {

    final static Logger logger = LogManager.getLogger(GeneanetTreeManager.class);
    private transient ArrayList<GeneanetTree> geneanetTrees = new ArrayList<>();

    public GeneanetTreeManager() throws Exception {
        initGeneanetTrees();
    }

    public ArrayList<GeneanetTree> getGeneanetTrees() {
        return geneanetTrees;
    }

    public void addNewTree(String name, String url, String idGedcom) throws Exception {
        GeneanetTree geneanetTree = new GeneanetTree(name, url, "0", "@" + idGedcom + "@");
        geneanetTrees.add(geneanetTree);
        saveTrees();
    }

    public void saveTrees() throws Exception {
        if (geneanetTrees.isEmpty()) {
            return;
        }

        String content = geneanetTrees.stream()
                .map(tree -> String.format("%s", tree.print()))
                .collect(Collectors.joining(System.lineSeparator()));

        try {
            String path = Serializer.getPath();
            if (path == null) {
                path = Serializer.getInstance().getPath();
            }
            try (FileWriter writer = new FileWriter(path + "geneanetTrees.properties");
                 BufferedWriter bw = new BufferedWriter(writer)) {
                bw.write(content);
            }
        } catch (Exception ex) {
            logger.error("Failed to write geneanetTrees file", ex);
            throw new Exception("Failed to write geneanetTrees file", ex);
        }
    }

    public void initGeneanetTrees() throws Exception {
        if (!geneanetTrees.isEmpty()) {
            return;
        }
        InputStream input = null;
        try {
            String path = Serializer.getPath();
            if (path == null) {
                path = Serializer.getInstance().getPath();
            }
            File f = new File(path + "geneanetTrees.properties");
            BufferedReader b = new BufferedReader(new FileReader(f));
            String line = "";
            while ((line = b.readLine()) != null) {
                String[] tmpTab = line.split(";");
                if (tmpTab.length == 4) {
                    geneanetTrees.add(new GeneanetTree(tmpTab[0], tmpTab[1], tmpTab[2], tmpTab[3]));
                } else {
                    logger.error("Could not read GeneanetTrees line : " + line);
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to read geneanetTrees.properties file", ex);
            throw new Exception("Impossible de lire le fichier de propriétés");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("Failed to close inputStream", e);
                }
            }
        }
    }
}
