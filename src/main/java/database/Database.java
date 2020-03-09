package database;

import java.util.List;

public interface Database {
    void insertAll(List<String> words, String domain);
}
