package database;

import java.util.Collection;
import java.util.List;

public interface Database {
    void insertAll(Collection<String> words, String domain);
}
