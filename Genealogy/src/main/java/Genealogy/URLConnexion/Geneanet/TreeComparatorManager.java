package Genealogy.URLConnexion.Geneanet;

import Genealogy.GUI.TreeModificationScreen;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Parsing.MyGedcomReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Scanner;

import static Genealogy.Model.Gedcom.Genealogy.genealogy;
import static Genealogy.Model.Gedcom.Genealogy.logger;

public class TreeComparatorManager {
    public static String gedcomFile;
    public static boolean searchOnGeneanet;
    public static boolean exceptionMode = false;
    private static TreeComparatorManager instance;
    public static GeneanetBrowser geneanetBrowser;
    public int indexTree;

    public static TreeComparatorManager getInstance() {
        if (instance == null) {
            instance = new TreeComparatorManager();
        }
        return instance;
    }

    private TreeComparatorManager() {
        indexTree = 1;
    }

    public static void refreshGedcomData() throws IOException, ParsingException {
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        genealogy = myGedcomReader.read(gedcomFile);
        genealogy.parseContents();
        genealogy.sortPersons();
    }

    public String askUser() {
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    public boolean compareTreeFromName(String name) throws Exception {
        for (GeneanetTree geneanetTree : getGeneanetBrowser().getGeneanetTrees()) {
            if (StringUtils.equals(name, geneanetTree.getName())) {
                return compareTreeOnce(geneanetTree.getUrl());
            }
        }
        return false;
    }

    public boolean compareTreeOnce(String url) throws Exception {
        Genealogy genealogyParameter = genealogy;
        TreeComparator treeComparator = new TreeComparator();
        String info = treeComparator.compareTree(url, genealogyParameter);
        if (treeComparator.isErrorComparison()) {
            TreeModificationScreen treeModificationScreen = TreeModificationScreen.getInstance();
            treeModificationScreen.getComparaisonText().setText(info);
            treeModificationScreen.treeComparator = treeComparator;
            return false;
        } else {
            logger.info(url + " OK");
            return true;
        }
    }

    public void compareTree(String testUrl) throws Exception {
        Genealogy genealogyParameter = genealogy;
        TreeComparator treeComparator = new TreeComparator();
        String info = treeComparator.compareTree(testUrl, genealogyParameter) + System.lineSeparator();
        boolean error = treeComparator.isErrorComparison();
        if (searchOnGeneanet && !exceptionMode) {
            error = false;
        }
        while (error) {
            info += treeComparator.analyseTree() + System.lineSeparator();
            String addModification = askUser();
            if (addModification != null) {
                genealogyParameter = treeComparator.makeModification(addModification, genealogyParameter);
            }
            info = treeComparator.compareTree(testUrl, genealogyParameter) + System.lineSeparator();
            error = treeComparator.isErrorComparison();
        }
        logger.info(System.lineSeparator() + info);
    }

    public GeneanetBrowser getGeneanetBrowser() throws Exception {
        if (geneanetBrowser == null) {
            geneanetBrowser = new GeneanetBrowser();

        }
        return geneanetBrowser;
    }

    public boolean compareTreesWithScreen() throws Exception {
        int index = 1;
        boolean runOK = true;
        for (GeneanetTree geneanetTree : getGeneanetBrowser().getGeneanetTrees()) {
            if (index >= indexTree) {
                if (!compareTreeOnce(geneanetTree.getUrl())) {
                    indexTree = index;
                    runOK = false;
                    break;
                }
            }
            index++;
        }
        return runOK;
    }

    public void compareTrees() throws Exception {
        int index = 1;
        for (GeneanetTree geneanetTree : getGeneanetBrowser().getGeneanetTrees()) {
            if (index >= 1) {
                compareTree(geneanetTree.getUrl());
            }
            index++;
        }
    }

    public static void main(String[] args) throws Exception {
        gedcomFile = "C:\\Users\\Dan\\Desktop\\famille1.ged";
        refreshGedcomData();
        searchOnGeneanet = false;
        TreeComparatorManager treeComparatorManager = getInstance();
        treeComparatorManager.compareTrees();
    }
}
