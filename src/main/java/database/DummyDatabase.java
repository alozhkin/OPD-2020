package database;

import database.models.Website;
import database.models.Word;

import java.util.Collection;
import java.util.Set;

/**
 * Database that does nothing. Returns false instead boolean methods, 0 instead int, null instead Object.
 */
public class DummyDatabase implements Database {
    @Override
    public boolean putWebsitesFromCSV(String CSVFile) {
        return false;
    }

    @Override
    public boolean putWebsite(Website website) {
        return false;
    }

    @Override
    public boolean putWebsites(Collection<Website> websites) {
        return false;
    }

    @Override
    public boolean putWord(Word word) {
        return false;
    }

    @Override
    public boolean putWords(Collection<Word> words) {
        return false;
    }

    @Override
    public boolean clearWebsites() {
        return false;
    }

    @Override
    public boolean clearWords() {
        return false;
    }

    @Override
    public int getWebsitesSize() {
        return 0;
    }

    @Override
    public boolean exportDataToCSV(String filepath) {
        return false;
    }

    @Override
    public int getWordsSize() {
        return 0;
    }

    @Override
    public Set<Website> getWebsites() {
        return null;
    }

    @Override
    public Set<Website> getWebsites(String word) {
        return null;
    }

    @Override
    public Set<Website> getWebsites(int companyId) {
        return null;
    }

    @Override
    public Set<String> getWebsiteLink(int companyId) {
        return null;
    }

    @Override
    public Set<Word> getWords() {
        return null;
    }

    @Override
    public Set<Word> getWords(int websiteId) {
        return null;
    }

    @Override
    public int getWordId(String word) {
        return 0;
    }

    @Override
    public Word getWord(int wordId) {
        return null;
    }
}
