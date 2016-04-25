package Genealogy.Model;

import Genealogy.AuxMethods;
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

    public Header(ArrayList<ParsingStructure> fileHeader) {
        software = AuxMethods.findField(fileHeader,"NAME");
        version = AuxMethods.findField(fileHeader,"VERS");
        String lastModifiedDate0 = AuxMethods.findField(fileHeader,"DATE");
        String lastModifiedHour0 = AuxMethods.findField(fileHeader,"TIME");

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
            lastModified = simpleDateFormat.parse(lastModifiedDate0 + " " + lastModifiedHour0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
