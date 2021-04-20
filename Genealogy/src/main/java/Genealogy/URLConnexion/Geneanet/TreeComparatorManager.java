package Genealogy.URLConnexion.Geneanet;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Model.Gedcom.Genealogy;
import Genealogy.Parsing.MyGedcomReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static Genealogy.Model.Gedcom.Genealogy.genealogy;
import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.logger;

public class TreeComparatorManager {
    public static String gedcomFile;
    public static boolean searchOnGeneanet;
    public static boolean exceptionMode;
    private static TreeComparatorManager instance;

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

    public static void main(String[] args) throws Exception {
        //init gedcomfile
        gedcomFile = "C:\\Users\\Dan\\Desktop\\famille1.ged";
        refreshGedcomData();
        //printDirectAncestorsToInvestigate();

        TreeComparatorManager treeComparatorManager = getInstance();

        GeneanetBrowser urlBrowser = new GeneanetBrowser();
        ArrayList<GeneanetTree> geneanetTrees = urlBrowser.getGeneanetTrees();
        searchOnGeneanet = false;
        exceptionMode = false;
        int index = 1;
        for (GeneanetTree geneanetTree : geneanetTrees) {
            if (index == 1) {
                treeComparatorManager.compareTree(geneanetTree.getUrl());
            }
            index++;
        }
    }
}
