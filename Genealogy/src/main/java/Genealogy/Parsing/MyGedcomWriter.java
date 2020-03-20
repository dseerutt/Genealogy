package Genealogy.Parsing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 04/03/2018.
 */
public class MyGedcomWriter {
    final static Logger logger = LogManager.getLogger(MyGedcomWriter.class);
    private String fileName;
    private String contents;
    private static final String commentSection = "1 NOTE ";
    private static final String contSection = "2 CONT ";
    private static final String pdfSection = "PDFI";

    public MyGedcomWriter(String fileName) {
        this.fileName = fileName;
        this.contents = "";
    }

    public void write() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("C:\\Users\\Dan\\Desktop\\the-file-name.txt", "Cp1252");
        writer.println(contents);
        writer.close();
    }

    public void initContents() throws IOException {
        BufferedReader br = null;
        String sCurrentLine;
        br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Cp1252"));
        int cpt = 0;
        while ((sCurrentLine = br.readLine()) != null) {
            if (cpt > 0){
                contents+= System.lineSeparator() + sCurrentLine ;
            } else {
                contents+= sCurrentLine ;
            }
            cpt++;
            //remove
            if (cpt > 150){
                break;
            }
        }
    }

    public boolean isPDFDefined(String id){
        return contents.contains(pdfSection + id);
    }

    public void updatePDFComment(String id, String PDFStructure) throws Exception {
        if (isPDFDefined(id)){

        } else {
            addComment(id,PDFStructure);
        }
    }

    public void addComment(String id, String comment) throws Exception {
        //Find person
        String id2 = "I" + (Integer.parseInt(id.substring(1,id.length())) + 1);
        String personRegex = "(.*0 @" + id + "@ INDI)(.*)(0 @" + id2 + "@ INDI.*)";
        Pattern pattern = Pattern.compile(personRegex,Pattern.DOTALL);
        Matcher matcher = pattern.matcher(contents);
        String textBefore = "";
        String workingText = "";
        String textAfter = "";
        if (matcher.find()&& matcher.groupCount() == 3){
            textBefore = matcher.group(1);
            workingText = matcher.group(2);
            textAfter = matcher.group(3);
        } else {
            if (contents.contains("0 @" + id + "@ INDI")){
                //Possible id is the last of the file
                personRegex = "(.*0 @" + id + "@ INDI)(.*)";
                pattern = Pattern.compile(personRegex,Pattern.DOTALL);
                matcher = pattern.matcher(contents);
                if (matcher.find()&& matcher.groupCount() == 2){
                    textBefore = matcher.group(1);
                    workingText = matcher.group(2);
                }
            } else {
                throw new Exception("Could not find person " + id);
            }
        }
        if (workingText.contains(commentSection)){
            if (workingText.contains(contSection)){
            //More than one comment exist
                String commentTextBefore = "";
                String commentRegex = "(.*" + contSection + "[^" + System.lineSeparator() + "]*)(" + System.lineSeparator() + ".*)";
                Pattern commentPattern = Pattern.compile(commentRegex,Pattern.DOTALL);
                Matcher commentMatcher = commentPattern.matcher(workingText);
                boolean found = false;
                while (!found && commentMatcher.find() && commentMatcher.groupCount() == 2){
                    commentTextBefore += commentMatcher.group(1);
                    String commentTextAfter = commentMatcher.group(2);
                    commentMatcher = commentPattern.matcher(commentTextAfter);
                    if (!commentTextAfter.contains(contSection)){
                        found = true;
                        workingText = commentTextBefore + System.lineSeparator() + contSection + comment + commentTextAfter;
                    }
                }
                if (!found){
                    throw new Exception("Could not treat complex note for " + id);
                }
            } else {
            //One comment exists
                String commentRegex = "(.*" + commentSection + "[^" + System.lineSeparator() + "]*)(" + System.lineSeparator() + ".*)";
                Pattern commentPattern = Pattern.compile(commentRegex,Pattern.DOTALL);
                Matcher commentMatcher = commentPattern.matcher(workingText);
                if (commentMatcher.find()&& commentMatcher.groupCount() == 2){
                    String commentTextBefore = commentMatcher.group(1);
                    String commentTextAfter = commentMatcher.group(2);
                    workingText = commentTextBefore + System.lineSeparator() + "2 CONT " + comment + commentTextAfter;
                } else {
                    throw new Exception("Could not find simple note for " + id);
                }
            }
        } else {
            //No comment exist
            workingText += commentSection + comment + System.lineSeparator();
        }

        //File building
        contents = textBefore + workingText + textAfter;
    }

    public static void main(String[] args ) throws Exception {
        //String pathArgs = "C:\\Users\\Dan\\Desktop\\Programmation\\IntelliJ\\Genealogy\\Genealogy\\src\\main\\resources\\famille1.ged";
        String pathArgs = "C:\\Users\\Dan\\Desktop\\try.ged";
        MyGedcomWriter writer = new MyGedcomWriter(pathArgs);
        writer.initContents();
        writer.addComment("I1", "Fourth comment : pas de balise CONT");
        writer.write();
        writer.addComment("I2", "First comment : Cas CONT déjà présent");
        writer.write();
        writer.addComment("I3", "Second comment : première balise CONT");
        writer.write();
        writer.addComment("I3", "Third comment : deuxième balise CONT");
        writer.write();
    }
}
