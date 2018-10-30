package Genealogy.Model.Act;

import Genealogy.AuxMethods;
import Genealogy.Model.Date.MyDate;
import Genealogy.Model.Person;
import Genealogy.Model.Town;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dan on 06/04/2016.
 */
public class Union extends Act {
    protected Person partner;
    protected State state;
    public enum State {
        MARIAGE_HETERO,
        MARIAGE_HOMO,
        PAS_DE_MARIAGE;
    }

    public Union(Person person, Person person2, MyDate date, Town town, State state0) {

        super(person,date,town);
        state = state0;
        partner = person2;
        if (town != null){
            town.addTown(this);
        }
    }

    @Override
    public String toString() {
        return "Union{" +
                "citizen=" + citizen.getFullName() +
                ", partner=" + partner.getFullName() +
                ", date=" + date +
                ", town=" + town +
                ", state=" + state +
                ", proof=" + proofs +
                '}';
    }

    public static State parseState(String s){
        switch (s){
            case "NOT MARRIED":
                return State.PAS_DE_MARIAGE;
            case "F":
                return State.MARIAGE_HOMO;
            default:
                return State.MARIAGE_HETERO;
        }
    }

    public Person getPartner() {
        return partner;
    }

    public State getState() {
        return state;
    }

    public Person getOtherPerson(Person person){
        if (citizen.getId().equals(person.getId())){
            return partner;
        } else if (partner.getId().equals(person.getId())){
            return citizen;
        } else {
            return null;
        }
    }
}
