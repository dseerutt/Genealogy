package Genealogy.Parsing;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dan on 22/04/2018.
 */
public class PDFStructure {
    private Set<String> birthPDFList = new HashSet<>();
    private Set<String> marriagePDFList = new HashSet<>();
    private Set<String> deathPDFList = new HashSet<>();
    private String idPerson;

    public void addToPDFBirthList(String PDF){
        birthPDFList.add(PDF);
    }

    public void addToPDFMarriageList(String PDF){
        marriagePDFList.add(PDF);
    }

    public void addToPDGDeathList(String PDF){
        deathPDFList.add(PDF);
    }

    public String getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(String idPerson) {
        this.idPerson = idPerson;
    }

    public Set<String> getBirthPDFList() {
        return birthPDFList;
    }

    public void setBirthPDFList(HashSet<String> birthPDFList) {
        this.birthPDFList = birthPDFList;
    }

    public Set<String> getMarriagePDFList() {
        return marriagePDFList;
    }

    public void setMarriagePDFList(HashSet<String> marriagePDFList) {
        this.marriagePDFList = marriagePDFList;
    }

    public Set<String> getDeathPDFList() {
        return deathPDFList;
    }

    public void setDeathPDFList(HashSet<String> deathPDFList) {
        this.deathPDFList = deathPDFList;
    }

    @Override
    public String toString() {
        return "¤PDF" + idPerson + "¤{" +
                "birthPDFList=" + birthPDFList +
                ", marriagePDFList=" + marriagePDFList +
                ", deathPDFList=" + deathPDFList +
                "}¤PDF" + idPerson + "¤";
    }

    public static PDFStructure parsePDFStucture(String contents, String id){
        PDFStructure pdfStructure = new PDFStructure();
        pdfStructure.setIdPerson(id);
        String PDFRegex = "¤PDF" + id + "¤\\{birthPDFList=\\[(.*)\\], marriagePDFList=\\[(.*)\\], deathPDFList=\\[(.*)\\]\\}¤PDF" + id + "¤";
        Pattern pattern = Pattern.compile(PDFRegex,Pattern.DOTALL);
        Matcher matcher = pattern.matcher(contents);
        if (matcher.find() && matcher.groupCount() == 3){
            String birthPDFList = matcher.group(1);
            String marriagePDFList = matcher.group(2);
            String deathPDFList = matcher.group(3);
            pdfStructure.setBirthPDFList(convertStringToArrayList(birthPDFList));
            pdfStructure.setMarriagePDFList(convertStringToArrayList(marriagePDFList));
            pdfStructure.setDeathPDFList(convertStringToArrayList(deathPDFList));
        }
        return pdfStructure;
    }

    public static HashSet<String> convertStringToArrayList(String input){
        String[] tab = input.split(", ");
        HashSet<String> result = new HashSet<>();
        for (int i = 0 ; i < tab.length ; i++){
            result.add(tab[i]);
        }
        return result;
    }

    public static void main(String[] args ) throws Exception {
        System.out.println("Hello");
        PDFStructure pdfStructure = new PDFStructure();
        pdfStructure.setIdPerson("1");
        pdfStructure.addToPDFBirthList("address1");
        pdfStructure.addToPDFMarriageList("address2");
        pdfStructure.addToPDGDeathList("address3");
        pdfStructure.addToPDGDeathList("address4");
        String data = "" + pdfStructure;
        System.out.println(data);
        PDFStructure myStructure = parsePDFStucture(data,"1");
        System.out.println(myStructure);
    }

}
