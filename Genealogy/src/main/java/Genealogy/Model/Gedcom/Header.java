package Genealogy.Model.Gedcom;

import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.ParsingStructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
     * The last modified Date (LocalDate bug for gedcom format)
     */
    private Date lastModified;

    /**
     * Logger of the class
     */
    public final static Logger logger = LogManager.getLogger(Header.class);

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
    public Date getLastModified() {
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
     * @param fileHeader
     * @throws ParsingException if it could not parse the file fields
     */
    public Header(Genealogy genealogy, ArrayList<ParsingStructure> fileHeader) throws ParsingException {
        software = genealogy.findFieldInContents("NAME");
        version = genealogy.findFieldInContents("VERS");
        String lastModifiedDate0 = genealogy.findFieldInContents("DATE");
        String lastModifiedHour0 = genealogy.findFieldInContents("TIME");

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
            lastModified = simpleDateFormat.parse(lastModifiedDate0 + " " + lastModifiedHour0);
        } catch (ParseException e) {
            logger.error("Failed to parse the gedcom modification date" + lastModifiedDate0 + " " + lastModifiedHour0, e);
        }
    }
}
