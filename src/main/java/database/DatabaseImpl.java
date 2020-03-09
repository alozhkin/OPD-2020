package database;

import java.util.List;
import java.util.Map;

public class DatabaseImpl implements Database {
    private Map<String, Integer> domainsIds;

    public DatabaseImpl(Map<String, Integer> domainsIds) {
        this.domainsIds = domainsIds;
    }

    @Override
    public void insertAll(List<String> words, String domain) {
        // TODO
    }
}
