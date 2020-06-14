package Genealogy.Model.Act;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProofTest {
    /**
     * Proof constructor test with regex for fullDate, MonthDate and YearDate
     */
    @Test
    public void proofConstructorTest() {
        //init & launch
        Proof proofClassic = new Proof("- La Cambe (Calvados) 25/11/1900 Naissance Louis Valin");
        Proof proofMonth = new Proof("- La Cambe (Calvados) 11/1900 Décès Louis Valin");
        Proof proofYear = new Proof("- La Cambe (Calvados) 1900 Mariage Louis Valin Louise Comma");
        //test
        assertEquals("Proof{date='25/11/1900', town='La Cambe (Calvados)', people='Louis Valin', typeAct='Naissance'}", proofClassic.toString());
        assertEquals("Proof{date='11/1900', town='La Cambe (Calvados)', people='Louis Valin', typeAct='Décès'}", proofMonth.toString());
        assertEquals("Proof{date='1900', town='La Cambe (Calvados)', people='Louis Valin Louise Comma', typeAct='Mariage'}", proofYear.toString());

    }
}