package Genealogy.Parsing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MyGedcomWriter class : write a Gedcom file
 */
public class MyGedcomWriter {
    /**
     * Logger of the class
     */
    final static Logger logger = LogManager.getLogger(MyGedcomWriter.class);
    /**
     * String fileName to save
     */
    private String fileName;
    /**
     * String contents to save
     */
    private String contents;
    /**
     * String final note id for the section
     */
    private static final String noteSection = "1 NOTE ";
    /**
     * String final contents id for the section
     */
    private static final String contSection = "2 CONT ";
    /**
     * String final pdf section for the section
     */
    private static final String pdfSection = "PDFI";

    /**
     * Constructor MyGedcomWriter
     *
     * @param fileName Name of file
     */
    public MyGedcomWriter(String fileName) {
        this.fileName = fileName;
        this.contents = "";
    }

    public boolean isPDFDefined(String id) {
        return contents.contains(pdfSection + id);
    }

    public void updatePDFComment(String id, String PDFStructure) throws Exception {
        if (isPDFDefined(id)) {

        } else {
            addComment(id, PDFStructure);
        }
    }

    public void addComment(String id, String comment) throws Exception {
        //Find person
        String id2 = "I" + (Integer.parseInt(id.substring(1, id.length())) + 1);
        String personRegex = "(.*0 @" + id + "@ INDI)(.*)(0 @" + id2 + "@ INDI.*)";
        Pattern pattern = Pattern.compile(personRegex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(contents);
        String textBefore = "";
        String workingText = "";
        String textAfter = "";
        if (matcher.find() && matcher.groupCount() == 3) {
            textBefore = matcher.group(1);
            workingText = matcher.group(2);
            textAfter = matcher.group(3);
        } else {
            if (contents.contains("0 @" + id + "@ INDI")) {
                //Possible id is the last of the file
                personRegex = "(.*0 @" + id + "@ INDI)(.*)";
                pattern = Pattern.compile(personRegex, Pattern.DOTALL);
                matcher = pattern.matcher(contents);
                if (matcher.find() && matcher.groupCount() == 2) {
                    textBefore = matcher.group(1);
                    workingText = matcher.group(2);
                }
            } else {
                throw new Exception("Could not find person " + id);
            }
        }
        if (workingText.contains(noteSection)) {
            if (workingText.contains(contSection)) {
                //More than one comment exist
                String commentTextBefore = "";
                String commentRegex = "(.*" + contSection + "[^" + System.lineSeparator() + "]*)(" + System.lineSeparator() + ".*)";
                Pattern commentPattern = Pattern.compile(commentRegex, Pattern.DOTALL);
                Matcher commentMatcher = commentPattern.matcher(workingText);
                boolean found = false;
                while (!found && commentMatcher.find() && commentMatcher.groupCount() == 2) {
                    commentTextBefore += commentMatcher.group(1);
                    String commentTextAfter = commentMatcher.group(2);
                    commentMatcher = commentPattern.matcher(commentTextAfter);
                    if (!commentTextAfter.contains(contSection)) {
                        found = true;
                        workingText = commentTextBefore + System.lineSeparator() + contSection + comment + commentTextAfter;
                    }
                }
                if (!found) {
                    throw new Exception("Could not treat complex note for " + id);
                }
            } else {
                //One comment exists
                String commentRegex = "(.*" + noteSection + "[^" + System.lineSeparator() + "]*)(" + System.lineSeparator() + ".*)";
                Pattern commentPattern = Pattern.compile(commentRegex, Pattern.DOTALL);
                Matcher commentMatcher = commentPattern.matcher(workingText);
                if (commentMatcher.find() && commentMatcher.groupCount() == 2) {
                    String commentTextBefore = commentMatcher.group(1);
                    String commentTextAfter = commentMatcher.group(2);
                    workingText = commentTextBefore + System.lineSeparator() + "2 CONT " + comment + commentTextAfter;
                } else {
                    throw new Exception("Could not find simple note for " + id);
                }
            }
        } else {
            //No comment exist
            workingText += noteSection + comment + System.lineSeparator();
        }

        //File building
        contents = textBefore + workingText + textAfter;
    }
}
