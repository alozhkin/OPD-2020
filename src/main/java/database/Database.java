package database;

import database.models.Website;
import database.models.Word;

import java.util.Collection;
import java.util.Set;

public interface Database {

    /**
     * Creates a database instance
     *
     * @param databasePath target path in which the database should be located
     * @return database instance
     */
    static Database newInstance(String databasePath) {
        return new DatabaseImpl(databasePath);
    }

    static Database createDummy() {
        return new DummyDatabase();
    }

    /**
     * Parses a CSV file and inserts the result of parsing into a database
     *
     * @param CSVFile path to the CSV file
     * @return true, if the insert was successful,
     * false, if it was not possible to insert
     */
    boolean putWebsitesFromCSV(String CSVFile);

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
     * @param websites collection of websites
     * @return true, if the insert was successful,
     * false, if it was not possible to insert
     */
    boolean putWebsites(Collection<Website> websites);

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
     * @param words collection of words
     * @return true, if the insert was successful,
     * false, if it was not possible to insert
     */
    boolean putWords(Collection<Word> words);

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
     * Exports data from database and inserts it in the new CSV file
     *
     * @return true, if the export was successful,
     * false, if it was not possible to insert
     */
    boolean exportDataToCSV(String filepath);

    /**
     * Returns the number of entries in the "words" table
     *
     * @return Words table size
     */
    int getWordsSize();

    /**
     * Returns a set containing all the websites from the "websites" table
     *
     * @return Set of websites from websites table
     */
    Set<Website> getWebsites();

    /**
     * Returns a set containing the websites in which such word occurred from the "words" table
     *
     * @param word word by which to find websites
     * @return Set of Website objects
     */
    Set<Website> getWebsites(String word);

    /**
     * Returns a set of site objects containing the link of website and company ID
     *
     * @param companyId websites' company ID in "websites" table
     * @return set of website objects with website link and company id
     */
    Set<Website> getWebsites(int companyId);

    /**
     * Returns a set of string containing the links of websites by its company ID
     *
     * @param companyId websites' company ID in "websites" table
     * @return set of Strings with websites link
     */
    Set<String> getWebsiteLink(int companyId);

    /**
     * Returns a set containing all found words from "words" table
     *
     * @return set of Word objects
     */
    Set<Word> getWords();

    /**
     * Returns a set containing all found words from the specified website
     *
     * @param websiteId id of website
     * @return set of Word objects
     */
    Set<Word> getWords(int websiteId);

    /**
     * Returns a word ID from the "words" table
     *
     * @param word word which id is required
     * @return word ID
     */
    int getWordId(String word);

    /**
     * Returns a word object by its id in "words" table
     *
     * @param wordId id of required word
     * @return Word object
     */
    Word getWord(int wordId);
}
