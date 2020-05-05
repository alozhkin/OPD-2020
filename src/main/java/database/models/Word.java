package database.models;

import java.util.*;

public class Word {

    private final int id;
    private final int websiteId;
    private final String word;

    public Word(int websiteId, String word) {
        this.id = -1;
        this.websiteId = websiteId;
        this.word = word;
    }

    public Word(int id, int websiteId, String word) {
        this.id = id;
        this.websiteId = websiteId;
        this.word = word;
    }

    public int getId() { return id; }

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

    public static class Factory {
        private static int id = -1;

        public static Word getWord(int websiteId, String word) {
            return new Word(++id, websiteId, word);
        }
    }
}
