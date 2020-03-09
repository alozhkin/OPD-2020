package database.models;

import java.util.Objects;

public class Word {
    private final int websiteId;
    private final String word;

    public Word(int websiteId, String word) {
        this.websiteId = websiteId;
        this.word = word;
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
        return websiteId == word1.websiteId &&
                Objects.equals(word, word1.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(websiteId, word);
    }
}
