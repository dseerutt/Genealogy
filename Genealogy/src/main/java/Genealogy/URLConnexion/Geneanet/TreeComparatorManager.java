package Genealogy.URLConnexion.Geneanet;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Parsing.MyGedcomReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Scanner;

import static Genealogy.Model.Gedcom.Genealogy.genealogy;
import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.logger;

public class TreeComparatorManager {
    public static String gedcomFile;
    public static boolean searchOnGeneanet;
    public static boolean exceptionMode = false;
    private static TreeComparatorManager instance;
    public static GeneanetBrowser geneanetBrowser;

    public static TreeComparatorManager getInstance() {
        if (instance == null) {
            instance = new TreeComparatorManager();
        }
        return instance;
    }

    private TreeComparatorManager() {

    }

    public static void refreshGedcomData() throws IOException, ParsingException {
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        genealogy = myGedcomReader.read(gedcomFile);
        genealogy.parseContents();
        genealogy.sortPersons();
    }


    public String consoleScan() {
        logger.info("Add line ? (A to add/replace/delete, exit to exit, any other to refresh data)");
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    public void compareTreeFromName(String name) throws Exception {
        for (GeneanetTree geneanetTree : getGeneanetBrowser().getGeneanetTrees()) {
            if (StringUtils.equals(name, geneanetTree.getName())) {
                compareTree(geneanetTree.getUrl());
                return;
            }
        }
    }

    public void compareTree(String testUrl) throws Exception {
        Genealogy genealogyParameter = genealogy;
        TreeComparator treeComparator = new TreeComparator();
        treeComparator.compareTree(testUrl, genealogyParameter);
        boolean error = treeComparator.isErrorComparison();
        if (searchOnGeneanet && !exceptionMode) {
            error = false;
        }
        while (error) {
            treeComparator.analyseTree();
            String addModification = consoleScan();
            if (addModification != null) {
                genealogyParameter = treeComparator.makeModification(addModification, genealogyParameter);
            }
            treeComparator.compareTree(testUrl, genealogyParameter);
            error = treeComparator.isErrorComparison();
        }
    }

    public GeneanetBrowser getGeneanetBrowser() throws Exception {
        if (geneanetBrowser == null) {
            geneanetBrowser = new GeneanetBrowser();

        }
        return geneanetBrowser;
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
