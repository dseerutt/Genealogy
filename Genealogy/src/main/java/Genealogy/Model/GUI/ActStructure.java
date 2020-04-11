package Genealogy.Model.GUI;

/**
 * Created by Dan on 22/04/2016.
 */
public class ActStructure {
    private String noms;
    private int nombre;
    private String town;

    public ActStructure(String noms, String town) {
        this.noms = noms;
        this.nombre = 1;
        this.town = town;
    }

    public String getNoms() {
        return noms;
    }

    public void addNom(String nom){
        if (nombre < 11){
            if (!noms.contains(nom)){
                noms += "<br>" + nom;
            }
        } else if (nombre == 11){
            noms += "<br>...";
        }

        nombre++;
    }

    public void setNoms(String noms) {
        this.noms = noms;
    }

    public int getNombre() {
        return nombre;
    }

    public void setNombre(int nombre) {
        this.nombre = nombre;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getTooltip(){
        return "<html><u><font size=\"5\"><b>" + town
                + " :</b></font></u><br>" + noms + "</html>";
    }
}
