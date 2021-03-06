package Genealogy.Model.Gedcom;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.ParsingStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Header class of a genealogic tree
 */
public class Header {
    /**
     * The String software of the gedcom file
     */
    private String software;
    /**
     * The String version of the software
     */
    private String version;
    /**
     * The last modified LocalDateTime
     */
    private LocalDateTime lastModified;

    /**
     * Logger of the class
     */
    public final static Logger logger = LogManager.getLogger(Header.class);

    /**
     * Header final gedcom id
     */
    public final static String headerID = "HEAD";

    /**
     * Getter of Software
     *
     * @return
     */
    public String getSoftware() {
        return software;
    }

    /**
     * Getter of Version
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Getter of lastModified
     *
     * @return
     */
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    /**
     * Function ToString : print the object
     *
     * @return
     */
    @Override
    public String toString() {
        return "Header{" +
                "software='" + software + '\'' +
                ", version='" + version + '\'' +
                ", lastModified='" + lastModified + '\'' +
                '}';
    }

    /**
     * Header constructor
     *
     * @param genealogy
     * @throws ParsingException if it could not parse the file fields
     */
    public Header(Genealogy genealogy) throws ParsingException {
        HashMap<String, ArrayList<ParsingStructure>> contents = genealogy.getContents();
        if (contents != null && contents.containsKey(headerID)) {
            ArrayList<ParsingStructure> parsingStructureList = contents.get(headerID);
            software = genealogy.findFieldInContents("NAME", parsingStructureList);
            version = genealogy.findFieldInContents("VERS", parsingStructureList);
            String lastModifiedDate0 = genealogy.findFieldInContents("DATE", parsingStructureList);
            String lastModifiedHour0 = genealogy.findFieldInContents("TIME", parsingStructureList);

            DateTimeFormatter formatter = (new DateTimeFormatterBuilder()).parseCaseInsensitive().appendPattern("d MMM yyyy H:mm:ss").toFormatter().withLocale(Locale.ENGLISH);
            lastModified = LocalDateTime.parse(lastModifiedDate0 + " " + lastModifiedHour0, formatter);
        } else {
            throw new ParsingException("Header object was not initialized : failed to parse header");
        }
    }
}
