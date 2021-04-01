package Genealogy.URLConnexion.Geneanet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneanetBrowserTest {

    public GeneanetBrowserTest() {

    }

    public String readPersonFromFile(GeneanetConverter converter, String fileName) throws Exception {
        GeneanetPerson person = new GeneanetPerson("");
        String fileName2 = "integrationTest/" + fileName;
        File input = new File(getClass().getClassLoader().getResource(fileName2).getFile());
        Document document = Jsoup.parse(input, "UTF-8", "");
        converter.parseDocument(document, person);
        return person.toString();
    }

    @Test
    public void integrationTest() throws Exception {
        GeneanetBrowser browser = new GeneanetBrowser("");
        GeneanetConverter converter = browser.getGeneanetConverter();
        //https://gw.geneanet.org/dil?lang=fr&p=jacques&n=de+silliers
        assertEquals(readPersonFromFile(converter, "jacquesDeSilliers.html"), "GeneanetPerson{url='', geneanetURL='https://gw.geneanet.org/dil?lang=fr&iz=0&m=RL&i1=10175&i2=0&b1=1&b2=6120', firstName='Jacques', familyName='de SILLIERS', gender='M', placeOfBirth='Les Sièges', placeOfDeath='Saint-Hilaire', father='https://gw.geneanet.org/dil?lang=fr&p=jacques&n=de+silliers&oc=1', mother='https://gw.geneanet.org/dil?lang=fr&p=francoise&n=de+chicault', siblings='https://gw.geneanet.org/dil?lang=fr&p=charles&n=de+silliers', children='https://gw.geneanet.org/dil?lang=fr&p=suzanne+jeanne&n=de+silliers;https://gw.geneanet.org/dil?lang=fr&p=jacques&n=de+silliers&oc=2;https://gw.geneanet.org/dil?lang=fr&p=charles&n=de+silliers&oc=1;https://gw.geneanet.org/dil?lang=fr&p=charlotte+catherine&n=de+silliers;https://gw.geneanet.org/dil?lang=fr&p=jeanne+claude&n=de+silliers;https://gw.geneanet.org/dil?lang=fr&p=jean&n=de+silliers', marriage='https://gw.geneanet.org/dil?lang=fr&p=marguerite&n=charpentier;{null=null}https://gw.geneanet.org/dil?lang=fr&p=madeleine&n=de+pollangis;{09/02/1672=Saint-Hilaire - Sens}', searched='true'");
        //https://gw.geneanet.org/dil?lang=fr&p=jacques&n=de+silliers&oc=1
        assertEquals(readPersonFromFile(converter, "jacquesDeSilliers2.html"), "GeneanetPerson{url='', geneanetURL='https://gw.geneanet.org/dil?lang=fr&iz=0&m=RL&i1=10456&i2=0&b1=1&b2=12240', firstName='Jacques', familyName='de SILLIERS', gender='M', placeOfDeath='Les Sièges', children='https://gw.geneanet.org/dil?lang=fr&p=charles&n=de+silliers;https://gw.geneanet.org/dil?lang=fr&p=jacques&n=de+silliers', marriage='https://gw.geneanet.org/dil?lang=fr&p=francoise&n=de+chicault;{1644=null}', searched='true'");
        //https://gw.geneanet.org/dil?lang=fr&p=marie&n=fremy
        assertEquals(readPersonFromFile(converter, "marieFremy.html"), "GeneanetPerson{url='', geneanetURL='https://gw.geneanet.org/dil?lang=fr&iz=0&m=RL&i1=678&i2=0&b1=1&b2=373', firstName='Marie', familyName='FREMY', gender='F', birthDate='11/01/1716', placeOfBirth='Tannerre-en-Puisaye', deathDate='11/09/1763', placeOfDeath='La Ferté-Loupière', father='https://gw.geneanet.org/dil?lang=fr&p=claude&n=fremy', mother='https://gw.geneanet.org/dil?lang=fr&p=marie&n=digue', children='https://gw.geneanet.org/dil?lang=fr&p=theodule&n=courty;https://gw.geneanet.org/dil?lang=fr&p=andre&n=courty;https://gw.geneanet.org/dil?lang=fr&p=simon&n=courty;https://gw.geneanet.org/dil?lang=fr&p=marie&n=courty&oc=1;https://gw.geneanet.org/dil?lang=fr&p=pierre&n=courty&oc=3', marriage='https://gw.geneanet.org/dil?lang=fr&p=pierre&n=courty;{02/07/1743=Villiers-Saint-Benoît}', searched='true'");
        //https://gw.geneanet.org/dil?lang=fr&iz=0&p=edme&n=archambault
        assertEquals(readPersonFromFile(converter, "edmeAimeArchambault.html"), "GeneanetPerson{url='', geneanetURL='https://gw.geneanet.org/dil?lang=fr&iz=0&m=RL&i1=3626&i2=0&b1=1&b2=2996', firstName='Edme', familyName='ARCHAMBAULT', gender='M', placeOfBirth='Aillant-sur-Tholon', placeOfDeath='Aillant-sur-Milleron', father='https://gw.geneanet.org/dil?lang=fr&p=edme&n=archambault&oc=1', mother='https://gw.geneanet.org/dil?lang=fr&p=guillemette&n=rondin', siblings='https://gw.geneanet.org/dil?lang=fr&p=louis&n=archambault', children='https://gw.geneanet.org/dil?lang=fr&p=nicolas&n=archambault', marriage='https://gw.geneanet.org/dil?lang=fr&p=francoise&n=nyon;{10/06/1670=La Bussière}', searched='true'");
        //https://gw.geneanet.org/dil?lang=fr&p=aubin+gustave&n=ledroit
        assertEquals(readPersonFromFile(converter, "aubinGustaveLedroit.html"), "GeneanetPerson{url='', firstName='Aubin, Gustave', familyName='LEDROIT', gender='M', birthDate='02/02/1904', placeOfBirth='les sept écluses', deathDate='10/04/1976', placeOfDeath='Charbuy', father='https://gw.geneanet.org/dil?lang=fr&p=adrien&n=ledroit', mother='https://gw.geneanet.org/dil?lang=fr&p=celestine+louise&n=fourgeux', siblings='https://gw.geneanet.org/dil?lang=fr&p=theodore+desire&n=ledroit;https://gw.geneanet.org/dil?lang=fr&p=norbert+charles&n=ledroit;https://gw.geneanet.org/dil?lang=fr&p=desire+charles&n=ledroit;https://gw.geneanet.org/dil?lang=fr&p=suzanne+adrienne&n=ledroit;https://gw.geneanet.org/dil?lang=fr&p=lucien&n=ledroit;https://gw.geneanet.org/dil?lang=fr&p=roger+edmond&n=ledroit;https://gw.geneanet.org/dil?lang=fr&p=raymond+rene&n=ledroit;https://gw.geneanet.org/dil?lang=fr&p=norbert+andre&n=ledroit', searched='true'");
        //https://gw.geneanet.org/dil?lang=fr&p=leontine+victorine&n=archambault
        assertEquals(readPersonFromFile(converter, "leontineVictorineArchambault.html"), "GeneanetPerson{url='', firstName='Léontine, Victorine', familyName='ARCHAMBAULT', gender='F', birthDate='24/02/1855', placeOfBirth='Assigny', children='https://gw.geneanet.org/dil?lang=fr&p=louis+alexandre&n=aligon;https://gw.geneanet.org/dil?lang=fr&p=armantine+marie&n=aligon;https://gw.geneanet.org/dil?lang=fr&p=augustin+henri&n=aligon;https://gw.geneanet.org/dil?lang=fr&p=lucie+juliette&n=aligon;https://gw.geneanet.org/dil?lang=fr&p=marie+marguerite&n=aligon;https://gw.geneanet.org/dil?lang=fr&p=victoire+josephine&n=aligon;https://gw.geneanet.org/dil?lang=fr&p=amede+armand&n=aligon', marriage='https://gw.geneanet.org/dil?lang=fr&p=gustave&n=ledroit;{27/01/1912=Saint-Maurice-sur-Aveyron}https://gw.geneanet.org/dil?lang=fr&p=alexandre+joseph&n=aligon;{13/01/1874=Beaulieu-sur-Loire}https://gw.geneanet.org/dil?lang=fr&p=auguste&n=gessat;{25/01/1901=Rogny-les-Sept-Écluses}', searched='true'");
        //https://gw.geneanet.org/msebastien1?lang=fr&p=madeleine&n=jusseaume
        assertEquals(readPersonFromFile(converter, "madeleineJusseaume.html"), "GeneanetPerson{url='', firstName='Madeleine', familyName='JUSSEAUME', gender='F', placeOfBirth='Cudot', placeOfDeath='Prunoy', father='https://gw.geneanet.org/msebastien1?lang=fr&p=claude&n=jusseaume', mother='https://gw.geneanet.org/msebastien1?lang=fr&p=laurence&n=croisere', children='https://gw.geneanet.org/msebastien1?lang=fr&p=claude&n=vincent;https://gw.geneanet.org/msebastien1?lang=fr&p=laurent&n=vincent;https://gw.geneanet.org/msebastien1?lang=fr&p=madeleine&n=vincent', marriage='https://gw.geneanet.org/msebastien1?lang=fr&p=claude&n=vincent&oc=2;{16/06/1710=Prunoy}https://gw.geneanet.org/msebastien1?lang=fr&p=jean&n=renoncia;{26/06/1708=Cudot}', image='https://gw.geneanet.org/public/img/media/deposits/ba/85/8085310/medium.jpg?t=1486571164', searched='true'");
        //assertEquals(readPersonFromFile(converter, ".html"), "");
    }

}