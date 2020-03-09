package database;

import database.models.Website;
import database.models.Word;

import java.util.List;

public interface Database {

    /**
     * Creates a database instance
     *
     * @return database instance
     */
    static Database newInstance() {
        return new DatabaseImpl();
    }

    /**
     * Parses a CSV file and inserts the result of parsing into a database
     *
     * @param csvFile path to the CSV file
     * @return true, if the insert was successful,
     * false, if it was not possible to insert
     */
    boolean putWebsitesFromCsv(String csvFile);

    /**
     * Inserts a website into the database,
     * if such an entry already exists, then does not insert anything
     *
     * @param companyId id of company
     * @param website   website link
     * @return true, if the insert was successful,
     * false, if it was not possible to insert
     */
    boolean putWebsite(int companyId, String website);

    /**
     * Inserts a website into the database,
     * if such an entry already exists, then does not insert anything
     *
     * @param website site object containing company id and link
     * @return true, if the insert was successful,
     * false, if it was not possible to insert
     */
    boolean putWebsite(Website website);

    /**
     * Inserts a websites into the database,
     * if such an entry already exists, then does not insert anything
     *
     * @param websites list of websites
     * @return true, if the insert was successful,
     * false, if it was not possible to insert
     */
    boolean putWebsites(List<Website> websites);

    /**
     * Inserts a word into the database,
     * if such an entry already exists, then does not insert anything
     *
     * @param websiteId id of website
     * @param word      word to insert
     * @return true, if the insert was successful,
     * false, if it was not possible to insert
     */
    boolean putWord(int websiteId, String word);

    /**
     * Inserts a word into the database,
     * if such an entry already exists, then does not insert anything
     *
     * @param word word object containing website id and word to insert
     * @return true, if the insert was successful,
     * false, if it was not possible to insert
     */
    boolean putWord(Word word);

    /**
     * Inserts a word into the database,
     * if such an entry already exists, then does not insert anything
     *
     * @param words list of words
     * @return true, if the insert was successful,
     * false, if it was not possible to insert
     */
    boolean putWords(List<Word> words);

    /**
     * Clears all columns of the 'websites' table
     *
     * @return true, if the cleaning was successful,
     * false, if it was not possible to clean
     */
    boolean clearWebsites();

    /**
     * Clears all columns of the 'words' table
     *
     * @return true, if the cleaning was successful,
     * false, if it was not possible to clean
     */
    boolean clearWords();

    /**
     * Returns the number of entries in the "websites" table
     *
     * @return Websites table size
     */
    int getWebsitesSize();

    /**
     * Returns the number of entries in the "words" table
     *
     * @return Words table size
     */
    int getWordsSize();

    /* TODO

    List<Website> getWebsites();
    List<Website> getWebsites(String word);

    String getWebsiteLink(int companyId);
    Website getWebsite(int companyId);

    List<Word> getWords();
    List<Word> getWords(int websiteId);

    Word getWord(int wordId);

    */
}
