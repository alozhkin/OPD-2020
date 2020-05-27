package database;

import config.ConfigurationUtils;
import database.models.Website;
import database.models.Word;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Link;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTest {

    private Database database;

    @BeforeAll
    public static void configure() {
        ConfigurationUtils.configure();
    }

    @BeforeEach
    public void initDb() {
        database = Database.newInstance("websites.db");
    }

    @Test
    void putWebsitesFromCSV() {
        assertTrue(database.putWebsitesFromCSV("websites_data.csv"));
    }

    @Test
    public void testInsertWebsite() {
        assertTrue(database.putWebsite(new Website(1233, new Link("https://alb-dach.de"))));
        assertTrue(database.putWebsite(new Website(344, new Link("https://wolfsperger-landmaschinen.de"))));
        assertTrue(database.putWebsite(new Website(75, new Link("https://nagel-gruppe.de"))));
        assertTrue(database.putWebsite(new Website(6, new Link("http://velte-steinmetz.de"))));

        assertTrue(database.putWebsite(new Website(1233, new Link("https://aalb-dach.de"))));
        assertTrue(database.putWebsite(new Website(344, new Link("https://wwolfsperger-landmaschinen.de"))));
        assertTrue(database.putWebsite(new Website(75, new Link("https://nnagel-gruppe.de"))));
        assertTrue(database.putWebsite(new Website(6, new Link("http://vvelte-steinmetz.de"))));
    }

    @Test
    public void testInsertWebsites() {
        List<Website> list = new ArrayList<>();

        list.add(new Website(12345, new Link("https://alb-dach.de")));
        list.add(new Website(123456, new Link( "https://wolfsperger-landmaschinen.de")));
        list.add(new Website(123567,  new Link("https://nagel-gruppe.de")));
        list.add(new Website(12346,  new Link("http://velte-steinmetz.de")));

        list.add(new Website(1235,  new Link("https://alb-dach.dsde")));
        list.add(new Website(12356,  new Link("https://wolfssdsdperger-landmaschinen.de")));
        list.add(new Website(12367,  new Link("https://nagel-gsdsdruppe.de")));
        list.add(new Website(1236,  new Link("http://velte-steinmsdsdetz.de")));

        assertTrue(database.putWebsites(list));
    }

    @Test
    public void testInsertWord() {
        assertTrue(database.putWord(Word.newInstance(1, "abarbeitung")));
        assertTrue(database.putWord(Word.newInstance(2, "abbaubaren")));
        assertTrue(database.putWord(Word.newInstance(1, "abbestellung")));
        assertTrue(database.putWord(Word.newInstance(3, "abbestellung")));
        assertTrue(database.putWord(Word.newInstance(4, "abbestellung")));
                                
        assertTrue(database.putWord(Word.newInstance(1, "abarbeitung")));
        assertTrue(database.putWord(Word.newInstance(2, "abbaubaren")));
        assertTrue(database.putWord(Word.newInstance(1, "abbestellung")));
        assertTrue(database.putWord(Word.newInstance(3, "abbestellung")));
        assertTrue(database.putWord(Word.newInstance(4, "abbestellung")));
    }

    @Test
    public void testInsertWords() {
        ArrayList<Word> words = new ArrayList<>();

        words.add(Word.newInstance(1, "abarbeitung"));
        words.add(Word.newInstance(2, "abbaubaren"));
        words.add(Word.newInstance(1, "abbestellung"));
        words.add(Word.newInstance(3, "abarbeitungsdsd"));
        words.add(Word.newInstance(4, "abarbeitungsdsd"));

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

        database.putWord(Word.newInstance(1, "test_word"));
        assertEquals(1, database.getWordsSize());

        database.putWord(Word.newInstance(2, "2test_word"));
        database.putWord(Word.newInstance(3, "3test_word"));
        database.putWord(Word.newInstance(4, "4test_word"));
        database.putWord(Word.newInstance(4, "4test_word"));

        assertEquals(5, database.getWordsSize());
    }

    @Test
    public void testWebsitesSize() {
        assertTrue(database.clearWebsites());
        assertEquals(0, database.getWebsitesSize());

        database.putWebsite(new Website(1, new Link("hhtlsdsd")));
        assertEquals(1, database.getWebsitesSize());

        database.putWebsite(new Website(2, new Link("sdasd")));
        database.putWebsite(new Website(3, new Link("sdsdsd")));
        database.putWebsite(new Website(5, new Link("sdsdasdasd")));
        database.putWebsite(new Website(5, new Link("sdsdasdasd")));

        assertEquals(5, database.getWebsitesSize());
    }

    @Test
    public void testExportData() {
        assertTrue(database.clearWords());

        Word word1 = Word.newInstance(1, "word1");
        Word word2 = Word.newInstance(2, "word2");
        Word word3 = Word.newInstance(3, "word3");
        Word word5 = Word.newInstance(5, "word5");

        database.putWord(word1);
        database.putWord(word2);
        database.putWord(word3);
        database.putWord(word5);

        String csvpath = "src/test/resources/actualExportData.csv";

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

        Website site1 = new Website(1,  new Link("website1"));
        Website site2 = new Website(2,  new Link("website2"));
        Website site3 = new Website(3,  new Link("website3"));
        Website site5 = new Website(5,  new Link("website5"));

        database.putWebsite(site1);
        database.putWebsite(site2);
        database.putWebsite(site3);
        database.putWebsite(site5);

        Word word1 = Word.newInstance(1, "word1");
        Word word2 = Word.newInstance(2, "word2");
        Word word3 = Word.newInstance(3, "word3");
        Word word5 = Word.newInstance(5, "word5");

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
        testLink1.add("http://website3");

        HashSet<String> testLink2 = new HashSet<>();
        testLink2.add("http://website5");

        assertEquals(testLink1, database.getWebsiteLink(3));
        assertEquals(testLink2, database.getWebsiteLink(5));
    }

    @Test
    public void testGetWords() {
        assertTrue(database.clearWords());
        int size = database.getWebsitesSize();

        Word word1 = Word.newInstance(1, "word1");
        Word word2 = Word.newInstance(2, "word2");
        Word word31 = Word.newInstance(3, "word31");
        Word word32 = Word.newInstance(3, "word32");
        Word word5 = Word.newInstance(5, "word5");

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