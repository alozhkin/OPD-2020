import database.Database;
import database.models.Website;
import database.models.Word;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class DatabaseTest {

    private Database database;

    @Before
    public void initDb() {
        database = Database.newInstance();
    }

    @Test
    public void testInsertWebsite() {
        assertTrue(database.putWebsite(1233, "https://alb-dach.de"));
        assertTrue(database.putWebsite(344, "https://wolfsperger-landmaschinen.de"));
        assertTrue(database.putWebsite(75, "https://nagel-gruppe.de"));
        assertTrue(database.putWebsite(6, "http://velte-steinmetz.de"));

        assertTrue(database.putWebsite(new Website(1233, "https://aalb-dach.de")));
        assertTrue(database.putWebsite(new Website(344, "https://wwolfsperger-landmaschinen.de")));
        assertTrue(database.putWebsite(new Website(75, "https://nnagel-gruppe.de")));
        assertTrue(database.putWebsite(new Website(6, "http://vvelte-steinmetz.de")));
    }

    @Test
    public void testInsertWebsites() {
        ArrayList<Website> list = new ArrayList<>();

        list.add(new Website(12345, "https://alb-dach.de"));
        list.add(new Website(123456, "https://wolfsperger-landmaschinen.de"));
        list.add(new Website(123567, "https://nagel-gruppe.de"));
        list.add(new Website(12346, "http://velte-steinmetz.de"));

        list.add(new Website(1235, "https://alb-dach.dsde"));
        list.add(new Website(12356, "https://wolfssdsdperger-landmaschinen.de"));
        list.add(new Website(12367, "https://nagel-gsdsdruppe.de"));
        list.add(new Website(1236, "http://velte-steinmsdsdetz.de"));

        list.add(new Website(1245, "https://asdsdASlb-dach.de"));
        list.add(new Website(12456, "https://wolfspersdsdger-landmaschinen.de"));
        list.add(new Website(1255567, "https://nagasasesdsdl-gruppe.de"));
        list.add(new Website(1246, "http://velte-ASAssteinmsdsdetz.de"));

        list.add(new Website(1245, "https://asdssdASsddlb-dach.de"));
        list.add(new Website(12456, "https://wolfsdASAssdspersdssdsddger-landmaschinen.de"));
        list.add(new Website(1255567, "https://nagsASAsdsdesdsdl-gruppe.de"));
        list.add(new Website(1246, "http://velte-sdASAssdsteinmsdsdetz.de"));

        list.add(new Website(1245, "https://sdsdsd-ASASdach.de"));
        list.add(new Website(12456, "https://wolfspASAssdsdersdsdger-landmaschinen.de"));
        list.add(new Website(1253467, "https://nagsASAsdsdesdsdl-gruppe.de"));
        list.add(new Website(1246, "http://veltesdsASAsd-steinmsdsdetz.de"));

        list.add(new Website(1245, "https://asdssdsddlb-ASAsdach.de"));
        list.add(new Website(12456, "https://wolfsdsdsperASAssdsdger-landmaschinen.de"));
        list.add(new Website(12567, "https://nagessdsASAddsdl-gruppe.de"));
        list.add(new Website(1246, "http://velte-stsdsASASdeinmsdsdetz.de"));

        list.add(new Website(1245, "https://asdsdlbsdsdASAs-dach.de"));
        list.add(new Website(12456, "https://wolfspesdsASASdrsdsdger-landmaschinen.de"));
        list.add(new Website(125167, "https://nagesdssdsASAsASAsddl-gruppe.de"));
        list.add(new Website(1246, "http://velte-stessdASAsinmsdsdetz.de"));

        assertTrue(database.putWebsites(list));
    }

    @Test
    public void testInsertWord() {
        assertTrue(database.putWord(1, "abarbeitung"));
        assertTrue(database.putWord(2, "abbaubaren"));
        assertTrue(database.putWord(1, "abbestellung"));
        assertTrue(database.putWord(3, "abbestellung"));
        assertTrue(database.putWord(4, "abbestellung"));

        assertTrue(database.putWord(new Word(1, "abarbeitung")));
        assertTrue(database.putWord(new Word(2, "abbaubaren")));
        assertTrue(database.putWord(new Word(1, "abbestellung")));
        assertTrue(database.putWord(new Word(3, "abbestellung")));
        assertTrue(database.putWord(new Word(4, "abbestellung")));
    }

    @Test
    public void testInsertWords() {
        ArrayList<Word> words = new ArrayList<>();

        words.add(new Word(1, "abarbeitung"));
        words.add(new Word(2, "abbaubaren"));
        words.add(new Word(1, "abbestellung"));
        words.add(new Word(3, "abarbeitungsdsd"));
        words.add(new Word(4, "abarbeitungsdsd"));

        assertTrue(database.putWords(words));
    }

    @Test
    public void testClearWebsites() {
        assertTrue(database.clearWebsites());
    }

    @Test
    public void testClearWords() {
        assertTrue(database.clearWords());
    }

    @Test
    public void testWordsSize() {
        assertTrue(database.clearWords());
        assertEquals(0, database.getWordsSize());

        database.putWord(1, "test_word");
        assertEquals(1, database.getWordsSize());

        database.putWord(2, "2test_word");
        database.putWord(3, "3test_word");
        database.putWord(4, "4test_word");
        database.putWord(4, "4test_word");

        assertEquals(5, database.getWordsSize());
    }

    @Test
    public void testWebsitesSize() {
        assertTrue(database.clearWebsites());
        assertEquals(0, database.getWebsitesSize());

        database.putWebsite(1, "hhtlsdsd");
        assertEquals(1, database.getWebsitesSize());

        database.putWebsite(2, "sdasd");
        database.putWebsite(3, "sdsdsd");
        database.putWebsite(5, "sdsdasdasd");
        database.putWebsite(5, "sdsdasdasd");

        assertEquals(5, database.getWebsitesSize());
    }

    @Test
    public void testGetWebsites() {

    }
}
