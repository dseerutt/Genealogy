package Genealogy.Model;

import Genealogy.AuxMethods;
import Genealogy.Genealogy;
import Genealogy.Model.Exception.ParsingException;
import Genealogy.Parsing.ParsingStructure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dan on 05/04/2016.
 */
public class Header {
    private String software;
    private String version;
    private Date lastModified;

    public String getSoftware() {
        return software;
    }

    public String getVersion() {
        return version;
    }

    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public String toString() {
        return "Header{" +
                "software='" + software + '\'' +
                ", version='" + version + '\'' +
                ", lastModified='" + AuxMethods.getStringDate(lastModified) + '\'' +
                '}';
    }

    /**
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
            e.printStackTrace();
        }
    }
}
