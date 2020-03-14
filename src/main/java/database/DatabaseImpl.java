package database;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DatabaseImpl implements Database {
    private Map<String, Integer> domainsIds;

    public DatabaseImpl(Map<String, Integer> domainsIds) {
        this.domainsIds = domainsIds;
    }


    @Override
    public void insertAll(Collection<String> words, String domain) {

    }
}
