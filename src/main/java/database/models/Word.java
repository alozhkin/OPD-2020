package database.models;

import java.util.*;

public class Word {

    private static int factoryId = 0;
    private static int foctoryDatabaseId = 0;
    private final int id;
    private final int websiteId;
    private final String word;
    private int databaseId = -1;

    public static Word newInstance(int websiteId, String word) {
        return new Word(++factoryId, websiteId, word);
    }

    public Word(int id, int websiteId, String word) {
        this.id = id;
        this.websiteId = websiteId;
        this.word = word;
    }

    public int getId() { return id; }

    public int getDatabaseId() {
        if (databaseId == -1) {
            databaseId = ++foctoryDatabaseId;
        }
        return databaseId;
    }

    public int getWebsiteId() {
        return websiteId;
    }

    public String getWord() {
        return word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word1 = (Word) o;
        return id == word1.id && websiteId == word1.websiteId &&
                Objects.equals(word, word1.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, websiteId, word);
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", websiteId=" + websiteId +
                ", word='" + word + '\'' +
                '}';
    }
}
