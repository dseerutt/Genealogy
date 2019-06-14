package Genealogy.URLConnexion.Geneanet;

/**
 * Created by Dan on 04/12/2017.
 */
public enum Gender {
    Male,
    Female,
    Unknown;

    public static Gender getGender(String name){
        if (name != null){
            switch(name){
                case "H":
                    return Gender.Male;
                case "F":
                    return Gender.Female;
                case "U":
                    return Gender.Unknown;
                default:
                    break;
            }
        }
        return null;
    }

    public String toString(){
        if (this != null){
            switch(this){
                case Male:
                    return "M";
                case Female:
                    return "F";
                case Unknown:
                    return "U";
                default:
                    break;
            }
        }
        return null;
    }
}