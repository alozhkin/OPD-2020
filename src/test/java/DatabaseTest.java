import database.Database;
import database.models.Website;
import database.models.Word;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
        List<Website> list = new ArrayList<>();

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
    public void testExportData() {
        assertTrue(database.clearWords());

        Word word1 = new Word(1, "word1");
        Word word2 = new Word(2, "word2");
        Word word3 = new Word(3, "word3");
        Word word5 = new Word(5, "word5");

        database.putWord(word1);
        database.putWord(word2);
        database.putWord(word3);
        database.putWord(word5);

        String csvpath = "src\\test\\resources\\actualExportData.csv";

        assertTrue(database.exportDataToCSV(csvpath));

        File file = new File(csvpath);
        try (FileReader fr = new FileReader(file)) {
            try (BufferedReader br = new BufferedReader(fr)) {
                String actualHeaderLine = br.readLine();
                String expectedHeaderLine = "\"id\";\"website_id\";\"word\"";
                assertEquals(expectedHeaderLine, actualHeaderLine);

                String tempLine;
                while ((tempLine = br.readLine()) != null) {         //reading every line and checking
                    String[] parsedLine = tempLine.split(";");// if current line in csv equal to the same line in database

                    int parsedLineId = Integer.parseInt(parsedLine[0]);
                    String parsedWord = parsedLine[2].replace("\"", "");
                    int parsedWebId = Integer.parseInt(parsedLine[1]);
                    Word testWord = new Word(parsedLineId, parsedWebId, parsedWord);
                    HashSet<Word> testSet = new HashSet<>();
                    testSet.add(testWord);

                    assertEquals(database.getWord(parsedLineId).getWord(), parsedWord);
                    assertEquals(testSet, database.getWords(parsedWebId));
                }

                //uncomment line below if you don't want to check output csv
                //file.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetWebsites() {
        assertTrue(database.clearWebsites());
        assertTrue(database.clearWords());

        Website site1 = new Website(1, "website1");
        Website site2 = new Website(2, "website2");
        Website site3 = new Website(3, "website3");
        Website site5 = new Website(5, "website5");

        database.putWebsite(site1);
        database.putWebsite(site2);
        database.putWebsite(site3);
        database.putWebsite(site5);

        Word word1 = new Word(1, "word1");
        Word word2 = new Word(2, "word2");
        Word word3 = new Word(3, "word3");
        Word word5 = new Word(5, "word5");

        database.putWord(word1);
        database.putWord(word2);
        database.putWord(word3);
        database.putWord(word5);

        HashSet<Website> testSet1 = new HashSet<>();
        HashSet<Website> testSet2 = new HashSet<>();
        HashSet<Website> testSet3 = new HashSet<>();

        testSet1.add(site1);
        testSet2.add(site1);
        testSet2.add(site2);
        testSet2.add(site3);
        testSet2.add(site5);
        testSet3.add(site3);

        assertEquals(testSet1, database.getWebsites("word1"));
        assertEquals(testSet1, database.getWebsites(1));
        assertEquals(testSet3, database.getWebsites("word3"));
        assertEquals(testSet3, database.getWebsites(3));
        assertEquals(testSet2, database.getWebsites());

        HashSet<String> testLink1 = new HashSet<>();
        testLink1.add("website3");

        HashSet<String> testLink2 = new HashSet<>();
        testLink2.add("website5");

        assertEquals(testLink1, database.getWebsiteLink(3));
        assertEquals(testLink2, database.getWebsiteLink(5));
    }

    @Test
    public void testGetWords() {
        assertTrue(database.clearWords());
        int size = database.getWebsitesSize();

        Word word1 = new Word(1, 1, "word1");
        Word word2 = new Word(2, 2, "word2");
        Word word31 = new Word(3, 3, "word31");
        Word word32 = new Word(4, 3, "word32");
        Word word5 = new Word(5, 5, "word5");

        database.putWord(word1);
        database.putWord(word2);
        database.putWord(word31);
        database.putWord(word32);
        database.putWord(word5);

        HashSet<Word> testSet1 = new HashSet<>();
        testSet1.add(word1);
        testSet1.add(word2);
        testSet1.add(word31);
        testSet1.add(word32);
        testSet1.add(word5);

        HashSet<Word> testSet2 = new HashSet<>();
        testSet2.add(word31);
        testSet2.add(word32);

        int word2Id = database.getWordId("word2");

        assertEquals(testSet1, database.getWords());
        assertEquals(testSet2, database.getWords(3));
        assertEquals(word2, database.getWord(word2Id));
    }
}