package Genealogy.URLConnexion.Geneanet;

import Genealogy.Genealogy;
import Genealogy.Model.Act.Union;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Person;
import Genealogy.Model.Town;
import Genealogy.Parsing.MyGedcomReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static Genealogy.URLConnexion.Geneanet.GeneanetBrowser.mainSearchFullTree;

/**
 * Created by Dan on 02/12/2018.
 */
public class TreeComparator {
    private GeneanetPerson geneanetRoot;
    private Person gedcomRoot;
    private HashMap<GeneanetPerson,String> differences = new HashMap<>();

    public TreeComparator(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        this.geneanetRoot = geneanetPerson;
        this.gedcomRoot = gedcomPerson;
    }

    public GeneanetPerson getGeneanetRoot() {
        return geneanetRoot;
    }

    public void setGeneanetRoot(GeneanetPerson geneanetRoot) {
        this.geneanetRoot = geneanetRoot;
    }

    public Person getGedcomRoot() {
        return gedcomRoot;
    }

    public void setGedcomRoot(Person gedcomRoot) {
        this.gedcomRoot = gedcomRoot;
    }

    public void printDifferences(){
        for(Map.Entry<GeneanetPerson, String> entry : differences.entrySet()) {
            System.out.println("\"" + entry.getKey().getUrl() + "\";" + entry.getKey().getFullName() + ";" + entry.getValue());

        }
    }

    private void compareRoot() {
        compareNames(geneanetRoot, gedcomRoot);
        compareBirth(geneanetRoot, gedcomRoot);
        compareDeath(geneanetRoot, gedcomRoot);
        compareMarriage(geneanetRoot, gedcomRoot);
        compareSiblings(geneanetRoot, gedcomRoot);
        compareHalfSiblings(geneanetRoot, gedcomRoot);
        compareChildren(geneanetRoot, gedcomRoot);

        //calls
    }

    private void comparePersons(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        compareNames(geneanetPerson, gedcomPerson);
        compareBirth(geneanetPerson, gedcomPerson);
        compareDeath(geneanetPerson, gedcomPerson);
        compareMarriage(geneanetPerson, gedcomPerson);
        compareSiblings(geneanetPerson, gedcomPerson);
        compareHalfSiblings(geneanetPerson, gedcomPerson);

        //calls
    }

    private void compareChildren(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        ArrayList<GeneanetPerson> childrenGen = geneanetPerson.getChildren();
        ArrayList<Person> children1Ged = gedcomPerson.getChildren();
        if (!superEquals(children1Ged, childrenGen)){
            addDifference(geneanetPerson,"Children=" + childrenGen );
        }
    }

    private void compareHalfSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        ArrayList<GeneanetPerson> halfSiblingsGen = geneanetPerson.getHalfSiblings();
        ArrayList<Person> halfSiblingGed = gedcomPerson.getHalfSiblings();
        if (!superEquals(halfSiblingsGen, halfSiblingGed)){
            addDifference(geneanetPerson,"HalfSiblings=" + halfSiblingsGen );
        }

    }

    private void compareSiblings(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        ArrayList<GeneanetPerson> siblingsGen = geneanetPerson.getSiblings();
        ArrayList<Person> siblingsGed = gedcomPerson.getSiblings();
        if (!superEquals(siblingsGen, siblingsGed)){
            addDifference(geneanetPerson,"Siblings=" + siblingsGen );
        }

    }

    private void compareMarriage(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        HashMap<GeneanetPerson, HashMap<MyDate, String>> marriageGen = geneanetPerson.getMarriage();
        ArrayList<Union> marriageGed = gedcomPerson.getUnions();

        for(Map.Entry<GeneanetPerson, HashMap<MyDate, String>> entry : marriageGen.entrySet()) {
            GeneanetPerson person = entry.getKey();
            HashMap<MyDate, String> value = entry.getValue();
            for(Map.Entry<MyDate, String> entry2 : value.entrySet()) {
                MyDate date = entry2.getKey();
                String town = entry2.getValue();
                if (!supercontains(marriageGed,person,date,town)){
                    addDifference(geneanetPerson,"Marriage=" + person.getFullName() + ";" + date + ";" + town );
                }
            }
        }
    }

    private static boolean supercontains(ArrayList<Union> marriageGed, GeneanetPerson person, MyDate date, String town) {
        for (Union union : marriageGed){
            Person partner = union.getCitizen();
            MyDate date1 = union.getDate();
            Town town1 = union.getTown();
            if (superEquals(partner.getFullName(),person.getFullName())&&superEquals(date,date1)&&superEquals(town,town1.getName())){
                return true;
            }
        }
        return false;
    }

    private void compareDeath(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        MyDate DeathDateGen = geneanetPerson.getDeathDate();
        MyDate DeathDateGed = gedcomPerson.getDeath().getDate();
        if (!superEquals(DeathDateGed,DeathDateGen)){
            addDifference(geneanetPerson,"DeathDate=" + DeathDateGen );
        }

        String placeOfDeathGen = geneanetPerson.getPlaceOfDeath();
        String placeOfDeathGed = gedcomPerson.getDeath().getTown().getName();
        if (!superEquals(placeOfDeathGen,placeOfDeathGed)){
            addDifference(geneanetPerson,"DeathPlace=" + placeOfDeathGen );
        }
    }

    private void compareBirth(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        MyDate birthDateGen = geneanetPerson.getBirthDate();
        MyDate birthDateGed = gedcomPerson.getBirth().getDate();
        if (!superEquals(birthDateGed,birthDateGen)){
            addDifference(geneanetPerson,"BirthDate=" + birthDateGen );
        }

        String placeOfBirthGen = geneanetPerson.getPlaceOfBirth();
        String placeOfBirthGed = gedcomPerson.getBirth().getTown().getName();
        if (!superEquals(placeOfBirthGen,placeOfBirthGed)){
            addDifference(geneanetPerson,"BirthPlace=" + placeOfBirthGen );
        }
    }
    private static boolean superEquals(Object object1, Object object2){
        if (object1 == null && object2 == null){
            return true;
        } else if (object1 != null && object2 != null){
            return object1.equals(object2);
        } else {
            return false;
        }
    }

    private static boolean superContains(ArrayList<String> list, String input){
        for (String string : list){
            if (superEquals(string, input)){
                return true;
            }
        }
        return false;
    }

    private static boolean superEquals(ArrayList<String> children1, ArrayList<Person> children2){
        for (Person person : children2){
            if (!superContains(children1, person.getFullName())){
                return false;
            }
        }
        return true;
    }

    private static boolean superEquals(String string1, String string2){
        if (string1 == null && string2 == null){
            return true;
        } else if (string1 != null && string2 != null){
            String newString1 = StringUtils.stripAccents(string1).toLowerCase();
            String newString2 = StringUtils.stripAccents(string2).toLowerCase();
            if (!newString1.equals(newString2)){
                //Synonyms
                newString1 = newString1.replace("boiau","boyau").replaceAll("\\d","").replace(" ","");
                newString2 = newString2.replace("boiau","boyau").replaceAll("\\d","").replace(" ","");
                return newString1.equals(newString2);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void addDifference(GeneanetPerson geneanetPerson, String txt){
        if (differences.containsKey(geneanetPerson)){
            String oldTxt = differences.get(geneanetPerson);
            differences.put(geneanetPerson,oldTxt + ";" + txt);
        } else {
            differences.put(geneanetPerson, txt);
        }
    }

    private void compareNames(GeneanetPerson geneanetPerson, Person gedcomPerson) {
        String firstNameGenea = geneanetPerson.getFirstName();
        String firstNameGed = gedcomPerson.getSurname();
        if (!superEquals(firstNameGenea, firstNameGed)){
            addDifference(geneanetPerson,"firstName=" + firstNameGenea );
        }

        String familyNameGenea = geneanetPerson.getFamilyName();
        String familyNameGed = gedcomPerson.getName();
        if (!superEquals(familyNameGenea, familyNameGed)){
            addDifference(geneanetPerson,"familyName=" + familyNameGenea );
        }
    }

    /** Write the object to a Base64 string. */
    private static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /** Read the object from Base64 string. */
    private static Object fromString( String s ) throws IOException ,
            ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    public static String saveGeneanetBrowser(GeneanetBrowser geneanetBrowser) throws IOException {
        String string = toString( geneanetBrowser );
        return string;
    }

    public static GeneanetBrowser getGeneanetBrowserFromString(String input) throws IOException, ClassNotFoundException {
        return ( GeneanetBrowser ) fromString( input );
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();

        //Geneanet Browser
        String testUrl = "http://gw.geneanet.org/roalda?lang=fr&pz=ronald+guy&nz=arnaux&ocz=0&p=marie+anne&n=bardin";
        GeneanetBrowser geneanetBrowser = mainSearchFullTree(testUrl, false);

        //String result = saveGeneanetBrowser(geneanetBrowser);
        //String result = "rO0ABXNyAC9HZW5lYWxvZ3kuVVJMQ29ubmV4aW9uLkdlbmVhbmV0LkdlbmVhbmV0QnJvd3NlchZ8qXSSj/JVAgAISQAQZXhwZWN0ZWROYlBlb3BsZUkACG5iUGVvcGxlTAAGY29va2lldAAPTGphdmEvdXRpbC9NYXA7TAANZ2VuZWFuZXRUcmVlc3QAFUxqYXZhL3V0aWwvQXJyYXlMaXN0O0wACXBlb3BsZVVybHQAE0xqYXZhL3V0aWwvSGFzaFNldDtMAApyb290UGVyc29udAAwTEdlbmVhbG9neS9VUkxDb25uZXhpb24vR2VuZWFuZXQvR2VuZWFuZXRQZXJzb247TAAMc2VhcmNoT3V0cHV0dAASTGphdmEvbGFuZy9TdHJpbmc7TAADdXJscQB+AAV4cAAAAAAAAAAHc3IAF2phdmEudXRpbC5MaW5rZWRIYXNoTWFwNMBOXBBswPsCAAFaAAthY2Nlc3NPcmRlcnhyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAADdAAIZ250c2VzczV0ABpnc3VzaHI1azRvM2x0YXB0dTAxaHRqN3BwNnQAElZpc2l0b3JMaW1pdENvb2tpZXQAB2RlbGV0ZWR0AApSRU1FTUJFUk1FdACwUjJWdVpXRnVaWFJjUW5WdVpHeGxYRlZ6WlhKQ2RXNWtiR1ZjUlc1MGFYUjVYRlZ6WlhJNlpFZDRhR050T1RGak0wNXNPakUxTkRreE5EY3pNREE2Tm1KbU1HUTVNbU5pTUdGaVpUTm1NelJsWkRCaFkyVTNNR1U0WVdOa1pXUTFOVFUzTmpCa05UVXdOek5sWmpZNE5tVmxNbVF5WlRRNU1XWTFORGhrWmclM0QlM0R4AHNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAAqdwQAAAAqc3IALEdlbmVhbG9neS5VUkxDb25uZXhpb24uR2VuZWFuZXQuR2VuZWFuZXRUcmVlvrwz5HbZVtoCAARJAAhnZWRjb21JZEkADHBlb3BsZU51bWJlckwABG5hbWVxAH4ABUwAA3VybHEAfgAFeHD/////AAAAB3QABnJvYWxkYXQAWWh0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmb2N6PTAmcD1tYXJpZSthbm5lJm49YmFyZGluc3EAfgAS/////wAAAC50AAtmYW1pbHlzb3llcnQAR2h0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvZmFtaWx5c295ZXI/bGFuZz1mciZpej0wJnA9bG91aXMrbGVvbm9yZSZuPWJhZGluc3EAfgAS/////wAAADd0AARnbHVjdABoaHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9nbHVjP2xhbmc9ZnImcHo9aXJpcyticmlnaXR0ZStkb21pbmlxdWUmbno9cGlnbmF1ZCZvY3o9MCZwPWxvdWlzK2VsZW9ub3JlJm49YmFkaW5zcQB+ABL/////AAAARnQACHN5bHZpZWI0dAB5aHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9zeWx2aWViND9sYW5nPWZyJnB6PXN5bHZpZStqYWNxdWVsaW5lK21hcmllK2Jlcm5hZGV0dGUmbno9YmVyZ2VyZWF1Jm9jej0wJnA9amVhbitqYWNxdWVzJm49Ym9pbGVhdXNxAH4AEv////8AAAALdAAOamVhbm5pbmVsZWdlcjF0AEZodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL2plYW5uaW5lbGVnZXIxP2xhbmc9ZnImcD1tYXJpZSZuPWJvdWNoZXJvbiZvYz0yc3EAfgAS/////wAAAAN0AAhqbHJlbmF1ZHQAWWh0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvamxyZW5hdWQ/bGFuZz1mciZwej1qZWFuK2xvdWlzJm56PXJlbmF1ZCZvY3o9MCZwPWFudG9pbmUmbj1iYXVsZ2V0c3EAfgAS/////wAAAAp0AA1taWNoZWxub3JtYW5kdABBaHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9taWNoZWxub3JtYW5kP2xhbmc9ZnImcD1jYXRoZXJpbmUmbj1jb2xsZXRzcQB+ABL/////AAAABnQAB2hhbW9uMjJ0AGJodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL2hhbW9uMjI/bGFuZz1mciZwej1yb21haW4rYWltZSZuej1hYmJydXp6ZXNlJm9jej0wJnA9aW5jb25udSZuPWNvbGxldCZvYz0xNnNxAH4AEv////8AAAAEdAAIZHV2YWNoYXR0AEFodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL2R1dmFjaGF0P2xhbmc9ZnImcD1ndWlsbGF1bWUmbj1jb2xsZXQmb2M9MXNxAH4AEv////8AAAAFdAAGcGhpbDY4dAA6aHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9waGlsNjg/bGFuZz1mciZwPWd1aWxsYXVtZSZuPWNvbGxldHNxAH4AEv////8AAABtdAAJanZpbGxldHRldABUaHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9qdmlsbGV0dGU/bGFuZz1mciZwej1qdWxpZW4mbno9ZHVyYW5kJm9jej0wJnA9cGllcnJlJm49Y29sbGV0c3EAfgAS/////wAAAB10AAhtYW5nZXJldHQAZGh0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvbWFuZ2VyZXQ/bGFuZz1mciZwej1yb21haW4rbWljaGVsK2ZyYW5jb2lzJm56PW1hbmdlcmV0Jm9jej0wJnA9YW5kcmUmbj1jb3VydHlzcQB+ABL/////AAAAHnQAB3BpZXJyZWZ0AFRodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL3BpZXJyZWY/bGFuZz1mciZwej1hbm5pZSZuej1mZXJtaWVyJm9jej0wJnA9bWFyaWUmbj1kZWxhdmlsbGVzcQB+ABL/////AAAALXQACmdhdWRpbGxhdHB0AGxodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL2dhdWRpbGxhdHA/bGFuZz1mciZwej1hcm5hdWQrbWFyYytwYXRyaWNrJm56PWdhdWRpbGxhdCZvY3o9MCZwPW1hcmllJm49ZGVsYXZpbGxlJm9jPTJzcQB+ABL/////AAAAA3QAB21nYW5pZXJ0AGFodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL21nYW5pZXI/bGFuZz1mciZwej1sb3VpcythdWd1c3RlJm56PWdhbmllciZvY3o9MCZwPWVtbWFudWVsK2FsYmVydCZuPWRyaWV1c3EAfgAS/////wAAAAN0AApqZWFuaGluYXJkdABBaHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9qZWFuaGluYXJkP249ZnJlbWluZSZvYz0mcD1tYXJpZSttYWRlbGVpbmVzcQB+ABL/////AAAADHQADm11cnBoeWF1emVyYWlzdABFaHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9tdXJwaHlhdXplcmFpcz9uPWZyZW1pbmUmb2M9JnA9bWFyaWUrbWFkZWxlaW5lc3EAfgAS/////wAAAA90AAVjcmFsdXQAPGh0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvY3JhbHU/bj1mcmVtaW5lJm9jPSZwPW1hcmllK21hZGVsYWluZXNxAH4AEv////8AAAAEdAAFc29ib3V0ADVodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL3NvYm91P249aGluYXJkJm9jPSZwPWNhdGhlcmluZXNxAH4AEv////8AAAAOdAAPb2xpdmllcmNhaWxsZWF1dAA/aHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9vbGl2aWVyY2FpbGxlYXU/bj1oaW5hcmQmb2M9JnA9Y2F0aGVyaW5lc3EAfgAS/////wAAAAN0AA5waWVycmVwZXRyZXF1aXQAPmh0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvcGllcnJlcGV0cmVxdWk/bj1oaW5hcmQmb2M9JnA9Y2F0aGVyaW5lc3EAfgAS/////wAAAAd0AAlmcmFuY29pc3R0ADlodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL2ZyYW5jb2lzdD9uPWhpbmFyZCZvYz0mcD1jYXRoZXJpbmVzcQB+ABL/////AAAACXQABWJldGExdABwaHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9iZXRhMT9sYW5nPWZyJnB6PWJlcm5hcmQrZnJhbmNpcyZuej1hbHF1aWVyJm9jej0wJnA9bWFyaWUrbWFyZ3Vlcml0ZStlbGlzYWJldGgmbj1sYW5nbG9pc3NxAH4AEv////8AAABLdAAFcm9nbzF0ADtodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL3JvZ28xP249bGViYXJneSZvYz0mcD1tYXJpZStjZXphcmluZXNxAH4AEv////8AAAAPdAALYmFydGhyaWNoZXJ0AGpodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL2JhcnRocmljaGVyP2xhbmc9ZnImcHo9Y2F0aGVyaW5lK2Zhbm55Jm56PWpldXZyZXkmb2N6PTAmcD1ndWlsbGF1bWUmbj1sZWZlYnZyZSZvYz0xc3EAfgAS/////wAAAEZ0AAxkZW5pc2NodXF1ZXR0AHNodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL2RlbmlzY2h1cXVldD9sYW5nPWZyJnB6PWplYW4rbWljaGVsK2Jlcm5hcmQmbno9Y2h1cXVldCZvY3o9MCZwPWphY3F1ZXMrcGllcnJlK2pvc2VwaCZuPWxlcm95c3EAfgAS/////wAAAEh0AAttamRlc29ldXZyZXQAOmh0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvbWpkZXNvZXV2cmU/bj1tYWJpbGF0Jm9jPSZwPXRoZXJlc2VzcQB+ABL/////AAAAHXQABXRydWN5dAA8aHR0cDovL2d3LmdlbmVhbmV0Lm9yZy90cnVjeT9sYW5nPWZyJnA9amVhbm5lJm49bW9jcXVvdCZvYz0xc3EAfgAS/////wAAACV0AAlnZXJ1d2VuZTF0AD1odHRwOi8vZ3cuZ2VuZWFuZXQub3JnL2dlcnV3ZW5lMT9sYW5nPWZyJnA9amFjcXVlcyZuPXJhYmlsbG9uc3EAfgAS/////wAAAC50AAdqYWNxYnJpdABVaHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvamFjcWJyaT9sYW5nPWZyJnB6PW1pY2hlbCZuej1iYXJiaWV1eCZvY3o9MCZwPW1hcmllMiZuPXNhdmFyeXNxAH4AEv////8AAABZdAAFYW1jNTB0ADhodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL2FtYzUwP249c2ltb24mb2M9JnA9Y2VsZXN0ZStqdWxpZXNxAH4AEv////8AAAAhdAAHbWljaGExMnQAOmh0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvbWljaGExMj9uPXNpbW9uJm9jPSZwPWNlbGVzdGUranVsaWVzcQB+ABL/////AAAABXQADmZhYnJpY2VsZW5vYmxldABIaHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9mYWJyaWNlbGVub2JsZT9sYW5nPWZyJnA9cGllcnJlK2xvdWlzJm49dGhpZWJhdWx0c3EAfgAS/////wAAABZ0AARmdWZ1dAA8aHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9mdWZ1P249cmlnbGV0Jm9jPSZwPWF1Z3VzdGluZStyb3NhbGllc3EAfgAS/////wAAAAd0AAltYW15bmFubmV0AFpodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL21hbXluYW5uZT9sYW5nPWZyJnB6PWV0aWVubmUmbno9ZGVuaXMmb2N6PTAmcD1qZWFuK2JhcHRpc3RlJm49bWFyaWVzcQB+ABL/////AAAACHQACm1kb252aWxsZTF0AERodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL21kb252aWxsZTE/bGFuZz1mciZpej0zMDQ2JnA9bWFyaWUmbj1kb252aWxsZXNxAH4AEv////8AAABKdAAJaHViZXJ0cDgzdABQaHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9odWJlcnRwODM/bGFuZz1mciZwPW1hcmllK21hcmd1ZXJpdGUrZWxpc2FiZXRoJm49bGFuZ2xvaXNzcQB+ABL/////AAAAFnQACXNsZWJydW1hbnQASWh0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvc2xlYnJ1bWFuP2xhbmc9ZnImcD1sb3Vpc2UrdmVyb25pcXVlK21hcmllJm49bGVyb3lzcQB+ABL/////AAACQ3QAC21zZWJhc3RpZW4xdABgaHR0cDovL2d3LmdlbmVhbmV0Lm9yZy9tc2ViYXN0aWVuMT9sYW5nPWZyJnB6PXNlYmFzdGllbitkYXZpZCZuej1tZXJjaWVyJm9jej0wJnA9bG91aXMmbj10aGllcnJ5c3EAfgAS/////wAAAit0AAhsaXNldG94ZXQARGh0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvbGlzZXRveGU/bGFuZz1mciZwPWxvdWlzZStqb3NlcGhpbmUmbj10aGllcnJ5c3EAfgAS/////wAAArd0AANkaWx0AD5odHRwOi8vZ3cuZ2VuZWFuZXQub3JnL2RpbD9sYW5nPWZyJml6PTAmcD1lcm5lc3QrbGVvbiZuPWJlcnRvbnNxAH4AEv////8AAARHdAAKZ2VuZWE1MGNvbXQAS2h0dHA6Ly9ndy5nZW5lYW5ldC5vcmcvZ2VuZWE1MGNvbT9sYW5nPWZyJnA9bWFyaWUrbWFkZWxlaW5lJm49ZG91dmlsbGUmb2M9NXhzcgARamF2YS51dGlsLkhhc2hTZXS6RIWVlri3NAMAAHhwdwwAAAAQP0AAAAAAAAZ0AFtodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZvY3o9MCZwPWplYW4ramFjcXVlcyZuPWJveWF1dABYaHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmb2N6PTAmcD1jYXRoZXJpbmUmbj1iZXJyeXQAV2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4Jm9jej0wJnA9amFjcXVlcyZuPWJhcmRpbnQAVWh0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4Jm9jej0wJnA9bWFydGluJm49Ym95YXV0AFRodHRwczovL2d3LmdlbmVhbmV0Lm9yZy9yb2FsZGE/bGFuZz1mciZwej1yb25hbGQrZ3V5Jm56PWFybmF1eCZvY3o9MCZwPWFubmUmbj1jb25uaW50AFlodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4Jm9jej0wJnA9bWFyaWUrYW5uZSZuPWJhcmRpbnhzcgAuR2VuZWFsb2d5LlVSTENvbm5leGlvbi5HZW5lYW5ldC5HZW5lYW5ldFBlcnNvbtFkhxvV7zZBAgAWWgAQaXNVc2luZ0RhdGVUYWJsZVoACnJvb3RwZXJzb25aAAhzZWFyY2hlZEwACWJpcnRoRGF0ZXQAHUxHZW5lYWxvZ3kvTW9kZWwvRGF0ZS9NeURhdGU7TAAKYnVyaWFsRGF0ZXEAfgCaTAAIY2hpbGRyZW5xAH4AAkwAD2NocmlzdGVuaW5nRGF0ZXEAfgCaTAAJZGVhdGhEYXRlcQB+AJpMAApmYW1pbHlOYW1lcQB+AAVMAAZmYXRoZXJxAH4ABEwACWZpcnN0TmFtZXEAfgAFTAAGZ2VuZGVydAAoTEdlbmVhbG9neS9VUkxDb25uZXhpb24vR2VuZWFuZXQvR2VuZGVyO0wAC2dlbmVhbmV0VXJscQB+AAVMAAxoYWxmU2libGluZ3NxAH4AAkwACG1hcnJpYWdldAATTGphdmEvdXRpbC9IYXNo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FENoYXRpbGxvbiBDb2xpZ255IDQ1c3EAfgAQAAAAAHcEAAAAAHhxAH4Al3BwcHBzcQB+ABAAAAAAdwQAAAAAeHEAfgCYdAjZR2VuZWFuZXRQZXJzb257dXJsPSdodHRwOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4Jm9jej0wJnA9bWFyaWUrYW5uZSZuPWJhcmRpbicsIGZpcnN0TmFtZT0nTWFyaWUgQW5uZScsIGZhbWlseU5hbWU9J0JBUkRJTicsIGdlbmRlcj0nRmVtYWxlJywgYmlydGhEYXRlPScxODE2JywgZmF0aGVyPSdudWxsIG51bGwnLCBtb3RoZXI9J251bGwgbnVsbCcsIHNpYmxpbmdzPSdudWxsIG51bGwnLCBtYXJyaWFnZT0nbnVsbCBudWxsO3swNS8xMi8xODM3PUNoYXRpbGxvbiBDb2xpZ255IDQ1fScsIHNlYXJjaGVkPSd0cnVlJywgcm9vdHBlcnNvbj0ndHJ1ZScNCkdlbmVhbmV0UGVyc29ue3VybD0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmb2N6PTAmcD1qYWNxdWVzJm49YmFyZGluJywgZmlyc3ROYW1lPSdKYWNxdWVzJywgZmFtaWx5TmFtZT0nQkFSRElOJywgZ2VuZGVyPSdNYWxlJywgY2hpbGRyZW49J251bGwgbnVsbG51bGwgbnVsbCcsIG1hcnJpYWdlPSdudWxsIG51bGw7e251bGw9bnVsbH0nLCBzZWFyY2hlZD0ndHJ1ZScNCkdlbmVhbmV0UGVyc29ue3VybD0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmb2N6PTAmcD1hbm5lJm49Y29ubmluJywgZmlyc3ROYW1lPSdBbm5lJywgZmFtaWx5TmFtZT0nQ09OTklOJywgZ2VuZGVyPSdGZW1hbGUnLCBkZWF0aERhdGU9JzI0LzEyLzE4MTcnLCBwbGFjZU9mRGVhdGg9J0NoYXRpbGxvbiBDb2xpZ255IDQ1JywgY2hpbGRyZW49J251bGwgbnVsbG51bGwgbnVsbCcsIG1hcnJpYWdlPSdudWxsIG51bGw7e251bGw9bnVsbH0nLCBzZWFyY2hlZD0ndHJ1ZScNCkdlbmVhbmV0UGVyc29ue3VybD0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmb2N6PTAmcD1waWVycmUramFjcXVlcyZuPWJhcmRpbicsIGZpcnN0TmFtZT0nUGllcnJlIEphY3F1ZXMnLCBmYW1pbHlOYW1lPSdCQVJESU4nLCBnZW5kZXI9J01hbGUnLCBiaXJ0aERhdGU9JzE4LzA5LzE4MDgnLCBwbGFjZU9mQmlydGg9J0Now6J0aWxsb24tQ29saWdueScsIGRlYXRoRGF0ZT0nMjkvMDMvMTg3MicsIHBsYWNlT2ZEZWF0aD0nQ2jDonRpbGxvbi1Db2xpZ255JywgZmF0aGVyPSdudWxsIG51bGwnLCBtb3RoZXI9J251bGwgbnVsbCcsIHNpYmxpbmdzPSdudWxsIG51bGwnLCBjaGlsZHJlbj0nbnVsbCBudWxsbnVsbCBudWxsbnVsbCBudWxsJywgbWFycmlhZ2U9J251bGwgbnVsbDt7MzAvMDQvMTgzOT1DaMOidGlsbG9uLUNvbGlnbnl9Jywgc2VhcmNoZWQ9J3RydWUnDQpHZW5lYW5ldFBlcnNvbnt1cmw9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4Jm9jej0wJnA9amVhbitqYWNxdWVzJm49Ym95YXUnLCBmaXJzdE5hbWU9J0plYW4gSmFjcXVlcycsIGZhbWlseU5hbWU9J0JPWUFVJywgZ2VuZGVyPSdNYWxlJywgYmlydGhEYXRlPScwMi8wOC8xODEwJywgcGxhY2VPZkJpcnRoPSdTYWludGUgR2VuZXZpZXZlIGRlcyBCb2lzIDQ1JywgZmF0aGVyPSdudWxsIG51bGwnLCBtb3RoZXI9J251bGwgbnVsbCcsIG1hcnJpYWdlPSdudWxsIG51bGw7ezA1LzEyLzE4Mzc9Q2hhdGlsbG9uIENvbGlnbnkgNDV9Jywgc2VhcmNoZWQ9J3RydWUnDQpHZW5lYW5ldFBlcnNvbnt1cmw9J2h0dHBzOi8vZ3cuZ2VuZWFuZXQub3JnL3JvYWxkYT9sYW5nPWZyJnB6PXJvbmFsZCtndXkmbno9YXJuYXV4Jm9jej0wJnA9bWFydGluJm49Ym95YXUnLCBmaXJzdE5hbWU9J01hcnRpbicsIGZhbWlseU5hbWU9J0JPWUFVJywgZ2VuZGVyPSdNYWxlJywgY2hpbGRyZW49J251bGwgbnVsbCcsIG1hcnJpYWdlPSdudWxsIG51bGw7e251bGw9bnVsbH0nLCBzZWFyY2hlZD0ndHJ1ZScNCkdlbmVhbmV0UGVyc29ue3VybD0naHR0cHM6Ly9ndy5nZW5lYW5ldC5vcmcvcm9hbGRhP2xhbmc9ZnImcHo9cm9uYWxkK2d1eSZuej1hcm5hdXgmb2N6PTAmcD1jYXRoZXJpbmUmbj1iZXJyeScsIGZpcnN0TmFtZT0nQ2F0aGVyaW5lJywgZmFtaWx5TmFtZT0nQkVSUlknLCBnZW5kZXI9J0ZlbWFsZScsIGRlYXRoRGF0ZT0nMTgxNCcsIHBsYWNlT2ZEZWF0aD0nQ2hhdGlsbG9uIENvbGlnbnkgNDUnLCBjaGlsZHJlbj0nbnVsbCBudWxsJywgbWFycmlhZ2U9J251bGwgbnVsbDt7bnVsbD1udWxsfScsIHNlYXJjaGVkPSd0cnVlJw0KcQB+AJg=";
        //geneanetBrowser = getGeneanetBrowserFromString(result);
        GeneanetPerson rootPerson = geneanetBrowser.rootPerson;
        System.out.println(geneanetBrowser.rootPerson);

        //Gedcom file
        String gedcomFile = "C:\\Users\\Dan\\Desktop\\Programmation\\IntelliJ\\Genealogy\\Genealogy\\src\\main\\resources\\famille1.ged";
        MyGedcomReader myGedcomReader = new MyGedcomReader();
        Genealogy.genealogy = myGedcomReader.read(gedcomFile);
        Genealogy.genealogy.parseContents();
        Genealogy.genealogy.sortPersons();
        Person person = Genealogy.genealogy.findPersonById("@I93@");
        System.out.println(person);

        //Comparing
        TreeComparator treeComparator = new TreeComparator(rootPerson,person);
        treeComparator.compareRoot();
        treeComparator.printDifferences();
    }

}
